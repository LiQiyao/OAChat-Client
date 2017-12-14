package com.yytech.ochatclient.resolver.impl;


import android.os.Handler;
import android.os.Message;

import com.yytech.ochatclient.Chat;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.ChatLog;
import com.yytech.ochatclient.resolver.DataResolver;
import com.yytech.ochatclient.util.GsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/12/1.
 */
public class ChatLogResolver implements DataResolver {
    @Override
    public void resolve(String jsonMessage) {
        Type objectType = new TypeToken<MessageDTO<ChatLog>>(){}.getType();
        MessageDTO<ChatLog> message = GsonUtil.getInstance().fromJson(jsonMessage, objectType);
        ChatLog chatLog = message.getData();
        Message msg=new Message();
        msg.what=0x789;
        msg.obj=chatLog;
        Chat.revHandler.sendMessage(msg);
        //TODO 在屏幕上显示

    }
}
