package com.yytech.ochatclient.resolver.impl;


import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.yytech.ochatclient.MainActivity;
import com.yytech.ochatclient.NewPeople;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.AddFriendRequestDTO;
import com.yytech.ochatclient.resolver.DataResolver;
import com.yytech.ochatclient.util.GsonUtil;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/12/1.
 */
public class AddFriendRequestDTOResolver implements DataResolver {

    @Override
    public void resolve(String jsonMessage) {
        Type objectType = new TypeToken<MessageDTO<AddFriendRequestDTO>>(){}.getType();
        final MessageDTO<AddFriendRequestDTO> addFriendRequestMsg = GsonUtil.getInstance().fromJson(jsonMessage, objectType);
        final AddFriendRequestDTO addFriendRequestDTO = addFriendRequestMsg.getData();
        Log.i("==1==AddFriendRequestDTOResolver.java",MainActivity.loginMsg.getData().getAddFriendRequestList().size()+"");
        MainActivity.loginMsg.getData().getAddFriendRequestList().add(addFriendRequestDTO);
        Log.i("==2==AddFriendRequestDTOResolver.java",MainActivity.loginMsg.getData().getAddFriendRequestList().size()+"");
        System.out.println("====收到消息发送好友请求的消息："+ addFriendRequestDTO+" ==accepted:"+addFriendRequestDTO.getAccepted());
        if (NewPeople.handler != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("addFriendRequestMsg",addFriendRequestMsg);
                    message.setData(bundle);
                    message.what = 0x123;
                    NewPeople.handler.sendMessage(message);
                }
            }).start();
            System.out.println("===!!addFriendRequestMsg send in newpeople");
        }
        //NewPeople newPeople = new NewPeople();
        //newPeople.addItem(null, String.valueOf(addFriendRequestDTO.getFromNickName()),addFriendRequestDTO.getFromGender());


        if (MainActivity.handler != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("addFriendRequestMsg",addFriendRequestMsg);
                    message.setData(bundle);
                    message.what = 0x234;
                    MainActivity.handler.sendMessage(message);
                }
            }).start();
            System.out.println("===!!addFriendRequestMsg send in main");
        }


    }
}
