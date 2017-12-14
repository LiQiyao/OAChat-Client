package com.yytech.ochatclient.dto.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Lee on 2017/11/26.
 */
public class LoginResultDTO implements Serializable {

    private UserInfo self;

    private List<UserDetailDTO> friendList;

    private Map<Long, ChatLogListDTO> chatLogMap;

    private List<AddFriendRequestDTO> addFriendRequestList;

    public UserInfo getSelf() {
        return self;
    }

    public void setSelf(UserInfo self) {
        this.self = self;
    }

    public List<UserDetailDTO> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<UserDetailDTO> friendList) {
        this.friendList = friendList;
    }

    public Map<Long, ChatLogListDTO> getChatLogMap() {
        return chatLogMap;
    }

    public void setChatLogMap(Map<Long, ChatLogListDTO> chatLogMap) {
        this.chatLogMap = chatLogMap;
    }

    public List<AddFriendRequestDTO> getAddFriendRequestList() {
        return addFriendRequestList;
    }

    public void setAddFriendRequestList(List<AddFriendRequestDTO> addFriendRequestList) {
        this.addFriendRequestList = addFriendRequestList;
    }
}
