package com.yytech.ochatclient.dto.data;

import java.io.Serializable;

/**
 * @author Lee
 * @date 2017/12/7
 */
public class DeleteFriendSuccessDTO implements Serializable{

    private Long deleteFriendId;

    public Long getDeleteFriendId() {
        return deleteFriendId;
    }

    public void setDeleteFriendId(Long deleteFriendId) {
        this.deleteFriendId = deleteFriendId;
    }

    @Override
    public String toString() {
        return "DeleteFriendSuccessDTO{" +
                "deleteFriendId=" + deleteFriendId +
                '}';
    }
}
