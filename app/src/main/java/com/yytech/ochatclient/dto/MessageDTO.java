package com.yytech.ochatclient.dto;

import java.io.Serializable;

/**
 * Created by Lee on 2017/11/23.
 * 通讯协议
 */
public class MessageDTO<T> implements Serializable {

    private String version;

    /**
    * data的类名，首字母小写 "loginResult"
    */
    private String dataName;

    /**
     * 状态码 1成功 0失败
     */
    private Integer status;

    //状态详情
    private String statusDetail;

    /**
     * 请求1，响应2，通知3
     */
    private Integer sign;

    private T data;

    private Long userId;

    /**
     * 令牌
     */
    private String token;

    public MessageDTO() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public Integer getSign() {
        return sign;
    }

    public void setSign(Integer sign) {
        this.sign = sign;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
