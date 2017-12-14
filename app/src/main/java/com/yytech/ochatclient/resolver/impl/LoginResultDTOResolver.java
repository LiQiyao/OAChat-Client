package com.yytech.ochatclient.resolver.impl;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;


import com.yytech.ochatclient.FirstActivity;
import com.yytech.ochatclient.LoginActivity;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.LoginResultDTO;
import com.yytech.ochatclient.resolver.DataResolver;
import com.yytech.ochatclient.util.GsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/12/1.
 */
public class LoginResultDTOResolver implements DataResolver {

    @Override
    public void resolve(String jsonMessage) {
        System.out.println("=============???" + jsonMessage);
        Type objectType = new TypeToken<MessageDTO<LoginResultDTO>>(){}.getType();
        final MessageDTO<LoginResultDTO> loginMsg = GsonUtil.getInstance().fromJson(jsonMessage, objectType);
        System.out.println("===loginMsg in resolver" + loginMsg);

        if (LoginActivity.handler != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("loginMsg",loginMsg);
                    message.setData(bundle);
                    message.what = 0x123;
                    LoginActivity.handler.sendMessage(message);
                }
            }).start();
            System.out.println("===!!msg send1");
        }



        if (FirstActivity.handler != null){
/*            new Thread(new Runnable() {
                @Override
                public void run() {*/
                    Message message2 = new Message();
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("loginMsg",loginMsg);
                    message2.setData(bundle2);
                    message2.what = 0x123;
                    FirstActivity.handler.sendMessage(message2);
                /*}
            }).start();*/

            System.out.println("===!!msg send2");
        }


    }
}
