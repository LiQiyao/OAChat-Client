package com.yytech.ochatclient.dto.data;

import java.io.Serializable;

/**
 * Created by Lee on 2017/11/23.
 */
public class AddFriendResponseDTO  implements Serializable {

    //添加好友申请者ID
    private Long fromUserId;

    //被添加者ID
    private Long toUserId;

    private boolean accepted;

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
