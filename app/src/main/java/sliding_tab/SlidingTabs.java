/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sliding_tab;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import brings_app.AddFriend;
import brings_app.R;
import brings_app.Task;
import brings_app.newTask;
import server.Chat.Chat_AsyncTask_insert;
import server.Event_User.EventUser_AsyncTask_UpdateAttending;
import server.Event_User.EventUser_AsyncTask_delete;
import server.Messageing.SendMessage_AsyncTask;
import server.Task.Task_AsyncTask_delete;
import server.Task.Task_AsyncTask_update;
import utils.Constans.Constants;
import utils.Constans.Table_Chat;
import utils.Constans.Table_Events;
import utils.Constans.Table_Events_Users;
import utils.Constans.Table_Tasks;
import utils.Constans.Table_Users;
import utils.Helper;
import utils.sqlHelper;

public class SlidingTabs extends Fragment {


    private int layouts[] = {R.layout.event_main, R.layout.event_attending, R.layout.event_todo, R.layout.event_chat};
    private String[] tabName= {"MAIN", "ATTENDING", "TODO", "CHAT"};
    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    public int getCurrentItem(){
        return mViewPager.getCurrentItem();
    }

    // BEGIN_INCLUDE (fragment_onviewcreated)
    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of {@link SamplePagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        // END_INCLUDE (setup_viewpager)
        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        Bundle b = getArguments();
        int currentView = b.getInt("view_num");
        mViewPager.setCurrentItem(currentView);
        // END_INCLUDE (setup_slidingtablayout)
    }
    // END_INCLUDE (fragment_onviewcreated)

    /**
     * The {@link PagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link SlidingTabLayout}.
     */
    class SamplePagerAdapter extends PagerAdapter {

