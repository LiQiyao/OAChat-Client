package com.yytech.ochatclient.dto.data;

import java.io.Serializable;

/**
 * Created by Lee on 2017/11/23.
 */
public class AddFriendSuccessDTO implements Serializable {

    private UserDetailDTO userDetailDTO;

    public AddFriendSuccessDTO() {
    }

    public AddFriendSuccessDTO(UserDetailDTO userDetailDTO) {
        this.userDetailDTO = userDetailDTO;
    }

    public UserDetailDTO getUserDetailDTO() {
        return userDetailDTO;
    }

    public void setUserDetailDTO(UserDetailDTO userDetailDTO) {
        this.userDetailDTO = userDetailDTO;
    }
}
