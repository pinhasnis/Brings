package com.example.some_lie.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "eventApi",
        version = "v1",
        resource = "event",
        namespace = @ApiNamespace(
                ownerDomain = "backend.some_lie.example.com",
                ownerName = "backend.some_lie.example.com",
                packagePath = ""
        )
)
public class EventEndpoint {

    private static final Logger logger = Logger.getLogger(EventEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Event.class);
    }

    /**
     * Returns the {@link Event} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Event} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "event/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Event get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Event with ID: " + id);
        Event event = ofy().load().type(Event.class).id(id).now();
        if (event == null) {
            throw new NotFoundException("Could not find Event with ID: " + id);
        }
        return event;
    }

    /**
     * Inserts a new {@code Event}.
     */
    @ApiMethod(
            name = "insert",
            path = "event",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Event insert(@Named("ID")String id, @Named("Name")String name) {
        String url = null;
        try {
            Class.forName("com.mysql.jdbc.GoogleDriver");
            url =
                    "jdbc:google:mysql://encoded-keyword-106406:test/datdbase1?user=root";


            //        d.setFrom(url);
            Connection conn = DriverManager.getConnection(url);
            //String query = "CREATE TABLE `Test`(`from` VARCHAR(50),`to` VARCHAR(50),`message` VARCHAR(500));";
            String query = "CREATE TABLE `Events`(`id` VARCHAR(50),`name` VARCHAR(50));";
            String query2 = "insert into Events values('"+id+"','"+name+"');";
            //conn.createStatement().execute("DROP TABLE `Test`;");
            //boolean rs2 = conn.createStatement().execute(query);
            conn.createStatement().execute(query);
            //boolean rs = conn.createStatement().execute(query2);

        }catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
        }
        return null;
    }


    /**
     * Updates an existing {@code Event}.
     *
     * @param id    the ID of the entity to be updated
     * @param event the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Event}
     */
    @ApiMethod(
            name = "update",
            path = "event/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Event update(@Named("id") Long id, Event event) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(event).now();
        logger.info("Updated Event: " + event);
        return ofy().load().entity(event).now();
    }

    /**
     * Deletes the specified {@code Event}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Event}
     */
    @ApiMethod(
            name = "remove",
            path = "event/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Event.class).id(id).now();
        logger.info("Deleted Event with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "event",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Event> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Event> query = ofy().load().type(Event.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Event> queryIterator = query.iterator();
        List<Event> eventList = new ArrayList<Event>(limit);
        while (queryIterator.hasNext()) {
            eventList.add(queryIterator.next());
        }
        return CollectionResponse.<Event>builder().setItems(eventList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Event.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Event with ID: " + id);
        }
    }
}