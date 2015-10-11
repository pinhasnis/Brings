package server;

import android.content.Context;
import android.os.AsyncTask;

import com.example.some_lie.backend.brings.Brings;
import com.example.some_lie.backend.brings.model.RegistrationRecord;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import brings_app.Constants;

/**
 * Created by pinhas on 11/10/2015.
 */
public class Registration_AsyncTask extends AsyncTask<String, Void, RegistrationRecord> {
    private static Brings myApiService = null;
    private RegistrationAsyncResponse delegate;
    private GoogleCloudMessaging gcm;
    private Context context;

    public Registration_AsyncTask(RegistrationAsyncResponse delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected RegistrationRecord doInBackground(String... params) {
        if(myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }
        try {
            String msg = "";
               if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                String regId = gcm.register(Constants.SENDER_ID);

            return myApiService.register(params[0], params[1], params[2], params[3], regId).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(RegistrationRecord result) {
        String message = "Cannot connect the server.. please try again later";
        if(result != null) {
            message = result.getMessage();
        }
        delegate.processFinish(message);
    }
}