package com.yytech.ochatclient.resolver.impl;


import android.os.Bundle;
import android.os.Message;

import com.google.gson.reflect.TypeToken;
import com.yytech.ochatclient.MainActivity;
import com.yytech.ochatclient.NewPeopleActivity;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.AddFriendRequestDTO;
import com.yytech.ochatclient.resolver.DataResolver;
import com.yytech.ochatclient.util.GsonUtil;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/12/1.
 */
public class AddFriendRequestDTOResolver implements DataResolver {
    private String tag ="addFriReqRe";
    private AddFriendRequestDTO addFriendRequestDTO;
    private MessageDTO<AddFriendRequestDTO> addFriendRequestMsg;
    @Override
    public void resolve(String jsonMessage) {
        Type objectType = new TypeToken<MessageDTO<AddFriendRequestDTO>>(){}.getType();
        addFriendRequestMsg = GsonUtil.getInstance().fromJson(jsonMessage, objectType);
        addFriendRequestDTO = addFriendRequestMsg.getData();
        System.out.println("==1==addFriReqRe"+MainActivity.loginMsg.getData().getAddFriendRequestList().size());
        for(int i = 0;i < MainActivity.loginMsg.getData().getAddFriendRequestList().size();i++){
            System.out.println(""+MainActivity.loginMsg.getData().getAddFriendRequestList().get(i));
        }
        Boolean hasResquest = false;
        int once = 0;
        for(int i = 0; i < MainActivity.loginMsg.getData().getAddFriendRequestList().size();i++){
            if(MainActivity.loginMsg.getData().getAddFriendRequestList().get(i).getFromUserId() == addFriendRequestDTO.getFromUserId()
                    && (!MainActivity.loginMsg.getData().getAddFriendRequestList().get(i).getAccepted() ||
                    MainActivity.loginMsg.getData().getAddFriendRequestList().get(i).getAccepted() == null)){
                hasResquest = true;
            }
        }
        if(!hasResquest && once == 0){
            MainActivity.loginMsg.getData().getAddFriendRequestList().add(addFriendRequestDTO);
            System.out.println("====收到消息发送好友请求的消息："+ addFriendRequestDTO+" ==accepted:"+addFriendRequestDTO.getAccepted());
            if (NewPeopleActivity.handler != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("addFriendRequestMsg",addFriendRequestMsg);
                        message.setData(bundle);
                        message.what = 0x123;
                        NewPeopleActivity.handler.sendMessage(message);
                    }
                }).start();
                System.out.println("===!!addFriendRequestMsg send in newpeople");
            }else if (MainActivity.handler != null){
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


            once ++;
        }

        System.out.println("==2==addFriReqRe"+MainActivity.loginMsg.getData().getAddFriendRequestList().size());
        for(int i = 0;i < MainActivity.loginMsg.getData().getAddFriendRequestList().size();i++){
            System.out.println(""+MainActivity.loginMsg.getData().getAddFriendRequestList().get(i));
        }






    }
}
