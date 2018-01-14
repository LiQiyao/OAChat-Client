package com.yytech.ochatclient.resolver.impl;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.yytech.ochatclient.MainActivity;
import com.yytech.ochatclient.PersonInfoActivity;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.DeleteFriendSuccessDTO;
import com.yytech.ochatclient.resolver.DataResolver;
import com.yytech.ochatclient.util.GsonUtil;

import java.lang.reflect.Type;

/**
 * Created by admin on 2018/1/13.
 */

public class DeleteFriendSuccessDTOResolver implements DataResolver {
    String tag = "==delFriendSuccessDTORe";
    private DeleteFriendSuccessDTO deleteFriendSuccessDTO;
    private MessageDTO<DeleteFriendSuccessDTO> deleteFriendSuccessMsg;
    @Override
    public void resolve(String jsonMessage) {
        Log.i(tag,""+jsonMessage);
        Type objectType = new TypeToken<MessageDTO<DeleteFriendSuccessDTO>>(){}.getType();
        deleteFriendSuccessMsg = GsonUtil.getInstance().fromJson(jsonMessage, objectType);
        deleteFriendSuccessDTO = deleteFriendSuccessMsg.getData();

        Log.i(tag,""+deleteFriendSuccessDTO);


//        if(Foreground.isForeground(new PersonInfoActivity())){
        if(PersonInfoActivity.handler != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 0x999;
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("deleteFriendSuccessDTO",deleteFriendSuccessDTO);
                    message.setData(bundle);
                    PersonInfoActivity.handler.sendMessage(message);
                    Log.i(tag,"向PersonInfo发送好友添加成功消息");
                }
            }).start();
        }else if(MainActivity.handler != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 0x999;
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("deleteFriendSuccessDTO",deleteFriendSuccessDTO);
                    message.setData(bundle);
                    MainActivity.handler.sendMessage(message);
                    Log.i(tag,"向MainActivity发送好友添加成功消息");
                }
            }).start();
        }

//        }
//        if(Foreground.isForeground(new MainActivity())){


//        }

    }
}
