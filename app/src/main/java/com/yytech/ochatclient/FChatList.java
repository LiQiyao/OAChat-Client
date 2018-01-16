package com.yytech.ochatclient;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yytech.ochatclient.adapter.ChatListAdapter;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.ChatLogListDTO;
import com.yytech.ochatclient.dto.data.LoginResultDTO;
import com.yytech.ochatclient.dto.data.UserInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trt on 2017/11/26.
 */

public class FChatList extends android.support.v4.app.Fragment {
    private MessageDTO<LoginResultDTO> loginMsg;
    private List<ChatLogListDTO> chatList;
    private UserInfo userInfo;
    private HttpURLConnection conn;
    private TextView chatAddText;
    public String IP=Const.IP;
    private Bundle bundle;
    public int HTTP_PORT=Const.HTTP_PORT;
    private TextView chatAddtext;
    private int[] heads=new int[]{R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        bundle=getArguments();
        loginMsg= (MessageDTO<LoginResultDTO>) bundle.getSerializable("loginMsg");
        chatList = new ArrayList<>();
        for (ChatLogListDTO chatLogListDTO : loginMsg.getData().getChatLogMap().values()){
            chatList.add(chatLogListDTO);
        }
        userInfo= (UserInfo) bundle.getSerializable("userInfo");
        ChatListAdapter chatListAdapter = new ChatListAdapter(chatList, getActivity());
        View view = inflater.inflate(R.layout.f_chat_list, container, false);
        ImageView head= (ImageView) view.findViewById(R.id.head);
        head.setImageResource(heads[Integer.parseInt(userInfo.getIcon())-1]);
        final ListView chatlist = (ListView) view.findViewById(R.id.chat_list);
        if(chatList.size()>0) {
            chatlist.setAdapter(chatListAdapter);
            chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final int i = position;
                    if (chatList.get(position).getUnReadChatLogCount() != 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL url = new URL("http://" + IP + ":" + HTTP_PORT + "/api/chatLog/read?" + "token=" + loginMsg.getToken() + "&selfId=" + userInfo.getId() + "&friendId=" + chatList.get(i).getFriendId());
                                    System.out.println("===" + "http://" + IP + ":" + HTTP_PORT + "/api/chatLog/read?" + "token=" + loginMsg.getToken() + "&selfId=" + userInfo.getId() + "&friendId=" + chatList.get(i).getFriendId());
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
                    }
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("chatLogInfo", chatList.get(position));
                    bundle.putSerializable("userInfo", userInfo);
                    bundle.putSerializable("token", loginMsg.getToken());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }

        chatAddText = (TextView) view.findViewById(R.id.chat_add);
        chatAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AddPeopleActivity.class);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });




        return view;
    }
}


