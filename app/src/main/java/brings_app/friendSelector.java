package brings_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import utils.Constans.Constants;
import utils.Constans.Table_Users;
import utils.sqlHelper;

/**
 * Created by pinhas on 15/10/2015.
 */
public class friendSelector extends AppCompatActivity{
    private ListView lv;
    private ArrayList<String> Users[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_selctor);
        lv = (ListView)findViewById(R.id.lvFriendsSelctor);
        lv.setClickable(true);
        fillList();
        StableArrayAdapter adapter = new StableArrayAdapter(this);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                Intent _result = new Intent();
                Bundle b = new Bundle();
                b.putString(Table_Users.User_ID, Users[0].get(position));
                b.putString(Table_Users.Nickname, Users[2].get(position));
                _result.putExtras(b);
                setResult(Activity.RESULT_OK, _result);
                finish();
            }
        });

    }

    private void fillList(){
        Users = sqlHelper.select(null, Table_Users.Table_Name, new String[]{Table_Users.Register}, new String[]{Constants.Yes}, null);
        ArrayList<String>[] temp = sqlHelper.select(null, Table_Users.Table_Name, new String[]{Table_Users.Register}, new String[]{Constants.No}, null);
        for (int i = 0; i < temp[0].size(); i++){
            for (int j = 0; j < temp.length; j++){
                Users[j].add(temp[j].get(i));
            }
        }
    }


    private class StableArrayAdapter extends BaseAdapter implements View.OnClickListener {

        private Context context;

        public StableArrayAdapter(Context context) {
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend_item_list, null);

            TextView tvName = (TextView) convertView.findViewById(R.id.tvFriendItemList);
            tvName.setText(Users[0].get(position));
            if(Users[3].get(position).equals(Constants.Yes)) {
                tvName.setBackgroundColor(Color.GREEN);
            }
            else{
                tvName.setBackgroundColor(Color.GRAY);
            }
            return convertView;
        }

        public int getCount() {
            return Users[0].size();
        }

        @Override
        public Object getItem(int position) {
            return Users[0].get(position);
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
