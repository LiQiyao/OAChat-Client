package com.yytech.ochatclient;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.google.gson.reflect.TypeToken;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.ChatLog;
import com.yytech.ochatclient.dto.data.LoginResultDTO;
import com.yytech.ochatclient.dto.data.OnlineDTO;
import com.yytech.ochatclient.dto.data.UserInfo;
import com.yytech.ochatclient.util.GsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends FragmentActivity {
    String tag = "==MainActivity";
    private boolean shouldPlayBeep = true;
    private MessageDTO<OnlineDTO> onlineMsg;
    public static MessageDTO<LoginResultDTO> loginMsg;
    private UserInfo userInfo;
    private HttpURLConnection conn;
    public static Handler handler;
    public static  Handler msgHandler;
    private static String IP= Const.IP;
    private static int HTTP_PORT=Const.HTTP_PORT;
    private Intent intent;
    private RelativeLayout mainContentLayout;
    private int tab;
    private android.support.v4.app.FragmentManager manager;
    private FragmentTransaction transaction;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        //获取intent中的信息
        intent=getIntent();
        onlineMsg= (MessageDTO<OnlineDTO>) intent.getSerializableExtra("onlineMsg");
        System.out.println("===onlineMsg" + onlineMsg);
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        super.onCreate(savedInstanceState);



        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what==0x123)
                    ChangeTab(0);
                if (msg.what == 0x234){
                    Log.i(tag,"开始发出声音！");
                    emitAddShound(R.raw.add);
                    Log.i(tag,"发出添加好友声！");

                    //广播
                    Intent intent = new Intent();
                    intent.setAction("MY_ACTION");
                    sendBroadcast(intent);

                }
                if (msg.what == 0x666){
                    Toast.makeText(MainActivity.this,"添加好友成功！",Toast.LENGTH_SHORT).show();
                }
                //如果得到删除成功消息
                if(msg.what == 0x999){
                    Bundle detailBundle = msg.getData();
                    Intent intent = new Intent(MainActivity.this,NewsActivity.class);
                    intent.putExtras(detailBundle);
                    startActivity(intent);
                    finish();
                }
            }
        };
        msgHandler=new Handler(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                int count=0;
                if (msg.what==0x789 && !isDestroyed()){
                    ChatLog chatLog= (ChatLog) msg.obj;
                    if(loginMsg.getData().getChatLogMap().get(chatLog.getSenderId())==null) {
                        Intent intent=new Intent(MainActivity.this,FirstAddSuccessActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        count=loginMsg.getData().getChatLogMap().get(chatLog.getSenderId()).getUnReadChatLogCount()+1;
                        loginMsg.getData().getChatLogMap().get(chatLog.getSenderId()).setUnReadChatLogCount(count);
                        loginMsg.getData().getChatLogMap().get(chatLog.getSenderId()).getChatLogs().add(chatLog);
                    }
                    if(tab==0)
                    ChangeTab(0);

                    //发出消息提示音
                    emitAddShound(R.raw.dididi);
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://"+IP+":"+HTTP_PORT+"/api/users/allInformation?"+"selfId="+onlineMsg.getUserId()+ "&token=" +onlineMsg.getToken());
                    System.out.println("====="+onlineMsg.getUserId());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    System.out.println("=====HTTP");
                    conn.setConnectTimeout(10 * 1000);
                    //请求头的信息
                    conn.setRequestProperty("accept","*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setRequestProperty("Origin", "http://"+IP);
                    conn.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    String result = "";

                    while((line = br.readLine()) != null){
                        result += line;
                    }
                    br.close();
                    int code = conn.getResponseCode();
                    if(code==200){
                        Type objectType = new TypeToken<MessageDTO<LoginResultDTO>>(){}.getType();
                        loginMsg = GsonUtil.getInstance().fromJson(result, objectType);
                        userInfo=loginMsg.getData().getSelf();
                        handler.sendEmptyMessage(0x123);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    conn.disconnect();
                }
            }
        }).start();
        setContentView(R.layout.activity_main);
        RelativeLayout message= (RelativeLayout) findViewById(R.id.message);
        RelativeLayout contacts= (RelativeLayout) findViewById(R.id.contacts);
        ImageView some = (ImageView) findViewById(R.id.somethingelse);



//        System.out.println("===loginMsgtrt" + userInfo);
        //点击消息按钮
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginMsg!=null)
                ChangeTab(0);
            }
        });
        //点击联系人按钮
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginMsg!=null)
                ChangeTab(1);
            }
        });

        //点击设置按钮
        some.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginMsg!=null)
                ChangeTab(2);
            }
        });


        System.out.println("===ChangeTab");

    }
    public void ChangeTab(int i){
        ImageView message= (ImageView) findViewById(R.id.message_pic);
        ImageView contacts= (ImageView) findViewById(R.id.contacts_pic);
        ImageView install = (ImageView) findViewById(R.id.somethingelse);
        TextView tag= (TextView) findViewById(R.id.tab);
        TextView messageText= (TextView) findViewById(R.id.message_text);
        TextView contactText= (TextView) findViewById(R.id.contact_text);
        TextView setText= (TextView) findViewById(R.id.set_text);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        if(i==0){
            tab=0;
            messageText.setTextColor(getResources().getColor(R.color.text_press));
            contactText.setTextColor(getResources().getColor(R.color.text_normal));
            setText.setTextColor(getResources().getColor(R.color.text_normal));
            message.setImageResource(R.mipmap.icon_message_press);
            contacts.setImageResource(R.mipmap.icon_contact_normal);
            install.setImageResource(R.mipmap.icon_set_normal);
//            tag.setText("消息");
            FChatList fragment=new FChatList();
            Bundle bundle = new Bundle();
            bundle.putSerializable("loginMsg",loginMsg);//这里的values就是我们要传的值
            bundle.putSerializable("userInfo",userInfo);
            fragment.setArguments(bundle);
            transaction.replace(R.id.main_relative, fragment);
            transaction.commitAllowingStateLoss();
            System.out.println("===loginMsg" + i);
        }
        if(i==1){
            tab=1;
            message.setImageResource(R.mipmap.icon_message_normal);
            contacts.setImageResource(R.mipmap.icon_contact_press);
            install.setImageResource(R.mipmap.icon_set_normal);
            messageText.setTextColor(getResources().getColor(R.color.text_normal));
            contactText.setTextColor(getResources().getColor(R.color.text_press));
            setText.setTextColor(getResources().getColor(R.color.text_normal));
//            tag.setText("联系人");
            FContactList fragment= new FContactList();
            Bundle bundle = new Bundle();
            bundle.putSerializable("loginMsg",loginMsg);//这里的values就是我们要传的值
            bundle.putSerializable("userInfo",userInfo);
            fragment.setArguments(bundle);
            transaction.replace(R.id.main_relative, fragment);
            transaction.commit();
            System.out.println("===loginMsg" + i);
        }
        if(i==2){
            tab=3;
            message.setImageResource(R.mipmap.icon_message_normal);
            contacts.setImageResource(R.mipmap.icon_contact_normal);
            install.setImageResource(R.mipmap.icon_set_press);
            messageText.setTextColor(getResources().getColor(R.color.text_normal));
            contactText.setTextColor(getResources().getColor(R.color.text_normal));
            setText.setTextColor(getResources().getColor(R.color.text_press));
//            tag.setText("设置");
            FEditPersonInfo fragment= new FEditPersonInfo();
            Bundle bundle = new Bundle();
            bundle.putSerializable("loginMsg",loginMsg);//这里的values就是我们要传的值
            bundle.putSerializable("userInfo",userInfo);
            fragment.setArguments(bundle);
            transaction.replace(R.id.main_relative, fragment);
            transaction.commit();
            System.out.println("===loginMsg" + i);
        }
    }

    public void refresh(Bundle savedInstanceState){
        onCreate(savedInstanceState);
    }

    public void changeEditAble(View source){
        EditText editText = (EditText) source;
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
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
