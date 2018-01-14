package com.yytech.ochatclient.resolver.impl;

import android.os.Bundle;
import android.os.Message;

import com.google.gson.reflect.TypeToken;
import com.yytech.ochatclient.FirstActivity;
import com.yytech.ochatclient.LoginActivity;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.OnlineDTO;
import com.yytech.ochatclient.resolver.DataResolver;
import com.yytech.ochatclient.util.GsonUtil;

import java.lang.reflect.Type;

/**
 * Created by Trt on 2017/12/7.
 */

public class OnlineDTOResolver implements DataResolver {
    private MessageDTO<OnlineDTO> onlineMsg;
    @Override
    public void resolve(String jsonMessage) {
        System.out.println("=============???" + jsonMessage);
        Type objectType = new TypeToken<MessageDTO<OnlineDTO>>(){}.getType();
        onlineMsg = GsonUtil.getInstance().fromJson(jsonMessage, objectType);
        System.out.println("===loginMsg in resolver" + onlineMsg);
        if (onlineMsg.getStatus() == Const.Status.SUCCESS){
            if (FirstActivity.handler != null){
                        Message message2 = new Message();
                        Bundle bundle2 = new Bundle();
                        bundle2.putSerializable("onlineMsg",onlineMsg);
                        message2.setData(bundle2);
                        message2.what = 0x123;
                        FirstActivity.handler.sendMessage(message2);
                        System.out.println("===!!msg send2");
            }
            if (LoginActivity.handler != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("onlineMsg",onlineMsg);
                        message.setData(bundle);
                        message.what = 0x123;
                        LoginActivity.handler.sendMessage(message);
                    }
                }).start();
                System.out.println("===!!msg send1");
            }
        }
        if (onlineMsg.getStatus() == Const.Status.FAILED){
            Message message2 = new Message();
            message2.what = 0x234;
            FirstActivity.handler.sendMessage(message2);
            System.out.println("===!!msg send2");
        }
    }
}
