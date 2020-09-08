package com.example.account.models;

import java.util.ArrayList;

public class ImportResponse {
   private int itemCount;
   private  String[] cards;

    public ImportResponse(int itemCount, String[] cards) {
        this.itemCount = itemCount;
        this.cards = cards;
    }

    public int getItemCount() {
        return itemCount;
    }

    public String[] getCards() {
        return cards;
    }
}
