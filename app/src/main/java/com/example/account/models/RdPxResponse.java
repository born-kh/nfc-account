package com.example.account.models;

public class RdPxResponse {
    private RdPxList[] rdList;
    private RdPxList[] pxList;
    public RdPxResponse(RdPxList[] rdList, RdPxList[] pxList) {
        this.rdList = rdList;
        this.pxList = pxList;
    }

    public RdPxList[] getRdList() {
        return rdList;
    }

    public RdPxList[] getPxList() {
        return pxList;
    }
}
