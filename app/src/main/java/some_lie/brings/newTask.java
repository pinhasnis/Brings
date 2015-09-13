package some_lie.brings;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by pinhas on 08/09/2015.
 */
public class newTask extends AppCompatActivity {

    private EditText et_nt_task_ui;
    private EditText et_nt_description_ui;
    private Button bt_nt_create_task_ui;
    private SQLiteDatabase db;
    private int ID;
    private String imagePath = "";
    private String USERNAME = "user 1";//TODO


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task);
        et_nt_task_ui = (EditText)findViewById(R.id.et_nt_task_ui);
        et_nt_description_ui = (EditText)findViewById(R.id.et_nt_description_ui);
        bt_nt_create_task_ui = (Button)findViewById(R.id.bt_nt_create_task_ui);
        Bundle b = getIntent().getExtras();
        ID = b.getInt("ID");
        USERNAME = b.getString("USERNAME");
        bt_nt_create_task_ui.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean ok = saveData();
                if (ok) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "only description can by empty..", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private boolean saveData(){
        boolean ok = false;
        if(et_nt_task_ui.getText().length() > 0) {
            ok = true;
            db = openOrCreateDatabase("_edata", MODE_PRIVATE, null);
            int id = 0;
            String key = USERNAME + " - "+id;
            String task = et_nt_task_ui.getText().toString();
            String description = et_nt_description_ui.getText().toString();

            db.execSQL("insert into Tasks values('"+key+"','" + task + "','" + description+"');");
            db.close();
        }
        return ok;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }

}
