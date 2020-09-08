package com.example.account;

import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;


public class Constans {
    public  static  final String DB_NAME = "NFC_INFO_DB";
    public  static  final int DB_VERSION = 1;
    public  static  final String NFC_INFO_TABLE = "NFC_INFO_TABLE";
    public  static  final String NFC_ACTIVE_TABLE = "NFC_ACTIVE_TABLE";
    public  static  final String C_ID ="ID";
    public  static  final String C_ID_CARD = "ID_CARD";
    public  static  final String C_ID_INFO = "ID_INFO";
    public  static  final String C_CREATED_AT = "CREATED_AT";

    public  static  final String C_STATUS = "STATUS";
    public  static  final String C_STATUS_NEW = "STATUS_NEW";
    public static  final String CREATE_NFC_INFO_TABLE ="CREATE TABLE "+ NFC_INFO_TABLE + " ("
            + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + C_ID_CARD + " TEXT,"
            + C_STATUS_NEW + " INTEGER DEFAULT 0"
            + ");";
    public static  final String CREATE_NFC_ACTIVE_TABLE ="CREATE TABLE "+ NFC_ACTIVE_TABLE + " ("
            + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + C_ID_INFO + " INTEGER,"
            + C_CREATED_AT + " DATETIME DEFAULT (datetime('now','localtime')),"
            + C_STATUS+ " INTEGER DEFAULT 0"
            + ");";



    public static final String[][] techList = new String[][] {
            new String[] {
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),

                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };


}
