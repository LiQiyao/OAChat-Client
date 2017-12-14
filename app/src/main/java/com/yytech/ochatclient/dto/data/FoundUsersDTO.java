package com.yytech.ochatclient.dto.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lee on 2017/11/26.
 */
public class FoundUsersDTO implements Serializable {

    private Integer count;

    private List<UserDetailDTO> users;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<UserDetailDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDetailDTO> users) {
        this.users = users;
    }
}
