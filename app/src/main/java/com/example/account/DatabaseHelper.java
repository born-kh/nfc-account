package com.example.account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, Constans.DB_NAME, null, Constans.DB_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constans.CREATE_NFC_INFO_TABLE);
        db.execSQL(Constans.CREATE_NFC_ACTIVE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + Constans.DB_NAME);
        onCreate(db);
    }
    public int insertNfcInfo(String cardID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constans.C_ID_CARD, cardID);
        values.put(Constans.C_STATUS_NEW, 1);
        long id = db.insert(Constans.NFC_INFO_TABLE, null, values);
        db.close();
        return (int) id;
    }




    public void importNfcCards(String[] cards){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
                db.execSQL("DELETE FROM " +Constans.NFC_INFO_TABLE);
                db.execSQL("DELETE FROM " +Constans.NFC_ACTIVE_TABLE);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.execSQL("VACUUM");
        for (String cardId : cards) {
            ContentValues values = new ContentValues();
            values.put(Constans.C_ID_CARD, cardId);
            values.put(Constans.C_STATUS_NEW, 0);
            db.insert(Constans.NFC_INFO_TABLE, null, values);
        }
        db.close();
    }
    public long insertNfcActive(Integer infoID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constans.C_ID_INFO, infoID);

        long id = db.insert(Constans.NFC_ACTIVE_TABLE, null, values);
        db.close();
        return id;
    }

    public Cursor  checkIdCard (String idCard) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constans.NFC_INFO_TABLE, null, Constans.C_ID_CARD + "=?", new String[]{idCard}, null, null, null);
       return  cursor;

    }
      public Cursor  getNfcActive() {
        String query = "SELECT * FROM " + Constans.NFC_ACTIVE_TABLE + " nfcActive, " + Constans.NFC_INFO_TABLE + " nfcInfo WHERE nfcActive.ID_INFO = nfcInfo.ID AND nfcActive.STATUS = 0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }



    public void nfcActiveUpdateStatus(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constans.C_STATUS, 1);
        db.update(Constans.NFC_ACTIVE_TABLE, values, null, null);
        db.close();
    }
}
