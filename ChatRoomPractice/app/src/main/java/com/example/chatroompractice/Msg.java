package com.example.chatroompractice;

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private int type;
    private String username;
    private String time;
    private int imageId;

    public Msg(String content, int type, String username, String time, int imageId) {
        this.content = content;
        this.type = type;
        this.username = username;
        this.time = time;
        this.imageId = imageId;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getTime() {
        return time;
    }

    public int getImageId() {
        return imageId;
    }
}
