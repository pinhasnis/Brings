package brings_app;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.some_lie.backend.brings.Brings;
import com.example.some_lie.backend.brings.model.Event;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;
import java.util.List;

import server.CloudEndpointBuilderHelper;
import server.Messageing.GcmIntentService;
import server.ServerAsyncResponse;
import utils.Constans.Constants;
import utils.Constans.Table_Events;
import utils.Helper;
import utils.bitmapHelper;
import utils.sqlHelper;

public class MainActivity extends AppCompatActivity implements ServerAsyncResponse{

    /**
     * Time limit for the application to wait on a response from Play Services.
     */
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String MY_PREFS_NAME = "Brings";

    private ImageButton ibAdd;
    private TextView tvSearch;
    private SearchView search;
    private static ArrayList<String> users_names;
    private static ArrayList<Integer> IDS;
    private static final String PROPERTY_REG_ID = "registrationId";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private Brings BringsApi;
    private Event event;
    private String mode_view;
    /**
     * Google Cloud Messaging API.
     */
    private GoogleCloudMessaging gcm;

    /**
     * The registration ID.
     */
    private String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GcmIntentService.delegate = this;
        BringsApi = CloudEndpointBuilderHelper.getEndpoints();
        users_names = new ArrayList<>();
        IDS = new ArrayList<>();
        tvSearch = (TextView) findViewById(R.id.tvSearch);
        ListView listview;
        //ibAdd = (ImageButton) findViewById(R.id.ibAdd);
       // search = (SearchView) findViewById(R.id.searchView);
       // setOnClick();
       // tvSearch.setText("Search  ");
        listview = (ListView) findViewById(R.id.lvMain);
        mode_view = Constants.Big_List_View;
        setList();
    }



    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     * @param applicationContext the Application context.
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(final Context applicationContext) {
        final SharedPreferences prefs = getGCMPreferences(getApplicationContext());
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
     //       Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs
                .getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(getApplicationContext());
        if (registeredVersion != currentVersion) {
       //     Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    /**
     * @param applicationContext the Application context.
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(final Context
                                                        applicationContext) {
        // This sample app persists the registration ID in shared preferences,
        // but how you store the registration ID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Returns the application version.
     * @param context the Application context.
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(final Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    private void sql() {


        ArrayList<String>[] sqlresult = sqlHelper.select(null, Table_Events.Table_Name, null, null, null);
        for (String str : sqlresult[0]){
            String[] s = str.split(" - ");
            users_names.add(s[0]);
            IDS.add(Integer.parseInt(s[1]));
        }

    }

    /**
     * Checks if Google Play Services are installed and if not it initializes
     * opening the dialog to allow user to install Google Play Services.
     * @return a boolean indicating if the Google Play Services are available.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
/*
    private void setOnClick() {

        final Intent new_event = new Intent(this, newEvent.class);

        ibAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new_event);
            }

        });

        tvSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                search.setIconified(false);
            }

        });
    }
    */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuItem item = (MenuItem)menu.findItem(R.id.spinner_view);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter() {
            String[] name = new String[]{"s","ss"};
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = new TextView(getApplicationContext());
                textView.setText(name[position]);
                return textView;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public int getCount() {
                return name.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = new TextView(getApplicationContext());
                textView.setText(name[position]);
                return textView;
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };
        //spinner.setAdapter(spinnerAdapter); // set the adapter to provide layout of rows and content
        List<String> list = new ArrayList<String>();
        list.add("normal");
        list.add("list");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinner.setDropDownVerticalOffset(10);
        }
        spinner.setOnItemSelectedListener(new SpinnerOnItemSelectedListener());
        return true;
    }

    public class SpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            switch (pos) {
                case 0: {
                    mode_view = Constants.Big_List_View;
                    setList();
                    break;
                }
                case 1:{
                    mode_view = Constants.Small_List_View;
                    setList();
                    break;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_add) {
            final Intent new_event = new Intent(this, newEvent.class);
            startActivity(new_event);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        setList();
    }

    public void setList() {
        users_names.clear();
        IDS.clear();
        sql();

        final ListView listview = (ListView) findViewById(R.id.lvMain);
        listview.setClickable(true);
        final Intent tabs =  new Intent(this,tab.class);
        final Context context = this;
        switch (mode_view){
            case Constants.Small_List_View:{
                StableArrayAdapter_small_view adapter = new StableArrayAdapter_small_view(this);
                listview.setAdapter(adapter);
                break;
            }
            case Constants.Big_List_View:{
                StableArrayAdapter_big_view adapter = new StableArrayAdapter_big_view(this);
                listview.setAdapter(adapter);

                break;
            }
        }

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
                                           final int pos, final long id) {
                // TODO Auto-generated method stub
                final String Event_ID = users_names.get(pos) + " - " + IDS.get(pos);
                String permission = Helper.getMyPermission(Event_ID);
                if (permission.equals(Constants.Manager)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Delete Event?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Helper.Delete_Event_ServerSQL(context, Event_ID);
                                    Helper.Delete_Event_MySQL(Event_ID);
                                    setList();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                } else {
                    Toast.makeText(context, "Only manager can delete event", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             *  starts the Register class for specific course when clicked on in the list
             */
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                Bundle data = new Bundle();
                //data.putInt("ID", IDS.get(position));
                //data.putString("USERNAME", users_names.get(position));
                data.putString("Event_ID", users_names.get(position) + " - " + IDS.get(position));
                tabs.putExtras(data);
                startActivity(tabs);
            }
        });
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     * @param applicationContext application's context.
     * @param registrationId     registration ID
     */
    private void storeRegistrationId(final Context applicationContext,
                                     final String registrationId) {
        final SharedPreferences prefs = getGCMPreferences(applicationContext);
        int appVersion = getAppVersion(applicationContext);
     //   Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, registrationId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    @Override
    public void processFinish(String... output) {
        if(output[0].equals(Constants.Update_Activity)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setList();
                }
            });

        }
    }



    private static class StableArrayAdapter_small_view extends BaseAdapter implements View.OnClickListener {

        private Context context;

        public StableArrayAdapter_small_view(Context context) {
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup viewGroup) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_main_small_list_item, null);

            ImageView iv = (ImageView) convertView.findViewById(R.id.ivPic);
            TextView tvName = (TextView) convertView.findViewById(R.id.tv_ambli_name);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tv_ambli_date);
            ArrayList<String>[] dbEvent = sqlHelper.select(null, Table_Events.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{users_names.get(position) + " - " + IDS.get(position)}, new int[]{1});
            tvName.setText(dbEvent[Table_Events.parseInt(Table_Events.Name)].get(0));
            tvDate.setText(dbEvent[Table_Events.parseInt(Table_Events.Start_Date)].get(0));
            String Image_Path = dbEvent[Table_Events.parseInt(Table_Events.Image_Path)].get(0);
            Bitmap bitmap = bitmapHelper.decodeSampledBitmapFromFile(Image_Path, 100, 100);
            if (bitmap!=null) {
               // RoundedBitmapDrawable img = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                //img.setCircular(true);
                //iv.setImageDrawable(img);
            }
            iv.setImageBitmap(bitmap);

            return convertView;
        }

        public int getCount() {
            return IDS.size();
        }

        @Override
        public Object getItem(int position) {
            String s = users_names.get(position)+" - "+IDS.get(position);
            return s;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onClick(View v) {

        }
    }

    private static class StableArrayAdapter_big_view extends BaseAdapter implements View.OnClickListener {

        private Context context;

        public StableArrayAdapter_big_view(Context context) {
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_main_big_list_item, null);

            ImageView iv = (ImageView) convertView.findViewById(R.id.iv_ambli_image);
            TextView tvName = (TextView) convertView.findViewById(R.id.tv_ambli_name);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tv_ambli_date);
            ArrayList<String>[] dbEvent = sqlHelper.select(null, Table_Events.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{users_names.get(position) + " - " + IDS.get(position)}, new int[]{1});
            tvName.setText(dbEvent[Table_Events.parseInt(Table_Events.Name)].get(0));
            tvDate.setText(dbEvent[Table_Events.parseInt(Table_Events.Start_Date)].get(0));
            String Image_Path = dbEvent[Table_Events.parseInt(Table_Events.Image_Path)].get(0);
            Bitmap bitmap = bitmapHelper.decodeSampledBitmapFromFile(Image_Path, 100, 100);
            if (bitmap!=null) {
                // RoundedBitmapDrawable img = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                //img.setCircular(true);
                //iv.setImageDrawable(img);
            }
            iv.setImageBitmap(bitmap);

            return convertView;
        }

        public int getCount() {
            return IDS.size();
        }

        @Override
        public Object getItem(int position) {
            String s = users_names.get(position)+" - "+IDS.get(position);
            return s;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onClick(View v) {

        }
    }


}
