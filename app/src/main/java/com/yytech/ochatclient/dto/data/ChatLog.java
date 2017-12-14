package com.yytech.ochatclient.dto.data;

import java.io.Serializable;

public class ChatLog implements Serializable {
    private Long id;

    private Long senderId;

    private Long receiverId;

    private String content;

    private Integer contentType;

    private Long sendTime;

    private Boolean alreadyRead;

    private Boolean alreadyReceived;

    public ChatLog(Long id, Long senderId, Long receiverId, String content, Integer contentType, Long sendTime, Boolean alreadyRead, Boolean alreadyReceived) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.contentType = contentType;
        this.sendTime = sendTime;
        this.alreadyRead = alreadyRead;
        this.alreadyReceived = alreadyReceived;
    }

    public ChatLog() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public Boolean getAlreadyRead() {
        return alreadyRead;
    }

    public void setAlreadyRead(Boolean alreadyRead) {
        this.alreadyRead = alreadyRead;
    }

    public Boolean getAlreadyReceived() {
        return alreadyReceived;
    }

    public void setAlreadyReceived(Boolean alreadyReceived) {
        this.alreadyReceived = alreadyReceived;
    }
}