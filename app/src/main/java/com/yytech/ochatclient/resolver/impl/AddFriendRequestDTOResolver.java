package com.yytech.ochatclient.resolver.impl;


import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.AddFriendRequestDTO;
import com.yytech.ochatclient.resolver.DataResolver;
import com.yytech.ochatclient.util.GsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/12/1.
 */
public class AddFriendRequestDTOResolver implements DataResolver {

    @Override
    public void resolve(String jsonMessage) {
        Type objectType = new TypeToken<MessageDTO<AddFriendRequestDTO>>(){}.getType();
        MessageDTO<AddFriendRequestDTO> message = GsonUtil.getInstance().fromJson(jsonMessage, objectType);
        AddFriendRequestDTO addFriendRequestDTO = message.getData();
    }
}