        private String Event_ID = "";
        ArrayList<Integer> Tasks_keys = new ArrayList<>();
        ArrayList<String> members_keys = new ArrayList<>();
        ArrayList<String> chat_keys = new ArrayList<>();
        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return layouts.length;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return tabName[position];
        }
        // END_INCLUDE (pageradapter_getpagetitle)

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a add_friend from the apps resources and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new add_friend from our resources
            View view = getActivity().getLayoutInflater().inflate(layouts[position],
                    container, false);
            if(Event_ID.equals("")){
                Bundle b = getArguments();
                Event_ID = b.getString("Event_ID");
            }
            ArrayList<String>[] dbEvents = sqlHelper.select(null, Table_Events.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{Event_ID}, null);
            if(dbEvents[0].isEmpty()) {
                getActivity().finish();
            }else {
                switch (position) {
                    case 0: {
                        setMainTab(view);
                        break;
                    }
                    case 1: {
                        setAttendingTab(view);
                        break;
                    }
                    case 2: {
                        setTodoTab(view);
                        break;
                    }
                    case 3: {
                        setChatTab(view);
                        break;
                    }
                }

                // Add the newly created View to the ViewPager
                container.addView(view);
            }
            return view;
        }
        private void setMainTab(View view){
            ArrayList<String>[] dbEvent = sqlHelper.select(null, Table_Events.Table_Name,new String[]{Table_Events.Event_ID},new String[]{Event_ID},new int[]{1});
            TextView name = (TextView) view.findViewById(R.id.tv_em_name_ui);
            TextView location = (TextView) view.findViewById(R.id.tv_em_place_ui);
            TextView start_date = (TextView) view.findViewById(R.id.tv_em_start_ui);
            TextView end_date = (TextView) view.findViewById(R.id.tv_em_end_ui);
            TextView description = (TextView) view.findViewById(R.id.tv_em_description_ui);
            name.setText(dbEvent[Table_Events.parseInt(Table_Events.Name)].get(0));
            location.setText(dbEvent[Table_Events.parseInt(Table_Events.Location)].get(0));
            start_date.setText(dbEvent[Table_Events.parseInt(Table_Events.Start_Date)].get(0));
            end_date.setText(dbEvent[Table_Events.parseInt(Table_Events.End_Date)].get(0));
            description.setText(dbEvent[Table_Events.parseInt(Table_Events.Description)].get(0));
            }
        private void setAttendingTab(final View view){
            ImageButton addFriend = (ImageButton) view.findViewById(R.id.ib_ea_add_friend);
            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String permission = Helper.getMyPermission(Event_ID);
                    if(!permission.equals(Constants.Participant)) {
                        final Intent addFriend = new Intent(getActivity(), AddFriend.class);
                        Bundle data = new Bundle();
                        data.putString("Event_ID", Event_ID);
                        addFriend.putExtras(data);
                        getArguments().putInt("from", 1);
                        startActivityForResult(addFriend, 1);
                    }else{
                        Toast.makeText(getContext(), "Participant can't add friend", Toast.LENGTH_LONG).show();
                    }
                }
            });
            setAttendingList(view);

        }
        private void setTodoTab(View view){
            setTodoList(view);
            ImageButton bt_etd_add_task = (ImageButton) view.findViewById(R.id.bt_etd_add_task);
            bt_etd_add_task.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String permission = Helper.getMyPermission(Event_ID);
                    if(!permission.equals(Constants.Participant)) {
                        final Intent task = new Intent(getActivity().getApplicationContext(), newTask.class);
                        Bundle data = new Bundle();
                        data.putString("Event_ID", Event_ID);
                        task.putExtras(data);
                        getArguments().putInt("from", 2);
                        startActivityForResult(task, 2);
                    }else{
                        Toast.makeText(getContext(), "Participant can't add task", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        private void setChatTab(final View view){
            setChatList(view);
            Button bt_chat_post_ui = (Button) view.findViewById(R.id.bt_chat_post_ui);
            bt_chat_post_ui.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText et_chat_message_ui = (EditText) view.findViewById(R.id.et_chat_message_ui);
                    String chat_message = et_chat_message_ui.getText().toString();
                    if (chat_message.length() > 0) {
                        String Chat_ID = Table_Chat.Table_Name + Helper.Clean_Event_ID(Event_ID);
                        int id = 0;
                        String message_ID = Constants.MY_User_ID + " - " + id;
                        ArrayList<String> allIDS = new ArrayList<>();
                        ArrayList<String>[] dbResult = sqlHelper.select(null, Chat_ID, new String[]{Table_Chat.User_ID}, new String[]{Constants.MY_User_ID}, null);
                        for (String t_id : dbResult[0]) {
                            allIDS.add(t_id);
                        }
                        while (allIDS.contains(message_ID)) {
                            id++;
                            message_ID = Constants.MY_User_ID + " - " + id;
                        }
                        String date = Helper.getCurrentDate();
                        String time = Helper.getCurrentTime();
                        sqlHelper.insert(Chat_ID, new String[]{message_ID, Constants.MY_User_ID, chat_message, date, time});
                        new Chat_AsyncTask_insert(getContext()).execute(Chat_ID, message_ID, Constants.MY_User_ID, chat_message, date, time);
                        String update_massage = Constants.New_Chat_Message + "|" + Chat_ID + "^" + message_ID;
                        Helper.Send_Message_To_All_My_Friend_By_Event(getContext(), Event_ID, update_massage);
                        et_chat_message_ui.setText("");
                        setChatList(view);
                        getArguments().putInt("from", 2);
                    }
                }
            });

        }

        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        private void setAttendingList(final View view){
            members_keys.clear();
            sqlAttending();

            final Context context = getActivity();
            ListView listview = (ListView) view.findViewById(R.id.lvAttending);
            StableArrayAdapterAttending adapter = new StableArrayAdapterAttending(getActivity()  ,members_keys , Event_ID);
            listview.setAdapter(adapter);

            listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
                                               final int pos, final long id) {
                    // TODO Auto-generated method stub
                    String name = members_keys.get(pos);
                    String permission = Helper.getMyPermission(Event_ID);
                    if (!permission.equals(Constants.Participant) || name.equals(Constants.MY_User_ID)){
                        if(!(permission.equals(Constants.Manager) && name.equals(Constants.MY_User_ID))){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            ArrayList<String>[] dbUsers = sqlHelper.select(null, Table_Users.Table_Name, new String[]{Table_Users.User_ID}, new String[]{members_keys.get(pos)}, new int[]{1});
                            if (!dbUsers[0].isEmpty()) {
                                name = dbUsers[2].get(0);
                            }
                            // set dialog message
                            alertDialogBuilder
                                    .setMessage("Delete " + name + "?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String User_ID = members_keys.get(pos);
                                            new EventUser_AsyncTask_delete(context).execute(Event_ID, User_ID);
                                            String message = Constants.Delete_Attending + "|" + Event_ID + "^" + User_ID;
                                            Helper.Send_Message_To_Friend_By_Event_Except_One(context, Event_ID, User_ID, message);
                                            if (User_ID.equals(Constants.MY_User_ID)) {
                                                Helper.Delete_Event_MySQL(Event_ID);
                                                getActivity().finish();
                                            } else {
                                                message = Constants.Delete_Event + "|" + Event_ID;
                                                new SendMessage_AsyncTask(context).execute(Constants.MY_User_ID, message, User_ID);
                                                sqlHelper.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID,
                                                        Table_Events_Users.User_ID}, new String[]{Event_ID, User_ID}, new int[]{1});
                                            }
                                            setAttendingList(view);
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
                        }else {
                            Toast.makeText(getContext(), "Manager can't delete himself, please change your permission", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Participant can't delete friend", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });
        }
        private void setTodoList(final View rootView) {
            Tasks_keys.clear();
            sqlTodo();
            final ArrayList<String>[] dbTasks = sqlHelper.select(null, Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID}, new String[]{Event_ID}, null);

            final Context context = getActivity();
            ListView listview = (ListView) rootView.findViewById(R.id.lv_etd);
            listview.setClickable(true);
            final Intent task = new Intent(getActivity().getApplicationContext(), Task.class);

            StableArrayAdapterTodo adapter = new StableArrayAdapterTodo(getActivity().getApplicationContext(),Tasks_keys, Event_ID);
            listview.setAdapter(adapter);

            listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
                                               final int pos, final long id) {
                    // TODO Auto-generated method stub
                    String permission = Helper.getMyPermission(Event_ID);
                    if(!permission.equals(Constants.Participant)) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        String task_name = dbTasks[2].get(pos);
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Delete Task: " + task_name + "?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int task_key = Tasks_keys.get(pos);
                                        sqlHelper.delete(Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number},
                                                new String[]{Event_ID, task_key + ""}, new int[]{1});
                                        new Task_AsyncTask_delete(context).execute(Event_ID, task_key + "");
                                        String message = Constants.Delete_Task + "|" + Event_ID + "^" + dbTasks[1].get(pos);
                                        Helper.Send_Message_To_All_My_Friend_By_Event(context, Event_ID, message);
                                        setTodoList(rootView);
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
                        Toast.makeText(getContext(), "Participant can't delete task", Toast.LENGTH_LONG).show();
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
                    data.putInt("taskID", Tasks_keys.get(position));
                    data.putString("Event_ID", Event_ID);
                    task.putExtras(data);
                    startActivity(task);
                }
            });
        }
        private void setChatList(final View rootView) {
            chat_keys.clear();
            sqlChat();
            final ArrayList<String>[] dbChat = sqlHelper.select(null, Table_Chat.Table_Name + Helper.Clean_Event_ID(Event_ID), null, null, null);

            final Context context = getActivity();
            ListView listview = (ListView) rootView.findViewById(R.id.lv_chat);
            listview.setClickable(true);

            StableArrayAdapterChat adapter = new StableArrayAdapterChat(getActivity().getApplicationContext(),chat_keys, Event_ID);
            listview.setAdapter(adapter);

            listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
                                               final int pos, final long id) {
                    // TODO Auto-generated method stub
                    String sender_ID = dbChat[1].get(pos);
                    if (sender_ID.equals(Constants.MY_User_ID)) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Delete message?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String Chat_ID = Table_Chat.Table_Name + Helper.Clean_Event_ID(Event_ID);
                                        sqlHelper.delete(Chat_ID, new String[]{Table_Chat.Message_ID}, new String[]{dbChat[0].get(pos)}, new int[]{1});
                                        new Task_AsyncTask_delete(context).execute(Chat_ID, dbChat[0].get(pos));
                                        String message = Constants.Delete_Chat_Message + "|" + Chat_ID + "^" + dbChat[0].get(pos);
                                        Helper.Send_Message_To_All_My_Friend_By_Event(context, Event_ID, message);
                                        setChatList(rootView);
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
                    }
                    return true;
                }
            });
            int last = Math.max(0,listview.getCount()-1);
            listview.setSelection(last);
        }


        private void sqlAttending() {
            ArrayList<String>[] dbResult = sqlHelper.select(null, Table_Events_Users.Table_Name,
                    new String[]{Table_Events_Users.Event_ID}, new String[]{Event_ID}, null);
            for (String val : dbResult[1]){
                members_keys.add(val);
            }
        }

        private void sqlTodo() {
            ArrayList<String>[] dbResult = sqlHelper.select(null, Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID}, new String[]{Event_ID}, null);
            for (String val: dbResult[1]){
                Tasks_keys.add(Integer.parseInt(val));
            }
        }

        private void sqlChat() {
            ArrayList<String>[] dbResult = sqlHelper.select(null, Table_Chat.Table_Name + Helper.Clean_Event_ID(Event_ID),
                    null, null, null);
            for (String val : dbResult[0]){
                chat_keys.add(val);
            }
        }

    }
}

