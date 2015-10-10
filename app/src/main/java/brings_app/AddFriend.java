package brings_app;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by pinhas on 19/09/2015.
 */
public class AddFriend extends AppCompatActivity {

    private TextView Name;
    private EditText input;
    private Button add;
    private SQLiteDatabase db;
    private String KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);
        Name = (TextView)findViewById(R.id.tv_addFriend);
        input = (EditText)findViewById(R.id.et_addFriend);
        add = (Button)findViewById(R.id.bt_addFriend);
        Bundle b = getIntent().getExtras();

        KEY = b.getString("KEY");
        add.setOnClickListener(new View.OnClickListener() {

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
        if(input.getText().length() > 0) {
            ok = true;
            db = openOrCreateDatabase("_edata", MODE_PRIVATE, null);

            String name = input.getText().toString();
            db.execSQL("insert into Attending values('"+KEY+"','" + name + "');");
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