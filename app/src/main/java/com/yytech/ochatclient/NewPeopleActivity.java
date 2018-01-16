package com.yytech.ochatclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

import com.githang.statusbar.StatusBarCompat;
import com.yytech.ochatclient.adapter.MessageAdapter;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.AddFriendRequestDTO;
import com.yytech.ochatclient.dto.data.AddFriendSuccessDTO;
import com.yytech.ochatclient.dto.data.ChatLog;
import com.yytech.ochatclient.dto.data.LoginResultDTO;
import com.yytech.ochatclient.dto.data.OnlineDTO;
import com.yytech.ochatclient.tcpconnection.TCPClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewPeopleActivity extends AppCompatActivity {
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
    private Bundle bundle1;
    private boolean shouldPlayBeep = true;
    private String tag = "==NewPeopleActivity.jav";
    private int[] heads=new int[]{R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        setContentView(R.layout.activity_new_people);

        bundle1 = getIntent().getExtras();
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

                    Log.i(tag,"开始发出声音！");
                    emitAddShound(R.raw.add);
                    Log.i(tag,"发出添加好友声！");

                    //广播
                    Intent intent = new Intent();
                    intent.setAction("MY_ACTION");
                    sendBroadcast(intent);

                    addFriendRequestList = MainActivity.loginMsg.getData().getAddFriendRequestList();
                    Log.i(tag,"addFriendRequestList.size :"+addFriendRequestList.size());
                    for (int i = 0;i < addFriendRequestList.size();i++){
                        System.out.println("=====好友请求id"+addFriendRequestList.get(i).getFromUserId());
                    }
                    getDate();
                    messageAdapter = new MessageAdapter(NewPeopleActivity.this,data);
                    listView.setAdapter(messageAdapter);
                    System.out.println("====更新新朋友界面");
                }
                if(msg.what == 0x666){
                    listView = (ListView) findViewById(R.id.message_listview);
                    getDate();
                    messageAdapter = new MessageAdapter(NewPeopleActivity.this,data);
                    listView.setAdapter(messageAdapter);
                    AddFriendSuccessDTO addFriendSuccessDTO= (AddFriendSuccessDTO) msg.getData().getSerializable("addFriendSuccessDTO");
                    ChatLog chatLog = new ChatLog();
                    //代表自己发送
                    chatLog.setSenderId(loginMsg.getUserId());
                    chatLog.setReceiverId(addFriendSuccessDTO.getUserDetailDTO().getId());
                    Long date=System.currentTimeMillis();
                    chatLog.setSendTime(date);
                    chatLog.setContentType(Const.ChatLogContentType.TEXT);
                    //得到发送内容
                    chatLog.setContent("我们已经是好友了，开始聊天吧！");
                    MessageDTO<ChatLog> sendChatLog = new MessageDTO<ChatLog>();
                    sendChatLog.setSign(Const.Sign.REQUEST);
                    sendChatLog.setDataName("chatLog");
                    sendChatLog.setUserId(loginMsg.getUserId());
                    sendChatLog.setToken(loginMsg.getToken());
                    sendChatLog.setData(chatLog);
                    TCPClient.getInstance().sendMessage(sendChatLog);
                    System.out.println("===1188"+sendChatLog);
                    Toast.makeText(NewPeopleActivity.this,"添加好友成功！",Toast.LENGTH_SHORT).show();
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
            map.put("imgid", heads[Integer.parseInt(String.valueOf(addFriendRequestList.get(i).getFromUserId()))-1]);
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
            messageAdapter = new MessageAdapter(NewPeopleActivity.this,data);
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
//                Intent intent = new Intent(NewPeopleActivity.this, PersonInfoActivity.class);
//                startActivity(intent);
//            }
//        });



        //为添加好友按钮添加事件
        addImageview = (ImageView) findViewById(R.id.add_imageview);
        addImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPeopleActivity.this,AddPeopleActivity.class);
                //intent 必须中携带token,userId
                intent.putExtras(bundle1);
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
                map.put("imgid", heads[Integer.parseInt(addFriendRequestList.get(i).getFromIcon())]-1);
                map.put("nickname", addFriendRequestList.get(i).getFromNickName());
                map.put("gender", addFriendRequestList.get(i).getFromGender());
                map.put("accepted",false);
                data.add(map);
            }
            messageAdapter = new MessageAdapter(NewPeopleActivity.this,data);
            listView.setAdapter(messageAdapter);
        }else{
            map.put("imgid", R.drawable.mayknow_head);
            map.put("nickname", nickname);
            map.put("gender", gender);
            map.put("accepted",false);
            data.add(map);
            messageAdapter = new MessageAdapter(NewPeopleActivity.this,data);
            listView.setAdapter(messageAdapter);
        }
    }

    public void onBackPressed() {
        NewPeopleActivity.handler = null;
        Intent intent=new Intent(NewPeopleActivity.this,MainActivity.class);
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


    public void emitAddShound(int sound){
        //为activity注册的默认 音频通道 。
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //检查当前的 铃音模式，或者成为 情景模式
        AudioManager audioService = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            shouldPlayBeep = false;
        }

        //初始化MediaPlayer对象，指定播放的声音 通道为 STREAM_MUSIC，这和上面的步骤一致，指向了同一个通道
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //注册事件。当播放完毕一次后，重新指向流文件的开头，以准备下次播放。
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer player) {
                player.seekTo(0);
            }
        });

        //设定数据源，并准备播放
        AssetFileDescriptor file = getResources().openRawResourceFd(sound);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(0.5f,0.5f);
            mediaPlayer.prepare();
        } catch (IOException ioe) {
            mediaPlayer = null;
        }
        if (shouldPlayBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
    }





}
