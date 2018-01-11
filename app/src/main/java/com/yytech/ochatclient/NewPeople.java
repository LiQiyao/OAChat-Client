package com.yytech.ochatclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.yytech.ochatclient.adapter.MessageAdapter;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.AddFriendRequestDTO;
import com.yytech.ochatclient.dto.data.LoginResultDTO;
import com.yytech.ochatclient.dto.data.OnlineDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewPeople extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private LinearLayout backLayout;
    private ListView listView;
    private MessageAdapter messageAdapter;
    private ImageView addImageview ;
    public static List<AddFriendRequestDTO> addFriendRequestList;
    private List<Map<String,Object>> data;
    public MessageDTO<LoginResultDTO> loginMsg;
    public  MessageDTO<AddFriendRequestDTO> addFriendRequestMsg;
    public static Handler handler;
    private String tag = "==NewPeople.java";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_people);

        Bundle bundle1 = getIntent().getExtras();
        loginMsg = MainActivity.loginMsg;
        data = new ArrayList<Map<String, Object>>();
        addFriendRequestList = loginMsg.getData().getAddFriendRequestList();

        init();

        handler = new Handler(this.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x123){
                    Bundle bundle = msg.getData();
                    addFriendRequestMsg = (MessageDTO<AddFriendRequestDTO>) bundle.getSerializable("addFriendRequestMsg");
                    AddFriendRequestDTO addFriendRequestDTO = addFriendRequestMsg.getData();
                    Toast.makeText(getApplicationContext(),"收到一条好友请求！",Toast.LENGTH_SHORT).show();

                    addFriendRequestList = MainActivity.loginMsg.getData().getAddFriendRequestList();
                    Log.i(tag,"addFriendRequestList.size :"+addFriendRequestList.size());
                    for (int i = 0;i < addFriendRequestList.size();i++){
                        System.out.println("=====好友请求id"+addFriendRequestList.get(i).getFromUserId());
                    }
                    addItem(null, String.valueOf(addFriendRequestDTO.getFromNickName()),addFriendRequestDTO.getFromGender());
                    System.out.println("====更新新朋友界面");
                }
                if(msg.what == 0x666){
                    listView = (ListView) findViewById(R.id.message_listview);
                    getDate();
                    messageAdapter = new MessageAdapter(NewPeople.this,data);
                    listView.setAdapter(messageAdapter);
                    Toast.makeText(NewPeople.this,"添加好友成功！",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        };


    }

    private void getDate(){
        data.clear();
        Map<String, Object> map;
        for(int i=0;i<addFriendRequestList.size();i++)
        {
            map = new HashMap<String, Object>();
            map.put("toUserId",addFriendRequestList.get(i).getToUserId());
            map.put("fromUserId",addFriendRequestList.get(i).getFromUserId());
            map.put("imgid", R.drawable.mine_avatar);
            map.put("nickname", addFriendRequestList.get(i).getFromNickName());
            map.put("gender", addFriendRequestList.get(i).getFromGender());
            map.put("accepted",addFriendRequestList.get(i).getAccepted());
            data.add(map);
        }
    }


    //初始化
    public void init(){

        backLayout = (LinearLayout) findViewById(R.id.new_people_back_layout);
        //得到好友消息
        listView = (ListView) findViewById(R.id.message_listview);

        Map<String, Object> map;
        if(addFriendRequestList != null){
            getDate();
            messageAdapter = new MessageAdapter(NewPeople.this,data);
            listView.setAdapter(messageAdapter);
        }else {
            Toast.makeText(this,"好友请求为空！",Toast.LENGTH_SHORT).show();
        }

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(NewPeople.this, PersonInfo.class);
//                startActivity(intent);
//            }
//        });



        //为添加好友按钮添加事件
        addImageview = (ImageView) findViewById(R.id.add_imageview);
        addImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPeople.this,AddPeople.class);
                //intent 必须中携带token,userId

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    public void addItem(String image,String nickname,String gender)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(addFriendRequestList.size() == 1 ){
            for(int i=0;i<addFriendRequestList.size();i++)
            {
                map = new HashMap<String, Object>();
                map.put("imgid", R.drawable.mayknow_head);
                map.put("nickname", addFriendRequestList.get(i).getFromNickName());
                map.put("gender", addFriendRequestList.get(i).getFromGender());
                map.put("accepted",false);
                data.add(map);
            }
            messageAdapter = new MessageAdapter(NewPeople.this,data);
            listView.setAdapter(messageAdapter);
        }else{
            map.put("imgid", R.drawable.mayknow_head);
            map.put("nickname", nickname);
            map.put("gender", gender);
            map.put("accepted",false);
            data.add(map);
            messageAdapter = new MessageAdapter(NewPeople.this,data);
            listView.setAdapter(messageAdapter);
        }
    }

    public void onBackPressed() {
        Intent intent=new Intent(NewPeople.this,MainActivity.class);
        Bundle bundle=new Bundle();
        preferences = getSharedPreferences("userIdAndToken",MODE_PRIVATE);
        editor = preferences.edit();
        String userId = preferences.getString("userId",null);
        String token = preferences.getString("token",null);
        MessageDTO<OnlineDTO> onlineMsg=new MessageDTO<OnlineDTO>();
        onlineMsg.setToken(token);
        onlineMsg.setUserId(Long.valueOf(userId));
        bundle.putSerializable("onlineMsg",onlineMsg);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }








}
