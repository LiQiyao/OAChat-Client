package com.yytech.ochatclient.resolver;




import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.resolver.impl.AddFriendRequestDTOResolver;
import com.yytech.ochatclient.resolver.impl.AddFriendSuccessDTOResolver;
import com.yytech.ochatclient.resolver.impl.ChatLogResolver;
import com.yytech.ochatclient.resolver.impl.DeleteFriendSuccessDTOResolver;
import com.yytech.ochatclient.resolver.impl.LoginResultDTOResolver;
import com.yytech.ochatclient.resolver.impl.OnlineDTOResolver;
import com.yytech.ochatclient.util.GsonUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/12/1.
 */
public class DataResolverProxy {

    private static final DataResolverProxy INSTANCE = new DataResolverProxy();

    public final ConcurrentHashMap<String, DataResolver> RESOLVER_MAP = new ConcurrentHashMap<>();

    private DataResolverProxy(){
        RESOLVER_MAP.put("chatLog", new ChatLogResolver());
        RESOLVER_MAP.put("loginResultDTO", new LoginResultDTOResolver());
        RESOLVER_MAP.put("addFriendSuccessDTO", new AddFriendSuccessDTOResolver());
        RESOLVER_MAP.put("addFriendRequestDTO", new AddFriendRequestDTOResolver());
        RESOLVER_MAP.put("onlineDTO",new OnlineDTOResolver());
        RESOLVER_MAP.put("deleteFriendSuccessDTO",new DeleteFriendSuccessDTOResolver());

    }

    public static DataResolverProxy getInstance(){
        return INSTANCE;
    }



    public void doAction(String jsonMessage){
        MessageDTO messageDTO = GsonUtil.getInstance().fromJson(jsonMessage, MessageDTO.class);
        System.out.println("===doAction" + messageDTO);

        System.out.println("===> resolver" + RESOLVER_MAP.get(messageDTO.getDataName()));
        RESOLVER_MAP.get(messageDTO.getDataName()).resolve(jsonMessage);
    }
}
