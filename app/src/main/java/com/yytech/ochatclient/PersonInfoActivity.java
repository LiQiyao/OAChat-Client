package com.yytech.ochatclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.AddFriendRequestDTO;
import com.yytech.ochatclient.dto.data.ChatLogListDTO;
import com.yytech.ochatclient.dto.data.DeleteFriendRequestDTO;
import com.yytech.ochatclient.dto.data.LoginResultDTO;
import com.yytech.ochatclient.dto.data.OnlineDTO;
import com.yytech.ochatclient.dto.data.UserDetailDTO;
import com.yytech.ochatclient.dto.data.UserInfo;
import com.yytech.ochatclient.tcpconnection.TCPClient;

import java.util.List;

import static com.yytech.ochatclient.R.id.person_info_address;
import static com.yytech.ochatclient.R.id.person_info_tel;

public class PersonInfoActivity extends AppCompatActivity implements Const.Status{
    private String tag = "==PersonInfoActivity.ja";
    private MessageDTO<LoginResultDTO> loginMsg;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private UserInfo userInfo;
    private EditText editText;
    private Bundle detailBundle;
    private UserDetailDTO userDetailDTO;
    private Button sendMsgBtn;
    private Button addfriendBtn;
    private TextView deleteText;
    public static Handler handler;
    private Long userId;
    private String token;
    private List<ChatLogListDTO> chatList;
    private int[] heads=new int[]{R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        setContentView(R.layout.activity_person_info);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                //如果得到删除成功消息
                if(msg.what == 0x999){
                    Toast.makeText(PersonInfoActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        };


        detailBundle = getIntent().getExtras();
        userDetailDTO = (UserDetailDTO) detailBundle.getSerializable("userDetailDTO");
        userId = detailBundle.getLong("userId");
        token = detailBundle.getString("token");

        Log.i(tag,""+userDetailDTO);

        TextView nickNameText = (TextView) findViewById(R.id.person_info_nickname);
        TextView genderText = (TextView) findViewById(R.id.person_info_gender);
        TextView addressText = (TextView) findViewById(person_info_address);
        TextView telText = (TextView)findViewById(person_info_tel);
        deleteText = (TextView) findViewById(R.id.person_info_delete_text);
        sendMsgBtn = (Button) findViewById(R.id.person_info_send_msg_btn);
        addfriendBtn = (Button) findViewById(R.id.person_info_add_friend);
        ImageView head= (ImageView) findViewById(R.id.head);
        head.setImageResource(heads[Integer.parseInt(userDetailDTO.getIcon())-1]);

        nickNameText.setText(userDetailDTO.getNickName());
        genderText.setText(userDetailDTO.getGender());
        addressText.setText(userDetailDTO.getAddress());
        telText.setText(userDetailDTO.getTelephoneNumber());

        //如果不是好友，则隐藏删除按钮，发送消息按钮改变成添加好友
        if(!userDetailDTO.getAlreadyFriend()){
            addfriendBtn.setVisibility(View.VISIBLE);
            deleteText.setVisibility(View.GONE);
            sendMsgBtn.setVisibility(View.GONE);
            addfriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginMsg = MainActivity.loginMsg;
                    userInfo = loginMsg.getData().getSelf();
                    //开始发送添加好友信号
                    System.out.println("=====发送添加好友信息");
                    //发送请求
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MessageDTO<AddFriendRequestDTO> addRequestMsg = new MessageDTO<AddFriendRequestDTO>();
                                AddFriendRequestDTO addFriendRequestDTO = new AddFriendRequestDTO();
                                addFriendRequestDTO.setAccepted(false);
                                addFriendRequestDTO.setFromGender(userInfo.getGender());
                                addFriendRequestDTO.setFromNickName(userInfo.getNickName());
                                addFriendRequestDTO.setFromUserId(loginMsg.getUserId());
                                addFriendRequestDTO.setFromUsername(userInfo.getUsername());
                                addFriendRequestDTO.setToUserId(userDetailDTO.getId());
                                System.out.println("====toUserId : " +userDetailDTO.getId() );
                                addRequestMsg.setData(addFriendRequestDTO);
                                addRequestMsg.setDataName("addFriendRequestDTO");
                                addRequestMsg.setSign(Const.Sign.REQUEST);
                                addRequestMsg.setToken(loginMsg.getToken());
                                addRequestMsg.setUserId(loginMsg.getUserId());

                                System.out.println("====addRequestMsg" + addRequestMsg);
                                if(TCPClient.getInstance().getChannel()==null) {
                                    Log.i("TPClicent=null+22","");
                                    TCPClient.getInstance().connect();
                                }
                                TCPClient.getInstance().sendMessage(addRequestMsg);
                                System.out.println("=====发送好友请求成功");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
        }else {
            deleteText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MessageDTO<DeleteFriendRequestDTO> delFriendReqMsg = new MessageDTO<DeleteFriendRequestDTO>();
                                DeleteFriendRequestDTO deleteFriendRequestDTO = new DeleteFriendRequestDTO();
                                deleteFriendRequestDTO.setFriendId(userDetailDTO.getId());
                                delFriendReqMsg.setData(deleteFriendRequestDTO);
                                delFriendReqMsg.setDataName("deleteFriendRequestDTO");
                                delFriendReqMsg.setSign(Const.Sign.REQUEST);
                                delFriendReqMsg.setToken(token);
                                delFriendReqMsg.setUserId(userId);

                                System.out.println("====delFriendReqMsg" + delFriendReqMsg);
                                if(TCPClient.getInstance().getChannel()==null) {
                                    Log.i("TPClicent=null+11","");
                                    TCPClient.getInstance().connect();
                                }
                                TCPClient.getInstance().sendMessage(delFriendReqMsg);
                                System.out.println("=====发送删除好友请求成功");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
            sendMsgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginMsg = MainActivity.loginMsg;
                   ChatLogListDTO chatLogListDTO=loginMsg.getData().getChatLogMap().get(userDetailDTO.getId());
                    Intent intent=new Intent(PersonInfoActivity.this,ChatActivity.class);
                    Bundle bundle=new Bundle();
                    System.out.println("===chatLogListDTO"+chatLogListDTO);
                    bundle.putSerializable("chatLogInfo",chatLogListDTO);
                    bundle.putSerializable("userInfo", loginMsg.getData().getSelf());
                    bundle.putSerializable("token", loginMsg.getToken());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
//                    Toast.makeText(PersonInfoActivity.this,"转到发消息页面",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void onBackPressed() {
        handler = null;
        Intent intent=new Intent(PersonInfoActivity.this,MainActivity.class);
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
