package com.example.account.models;

public class MjResponse {
    private Content1 content1;
    private String content2;
    private String content3;


    public MjResponse(Content1 content1, String content2, String content3) {
        this.content1 = content1;
        this.content2 = content2;
        this.content3 = content3;

    }

    public Content1 getContent1() {
        return content1;
    }

    public void setContent1(Content1 content1) {
        this.content1 = content1;
    }

    public String getContent2() {
        return content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }

    public String getContent3() {
        return content3;
    }

    public void setContent3(String content3) {
        this.content3 = content3;
    }


}
