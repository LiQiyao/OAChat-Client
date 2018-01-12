package com.yytech.ochatclient.resolver.impl;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.yytech.ochatclient.AddPeople;
import com.yytech.ochatclient.MainActivity;
import com.yytech.ochatclient.NewPeople;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.AddFriendRequestDTO;
import com.yytech.ochatclient.dto.data.AddFriendSuccessDTO;
import com.yytech.ochatclient.resolver.DataResolver;
import com.yytech.ochatclient.util.GsonUtil;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/12/1.
 */
public class AddFriendSuccessDTOResolver implements DataResolver {
    String tag = "==AddFriendSucessDtoRe";
    @Override
    public void resolve(String jsonMessage) {
        Type objectType = new TypeToken<MessageDTO<AddFriendSuccessDTO>>(){}.getType();
        MessageDTO<AddFriendSuccessDTO> message = GsonUtil.getInstance().fromJson(jsonMessage, objectType);
        final AddFriendSuccessDTO addFriendSuccessDTO = message.getData();

        Log.i(tag,""+addFriendSuccessDTO);

        for(AddFriendRequestDTO addFriendRequestDTO : NewPeople.addFriendRequestList){
            if(addFriendRequestDTO.getFromUserId() == addFriendSuccessDTO.getUserDetailDTO().getId()){
                addFriendRequestDTO.setAccepted(true);
            }
        }

        //向新朋友界面发送添加好友成功响应信息
        if(NewPeople.handler != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 0x666;
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("addFriendSuccessDTO",addFriendSuccessDTO);
                    message.setData(bundle);
                    NewPeople.handler.sendMessage(message);
                    Log.i(tag,"向NewPeople发送好友添加成功消息");
                }
            }).start();
        }
        if(AddPeople.handler != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 0x666;
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("addFriendSuccessDTO",addFriendSuccessDTO);
                    message.setData(bundle);
                    NewPeople.handler.sendMessage(message);
                    Log.i(tag,"向AddPeople发送好友添加成功消息");
                }
            }).start();
        }
        if(MainActivity.handler != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 0x666;
                    NewPeople.handler.sendMessage(message);
                    Log.i(tag,"向MainActivity发送好友添加成功消息");
                }
            }).start();
        }

    }
}
