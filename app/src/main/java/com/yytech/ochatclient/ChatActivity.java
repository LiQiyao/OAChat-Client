package com.yytech.ochatclient;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.yytech.ochatclient.adapter.ChatAdapter;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.ChatLog;
import com.yytech.ochatclient.dto.data.ChatLogListDTO;
import com.yytech.ochatclient.dto.data.OnlineDTO;
import com.yytech.ochatclient.dto.data.UserInfo;
import com.yytech.ochatclient.tcpconnection.TCPClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import static com.yytech.ochatclient.MainActivity.loginMsg;

public class ChatActivity extends Activity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private Long myId;
    private Long friendId;
    private Long sendTime;
    private String token;
    private ChatAdapter chatAdapter;
    private ListView lv_chat_dialog;
    private ChatLogListDTO chatLogInfo;
    private UserInfo userInfo;
    private String friendIcon;
    private String myIcon;
    private ImageView upLoad;
    private List<ChatLog> chatLogs;
    public static Handler revHandler;
    private HttpURLConnection conn;
    public String IP=Const.IP;
    public int HTTP_PORT=Const.HTTP_PORT;
    private Handler fileHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x456) {
                ChatLog chatLog = new ChatLog();
                //代表自己发送
                chatLog.setSenderId(myId);
                chatLog.setContentType(Const.ChatLogContentType.FILE);
                chatLog.setReceiverId(friendId);
                Long date=System.currentTimeMillis();
                chatLog.setSendTime(date);
                //得到发送内容
                chatLog.setContent(msg.getData().getString("strLocalFile"));
                //加入集合
                MessageDTO<ChatLog> sendChatLog = new MessageDTO<ChatLog>();
                sendChatLog.setSign(Const.Sign.REQUEST);
                sendChatLog.setDataName("chatLog");
                sendChatLog.setUserId(myId);
                sendChatLog.setToken(token);
                sendChatLog.setData(chatLog);
                TCPClient.getInstance().sendMessage(sendChatLog);
                chatLogs.add(chatLog);
                chatAdapter.notifyDataSetChanged();
            }
        }
    };
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    /**
                     * ListView条目控制在最后一行
                     */
                    lv_chat_dialog.setSelection(chatLogs.size());
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        setContentView(R.layout.activity_chat);
        final Intent intent=getIntent();
        userInfo= (UserInfo) intent.getSerializableExtra("userInfo");
        myIcon=userInfo.getIcon();
        token= (String) intent.getSerializableExtra("token");
        chatLogInfo= (ChatLogListDTO) intent.getSerializableExtra("chatLogInfo");
        friendIcon=chatLogInfo.getFriendIcon();
        chatLogs=chatLogInfo.getChatLogs();
        myId=userInfo.getId();
        System.out.println("===222222");
        if(chatLogs.get(0).getReceiverId().equals(myId)) {
            friendId = chatLogs.get(0).getSenderId();
        }
        else {
            friendId=chatLogs.get(0).getReceiverId();
        }
        Date date=new Date();
        sendTime=date.getTime();
        lv_chat_dialog = (ListView) findViewById(R.id.lv_chat_dialog);
        final Button btn_chat_message_send = (Button) findViewById(R.id.btn_chat_message_send);
        final EditText et_chat_message = (EditText) findViewById(R.id.et_chat_message);
        final ImageView back= (ImageView) findViewById(R.id.back);
        final TextView friendName= (TextView) findViewById(R.id.friendName);
        final ImageView upLoad= (ImageView) findViewById(R.id.upload);
        friendName.setText(chatLogInfo.getFriendNickName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        upLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileIntent=new Intent(ChatActivity.this,FilePathList.class);
                System.out.println("===OnClick");
                startActivity(fileIntent);
            }
        });
        /**
         *setAdapter
         */
        chatAdapter = new ChatAdapter(this, chatLogs,myId,myIcon,friendIcon);
        lv_chat_dialog.setAdapter(chatAdapter);

        et_chat_message.addTextChangedListener(new TextWatcher() {

            // 第二个执行
            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {
            }

            // 第一个执行
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            // 第三个执行
            @Override
            public void afterTextChanged(Editable s) { // Edittext中实时的内容
                if(s.length()>0){
                    btn_chat_message_send.setBackgroundColor(Color.parseColor("#12B7F5"));
                }
                else{
                    btn_chat_message_send.setBackgroundColor(Color.parseColor("#D4D4D4"));
                }
            }
        });
        revHandler = new Handler() {
            ChatLog newChatLog;
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x789) {
                    newChatLog = (ChatLog) msg.obj;
                    if(newChatLog.getSenderId().equals(friendId)) {
                        chatLogs.add(newChatLog);
                        chatAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
        /**
         * 发送按钮的点击事件
         */
        btn_chat_message_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_chat_message.getText().toString())) {
                    Toast.makeText(ChatActivity.this, "发送内容不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                ChatLog chatLog = new ChatLog();
                //代表自己发送
                chatLog.setSenderId(myId);
                chatLog.setReceiverId(friendId);
                chatLog.setContentType(Const.ChatLogContentType.TEXT);
                //得到发送内容
                chatLog.setContent(et_chat_message.getText().toString());
                Long date=System.currentTimeMillis();
                chatLog.setSendTime(date);
                //加入集合
                MessageDTO<ChatLog> sendChatLog = new MessageDTO<ChatLog>();
                sendChatLog.setSign(Const.Sign.REQUEST);
                sendChatLog.setDataName("chatLog");
                sendChatLog.setUserId(myId);
                sendChatLog.setToken(token);
                sendChatLog.setData(chatLog);
                TCPClient.getInstance().sendMessage(sendChatLog);
                chatLogs.add(chatLog);
                //清空输入框
                et_chat_message.setText("");
                //刷新ListView
                chatAdapter.notifyDataSetChanged();
                handler.sendEmptyMessage(1);
                System.out.println("===1111111112111211");
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
            setIntent(intent);
            Intent intent1=getIntent();
            String strLocalFile = intent1.getStringExtra("strLocalFile");
            System.out.println("===Restart  " + strLocalFile);
                Message msg = new Message();
                msg.what = 0x456;
                Bundle bundle = new Bundle();
                bundle.putString("strLocalFile", strLocalFile);
                msg.setData(bundle);
                fileHandler.sendMessage(msg);
    }

    public void onBackPressed() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://" + IP + ":" + HTTP_PORT + "/api/chatLog/read?" + "token=" + loginMsg.getToken() + "&selfId=" + userInfo.getId() + "&friendId=" + friendId);
                    System.out.println("===" + "http://" + IP + ":" + HTTP_PORT + "/api/chatLog/read?" + "token=" + loginMsg.getToken() + "&selfId=" + userInfo.getId() + "&friendId=" + friendId);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    System.out.println("=====HTTP");
                    conn.setConnectTimeout(10 * 1000);
                    //请求头的信息
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setRequestProperty("Origin", "http://" + IP);
                    conn.connect();
                    int code = conn.getResponseCode();
                    System.out.println("===code" + code);
                } catch (MalformedURLException e) {
                    System.out.println("===error1");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("===error2");
                    e.printStackTrace();
                }
                finally {
                    conn.disconnect();
                }
            }
        }).start();

        Intent intent=new Intent(ChatActivity.this,MainActivity.class);
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
