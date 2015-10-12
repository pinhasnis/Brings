/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.example.some_lie.backend.apis;

import com.example.some_lie.backend.models.RegistrationRecord;
import com.example.some_lie.backend.utils.EndpointUtil;
import com.example.some_lie.backend.utils.MySQL_Util;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.example.some_lie.backend.OfyService.ofy;

/**
 * A registration endpoint class we are exposing for a device's GCM registration id on the backend
 * <p/>
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 * <p/>
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */

@Api(name = "brings", version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = com.example.some_lie.backend.Constants.API_OWNER,
                ownerName = com.example.some_lie.backend.Constants.API_OWNER,
                packagePath = com.example.some_lie.backend.Constants.API_PACKAGE_PATH
        )
)
@ApiClass(resource = "registration",
        clientIds = {
                com.example.some_lie.backend.Constants.ANDROID_CLIENT_ID,
                com.example.some_lie.backend.Constants.IOS_CLIENT_ID,
                com.example.some_lie.backend.Constants.WEB_CLIENT_ID},
        audiences = {com.example.some_lie.backend.Constants.AUDIENCE_ID}
)
public class RegistrationEndpoint {

    private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

    /**
     * Register a device to the backend
     */

    @ApiMethod(name = "Register", httpMethod = "POST")
    public RegistrationRecord registerDevice(@Named("mail") String mail, @Named("name") String name, @Named("phone") String phone
            , @Named("password") String password, @Named("regId") String regId) {// throws UnauthorizedException {
        // EndpointUtil.throwIfNotAuthenticated(user);
        RegistrationRecord record = new RegistrationRecord();
        String isExist = checkIfUserExist(mail);
        if (isExist.equals("O.K")) {
            try {
                MySQL_Util.insert("Users", new String[]{mail, name, phone, password});
                MySQL_Util.insert("UsersDevices", new String[]{mail, regId});
                record.setRegistration_message("O.K");
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                record.setRegistration_message(sw.toString());
            }

        } else {
            record.setRegistration_message("User alrady exist!");
        }
    //    record.setRegistration_message(isExist);
        return record;
    }

    private String checkIfUserExist(String user) {
        String debug = "O.K";
        boolean isUserExist = true;
        try {
            ResultSet rs = MySQL_Util.select(null,"Users",new String[]{"email"},new String[]{user},new int[]{1});
            if (!rs.next()) {
                isUserExist = false;
            }
            rs.close();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            debug = sw.toString();
        }
        return debug;//isUserExist;
    }

    private boolean checkIfUserExist(String user, String pass) {
        boolean isUserExist = true;
        try {
            ResultSet rs = MySQL_Util.select(null,"Users",new String[]{"email","password"},new String[]{user,pass},new int[]{1});
            if (!rs.next()) {
                isUserExist = false;
            }
            rs.close();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
        }
        return isUserExist;
    }

    @ApiMethod(name = "authentication", httpMethod = "POST")
    public RegistrationRecord authentication(@Named("Email_OR_Phone")String user, @Named("Password")String pass,
                                             @Named("regId") String regId ) {
        RegistrationRecord record = null;
        try {
            String email = "";
            ResultSet rs = MySQL_Util.select(null,"Users",new String[]{"email","password"},new String[]{user,pass},new int[]{1});
            if (rs.next()) {
                record = new RegistrationRecord();
                rs.close();
                email = user;
            }
            else{
                rs = MySQL_Util.select(null,"Users",new String[]{"phone","password"},new String[]{user,pass},new int[]{1});
                if (rs.next()) {
                    email = rs.getString(1);
                    record = new RegistrationRecord();
                }
            }
            rs.close();
            if(record != null){
                record.setMail(email);
                rs = MySQL_Util.select(new String[]{"reg_id"}, "UsersDevices",new String[]{"email"},new String[]{email},new int[]{1});
                boolean done = false;
                while(rs.next() && !done){
                    if(regId.equals(rs.getString(1))){
                        done = true;
                    }
                }
                rs.close();
                if(!done){
                    MySQL_Util.insert("UsersDevices", new String[]{email, regId});
                }
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
        }
        return record;
    }

    /**
     * Unregister a device from the backend
     */
    @ApiMethod(name = "Unregister", httpMethod = "DELETE")
    public RegistrationRecord unregisterDevice(@Named("mail") String mail, @Named("password") String password) {
        RegistrationRecord record = new RegistrationRecord();
        if (checkIfUserExist(mail, password)) {
            try {
                MySQL_Util.delete("Users",new String[]{"email"},new String[]{mail},new int[]{1});
                record.setRegistration_message("User was deleted");

            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
            }
        } else {
            record.setRegistration_message("email or password are wrong!");
        }
        return record;
    }
}
