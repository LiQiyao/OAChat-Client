package com.yytech.ochatclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.githang.statusbar.StatusBarCompat;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.OnlineDTO;
import com.yytech.ochatclient.tcpconnection.TCPClient;
import com.yytech.ochatclient.util.GsonUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity implements Const.Status{
    EditText loginNameEdit;
    EditText passwordEdit;
    ImageView ensureImage;
    HttpURLConnection conn;
    String loginname;
    private static Context context;
    String password;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private static String IP=Const.IP;
    private static int HTTP_PORT=Const.HTTP_PORT;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    public static Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        verifyStoragePermissions(this);
        context = this;
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        handler=new Handler(context.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0x123){
                    System.out.println("===handler" + context);
                    Intent intent=new Intent(context,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Bundle bundle = msg.getData();
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        };
        loginNameEdit = (EditText) findViewById(R.id.login_loginname);
        passwordEdit = (EditText) findViewById(R.id.login_password);
        ensureImage = (ImageView) findViewById(R.id.login_ensure_imageView);

        ensureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginname = loginNameEdit.getText().toString();
                password = passwordEdit.getText().toString();

                preferences = getSharedPreferences("userIdAndToken",MODE_PRIVATE);
                editor = preferences.edit();

                //post方式登录
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //urlConnection请求服务器，验证
                        try {
                            //1：url对象
                            URL url = new URL("http://"+IP+":"+HTTP_PORT+"/api/users/login");
                            //2;url.openconnection
                            conn = (HttpURLConnection) url.openConnection();

                            //3设置请求参数
                            conn.setRequestMethod("POST");
                            conn.setConnectTimeout(10 * 1000);
                            //请求头的信息
                            String body = "username=" + URLEncoder.encode(loginname) + "&password=" + URLEncoder.encode(password);
                            conn.setRequestProperty("Content-Length", String.valueOf(body.length()));
                            conn.setRequestProperty("Cache-Control", "max-age=0");
                            conn.setRequestProperty("Origin", "http://"+IP);

                            //设置conn可以写请求的内容
                            conn.setDoOutput(true);
                            conn.setDoInput(true);//默认设置为true

                            OutputStream os = conn.getOutputStream();
                            os.write(body.getBytes());
                            os.flush();


                            //4响应码
                            int code = conn.getResponseCode();
                            if (code == 200) {
                                InputStream inputStream = conn.getInputStream();
                                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                                String line;
                                String result = "";

                                while((line = br.readLine()) != null){
                                    result += line;
                                }


                                br.close();
                                MessageDTO messageDTO = GsonUtil.getInstance().fromJson(result, MessageDTO.class);
                                System.out.println("=====================服务器返回的信息：：" + result);
                                System.out.println("==========" + messageDTO.getStatus());
                                if(messageDTO.getStatus() == SUCCESS){

                                    //处理token， userId
                                    editor.putString("userId",messageDTO.getUserId().toString());
                                    editor.putString("token",messageDTO.getToken().toString());
                                    editor.commit();


                                    //构造MessageDTO<OnlineDTO>
                                    MessageDTO<OnlineDTO> sendOnlineMsg = new MessageDTO<OnlineDTO>();
                                    sendOnlineMsg.setSign(Const.Sign.REQUEST);
                                    sendOnlineMsg.setDataName("onlineDTO");
                                    sendOnlineMsg.setUserId(messageDTO.getUserId());
                                    sendOnlineMsg.setToken(messageDTO.getToken());
                                    sendOnlineMsg.setData(new OnlineDTO());
                                    System.out.println("======---" + sendOnlineMsg);
                                    //发送
                                    if (TCPClient.getInstance().getChannel()==null) {
                                        TCPClient.getInstance().connect();
                                    }
                                    Thread.sleep(500);
                                    TCPClient.getInstance().sendMessage(sendOnlineMsg);
                                }
//
                            }else {
                                System.out.println("链接失败！"+code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            conn.disconnect();
                        }
                    }
                }).start();

            }
        });

    }
    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
