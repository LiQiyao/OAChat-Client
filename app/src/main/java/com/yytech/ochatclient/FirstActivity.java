package com.yytech.ochatclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.OnlineDTO;
import com.yytech.ochatclient.tcpconnection.TCPClient;

public class FirstActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ImageView loginImage;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    public static Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handler = new Handler(this.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0x123){
                    System.out.println("===handler" + this);
                    Bundle bundle=  msg.getData();
                    MessageDTO<OnlineDTO> onlineMsg= (MessageDTO<OnlineDTO>) bundle.getSerializable("onlineMsg");
                    Intent intent=new Intent(FirstActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtras(bundle);
                    FirstActivity.this.startActivity(intent);
                }
                if (msg.what==0x234){
                    editor.remove("userId");
                    editor.remove("token");
                    Intent intent=new Intent(FirstActivity.this,LoginActivity.class);
                    FirstActivity.this.startActivity(intent);
                }
            }
        };
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        setContentView(R.layout.activity_first);


        preferences = getSharedPreferences("userIdAndToken",MODE_PRIVATE);
        editor = preferences.edit();

        loginImage = (ImageView) findViewById(R.id.login_ensure_imageView);




        String userId = preferences.getString("userId",null);
        String token = preferences.getString("token",null);
        System.out.println("!!!!");
        if(userId == null || token == null){
            System.out.println("no token");
            Intent intent = new Intent(FirstActivity.this,LoginActivity.class);
            startActivity(intent);


        }else{
            System.out.println("has token");
            Toast.makeText(FirstActivity.this,"userId:"+userId+"  ;  token : " + token,Toast.LENGTH_SHORT).show();
            //todo
            MessageDTO<OnlineDTO> sendOnlineMsg = new MessageDTO<OnlineDTO>();
            sendOnlineMsg.setSign(Const.Sign.REQUEST);
            sendOnlineMsg.setDataName("onlineDTO");
            sendOnlineMsg.setUserId(Long.parseLong(userId));
            sendOnlineMsg.setToken(token);
            sendOnlineMsg.setData(new OnlineDTO());
            System.out.println("======---" + sendOnlineMsg);
            //发送
            if (TCPClient.getInstance().getChannel()==null) {
                TCPClient.getInstance().connect();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                editor.remove("userId");
                editor.remove("token");
                Intent intent = new Intent(FirstActivity.this,LoginActivity.class);
                startActivity(intent);
            }
            TCPClient.getInstance().sendMessage(sendOnlineMsg);
        }


    }


}
