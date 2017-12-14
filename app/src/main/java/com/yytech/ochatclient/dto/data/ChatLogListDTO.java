package com.yytech.ochatclient.dto.data;



import java.io.Serializable;
import java.util.List;

/**
 * Created by Lee on 2017/11/26.
 * 某个好友的所有消息
 */
public class ChatLogListDTO implements Serializable {

    private Long friendId;

    private String friendUsername;

    private String friendNickName;

    private String friendIcon;

    private boolean read;//所有消息是否都已读

    private Long lastChatLogTime;

    private Integer unReadChatLogCount;

    private List<ChatLog> chatLogs;//消息列表

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public List<ChatLog> getChatLogs() {
        return chatLogs;
    }

    public void setChatLogs(List<ChatLog> chatLogs) {
        this.chatLogs = chatLogs;
    }

    public Long getLastChatLogTime() {
        return lastChatLogTime;
    }

    public void setLastChatLogTime(Long lastChatLogTime) {
        this.lastChatLogTime = lastChatLogTime;
    }

    public void setUnReadChatLogCount(Integer unReadChatLogCount) {
        this.unReadChatLogCount = unReadChatLogCount;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendNickName() {
        return friendNickName;
    }

    public void setFriendNickName(String friendNickName) {
        this.friendNickName = friendNickName;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public String getFriendIcon() {
        return friendIcon;
    }

    public void setFriendIcon(String friendIcon) {
        this.friendIcon = friendIcon;
    }

    public Integer getUnReadChatLogCount() {
        return unReadChatLogCount;
    }
}
