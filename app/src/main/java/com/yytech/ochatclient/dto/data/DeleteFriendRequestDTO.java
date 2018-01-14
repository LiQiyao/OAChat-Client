package com.yytech.ochatclient.dto.data;

/**
 * @author Lee
 * @date 2017/12/7
 */
public class DeleteFriendRequestDTO {

    private Long friendId;

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return "DeleteFriendRequestDTO{" +
                "friendId=" + friendId +
                '}';
    }
}
