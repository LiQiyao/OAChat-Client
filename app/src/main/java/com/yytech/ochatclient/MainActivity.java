package com.yytech.ochatclient;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.ChatLog;
import com.yytech.ochatclient.dto.data.ChatLogListDTO;
import com.yytech.ochatclient.dto.data.LoginResultDTO;
import com.yytech.ochatclient.dto.data.OnlineDTO;
import com.yytech.ochatclient.dto.data.UserDetailDTO;
import com.yytech.ochatclient.dto.data.UserInfo;
import com.yytech.ochatclient.util.GsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends FragmentActivity {
    String tag = "==MainActivity";
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


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        //获取intent中的信息
        intent=getIntent();
        onlineMsg= (MessageDTO<OnlineDTO>) intent.getSerializableExtra("onlineMsg");
        System.out.println("===onlineMsg" + onlineMsg);

        super.onCreate(savedInstanceState);



        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what==0x123)
                    ChangeTab(0);
                if (msg.what == 0x234){
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
//                    DeleteFriendSuccessDTO deleteFriendSuccessDTO = (DeleteFriendSuccessDTO) detailBundle.getSerializable("deleteFriendSuccessDTO");
//                    Toast.makeText(MainActivity.this,"你和"+deleteFriendSuccessDTO.getDeleteFriendId()+"不再是好友关系！",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,NewsActivity.class);
                    intent.putExtras(detailBundle);
                    startActivity(intent);
//                    Log.i(tag,"你被删除了");
                }
            }
        };
        msgHandler=new Handler(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what==0x789 && !isDestroyed()){
                    ChatLog chatLog= (ChatLog) msg.obj;
                    loginMsg.getData().getChatLogMap().get(chatLog.getSenderId()).setUnReadChatLogCount(loginMsg.getData().getChatLogMap().get(chatLog.getSenderId()).getUnReadChatLogCount()+1);
                    loginMsg.getData().getChatLogMap().get(chatLog.getSenderId()).getChatLogs().add(chatLog);
                    if(tab==0)
                    ChangeTab(0);
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
                ChangeTab(0);
            }
        });
        //点击联系人按钮
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeTab(1);
            }
        });

        //点击设置按钮
        some.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(i==0){
            tab=0;
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
            transaction.commit();
            System.out.println("===loginMsg" + i);
        }
        if(i==1){
            tab=1;
            message.setImageResource(R.mipmap.icon_message_normal);
            contacts.setImageResource(R.mipmap.icon_contact_press);
            install.setImageResource(R.mipmap.icon_set_normal);
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

}