class StableArrayAdapterAttending extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private ArrayList<String> members_keys;
    private String Event_ID;

    public StableArrayAdapterAttending(Context context, ArrayList<String> members_keys, String Event_ID) {
        this.context = context;
        this.members_keys = members_keys;
        this.Event_ID = Event_ID;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.event_attending_list_item, null);

        TextView name = (TextView) convertView.findViewById(R.id.tv_ea_list_item);
        final RadioGroup radioGroup = (RadioGroup) convertView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ArrayList<String>[] dbResult = Helper.getFriends_From_Event(Event_ID);
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb_ea_list_yes: {
                        Update_Attending(dbResult, Constants.Yes, position);
                        break;
                    }
                    case R.id.rb_ea_list_maybe: {
                        Update_Attending(dbResult, Constants.Maybe, position);
                        break;
                    }
                    case R.id.rb_ea_list_no: {
                        Update_Attending(dbResult, Constants.No, position);
                        break;
                    }
                }
            }
        });
        ArrayList<String>[] dbResult = Helper.getFriends_From_Event(Event_ID);
        //name.setText(dbResult[1].get(position));
        name.setText(Helper.getNickname(dbResult[1].get(position)));
        switch (dbResult[2].get(position)) {
            case Constants.Yes: {
                radioGroup.check(R.id.rb_ea_list_yes);
                break;
            }
            case Constants.Maybe: {
                radioGroup.check(R.id.rb_ea_list_maybe);
                break;
            }
            case Constants.No: {
                radioGroup.check(R.id.rb_ea_list_no);
                break;
            }
            default: {
                break;
            }
        }
        if (!dbResult[1].get(position).equals(Constants.MY_User_ID)) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                radioGroup.getChildAt(i).setEnabled(false);
            }
        }

        return convertView;
    }

    private void Update_Attending(ArrayList<String>[] dbResult, String attend, int pos) {
        if (!dbResult[2].get(pos).equals(attend)) {
            new EventUser_AsyncTask_UpdateAttending(context).execute(Event_ID, Constants.MY_User_ID, attend);
            sqlHelper.update(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Attending}, new String[]{attend},
                    new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID}, new String[]{Event_ID, Constants.MY_User_ID});
            String message = Constants.Update_Attending + "|" + Event_ID + "^" + Constants.MY_User_ID + "^" + attend;
            Helper.Send_Message_To_All_My_Friend_By_Event(context, Event_ID, message);
        }
    }

    public int getCount() {
        //return IDS.size();
        return members_keys.size();
    }

    @Override
    public Object getItem(int position) {
        //String s = users_names.get(position)+" - "+IDS.get(position);
        //return s;
        return members_keys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {

    }
}
class StableArrayAdapterTodo extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private ArrayList<Integer> Tasks_keys;
    private String Event_ID;

    public StableArrayAdapterTodo(Context context, ArrayList<Integer> Tasks_keys, String Event_ID) {
        this.context = context;
        this.Tasks_keys = Tasks_keys;
        this.Event_ID = Event_ID;
    }

    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.event_todo_list_item, null);

        TextView task_tit = (TextView) convertView.findViewById(R.id.tv_etd_list_item_task_tit);
        final TextView task_friend = (TextView) convertView.findViewById(R.id.tv_etd_list_item_frind_tit);
        final CheckBox task_do = (CheckBox) convertView.findViewById(R.id.cb_etd_list_item_task);
        task_do.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ArrayList<String>[] dbTasks = sqlHelper.select(null, Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID}, new String[]{Event_ID}, null);
                if (isChecked) {
                    Update_Task_do(dbTasks, true, position);
               //     task_friend.setText(Constants.User_Nickname);
                } else {
                    Update_Task_do(dbTasks, false, position);
                    task_friend.setText("");
                }

            }
        });
        ArrayList<String>[] dbTasks = sqlHelper.select(null, Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID}, new String[]{Event_ID}, null);
        task_tit.setText(dbTasks[2].get(position));
        task_friend.setText(Helper.getNickname(dbTasks[4].get(position)));
        task_friend.setTextColor(Color.BLACK);
        if (dbTasks[4].get(position).equals(Constants.UnCheck)) {
            task_do.setChecked(false);
        } else {
            task_do.setChecked(true);
        }
        if (dbTasks[4].get(position).equals(Constants.MY_User_ID)||dbTasks[4].get(position).equals(Constants.UnCheck)) {
            task_do.setEnabled(true);
            task_do.setVisibility(View.VISIBLE);
        } else {
            task_do.setEnabled(false);
        }
        return convertView;
    }

    public int getCount() {
        //return IDS.size();
        return Tasks_keys.size();
    }

    @Override
    public Object getItem(int position) {
        //String s = users_names.get(position)+" - "+IDS.get(position);
        //return s;
        return Tasks_keys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {

    }

    private void Update_Task_do(ArrayList<String>[] dbTasks, Boolean task_do, int pos) {
        if ((dbTasks[4].get(pos).equals(Constants.UnCheck) && task_do == true) ||
                (!dbTasks[4].get(pos).equals(Constants.UnCheck) && task_do == false)) {
            String User_ID;
            if (task_do) {
                new Task_AsyncTask_update(context).execute(dbTasks[0].get(pos), dbTasks[1].get(pos),
                        dbTasks[2].get(pos), dbTasks[3].get(pos), Constants.MY_User_ID);
                sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.User_ID}, new String[]{Constants.MY_User_ID},
                        new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number},
                        new String[]{dbTasks[0].get(pos), dbTasks[1].get(pos)});
                User_ID = Constants.MY_User_ID;
            } else {
                new Task_AsyncTask_update(context).execute(dbTasks[0].get(pos), dbTasks[1].get(pos),
                        dbTasks[2].get(pos), dbTasks[3].get(pos), Constants.UnCheck);
                sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.User_ID}, new String[]{Constants.UnCheck},
                        new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number},
                        new String[]{dbTasks[0].get(pos), dbTasks[1].get(pos)});
                User_ID = Constants.UnCheck;
            }
            String message = Constants.Update_Task_User_ID + "|" + dbTasks[0].get(pos) + "^" + dbTasks[1].get(pos) + "^" + User_ID;
            Helper.Send_Message_To_All_My_Friend_By_Event(context, Event_ID, message);
        }
    }

}
class StableArrayAdapterChat extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private ArrayList<String> chat_keys;
    private String Event_ID;

    public StableArrayAdapterChat(Context context, ArrayList<String> chat_keys, String Event_ID) {
        this.context = context;
        this.chat_keys = chat_keys;
        this.Event_ID = Event_ID;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.event_chat_list_item, null);

        TextView sender_name = (TextView) convertView.findViewById(R.id.tv_chat_list_item_sender_name);
        TextView time = (TextView) convertView.findViewById(R.id.tv_chat_list_item_time);
        TextView chat_message = (TextView) convertView.findViewById(R.id.tv_chat_list_item_message);

        ArrayList<String>[] dbChat = sqlHelper.select(null, Table_Chat.Table_Name + Helper.Clean_Event_ID(Event_ID), null, null, null);
        sender_name.setText(Helper.getNickname(dbChat[1].get(position)));
        sender_name.setTextColor(Color.BLACK);
        time.setText(dbChat[4].get(position));
        time.setTextColor(Color.BLACK);
        chat_message.setText(dbChat[2].get(position));
        chat_message.setTextColor(Color.BLACK);
        return convertView;
    }

    public int getCount() {
        //return IDS.size();
        return chat_keys.size();
    }

    @Override
    public Object getItem(int position) {
        //String s = users_names.get(position)+" - "+IDS.get(position);
        //return s;
        return chat_keys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {

    }
}
