package com.example.account.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;


import android.os.Parcelable;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import androidx.annotation.RequiresApi;

import com.example.account.Constans;
import com.example.account.DatabaseHelper;
import com.example.account.R;
import com.example.account.api.RetrofitClient;
import com.example.account.models.ImportResponse;
import com.example.account.models.LoginResponse;
import com.example.account.models.NfcActive;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.Constants;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TAG" ;
    private TextView mTextView;
    private TextView timeTextView;
    private DatabaseHelper dbHelper;
    private MediaPlayer mediaSuccess;
    private MediaPlayer mediaError;
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    private String userId;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //placing toolbar in place of actionbar

        Date now = new Date();
        dbHelper = new  DatabaseHelper(this);

        mTextView = (TextView) findViewById(R.id.textview_first);
        timeTextView = (TextView) findViewById(R.id.textview_first2);
        Bundle arguments = getIntent().getExtras();
       Log.d("userID", String.valueOf(arguments.get("userId")));
        if(arguments!=null){
           userId =  arguments.get("userId").toString();
        }

        mTextView.setText(DateFormat.getDateInstance(DateFormat.MEDIUM, new Locale("ru")).format(now));


        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask,  0, 1000);



    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null){
            finish();
        }
      if(!nfcAdapter.isEnabled())  {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
              Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
              startActivity(intent);
          } else {
              Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
              startActivity(intent);
          }
      }


        nfcAdapter.enableForegroundDispatch(this,
                pendingIntent,
                new IntentFilter[]{filter},
                Constans.techList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("onNewIntent", "1");
        mediaError = MediaPlayer.create(this, R.raw.error);
        mediaSuccess= MediaPlayer.create(this, R.raw.success);

        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Log.d("onNewIntent", "2");
            Parcelable tagN = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tagN != null) {
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                String CARD_ID = getHex(id);
                int checkStatusNew = 0;
                int idCard = 0;
                 Cursor cursor = dbHelper.checkIdCard(CARD_ID);
                 Log.d("countt", String.valueOf(cursor.getCount()));
                if (cursor.getCount()>0) {
                    cursor.moveToFirst();
                    idCard = cursor.getInt(cursor.getColumnIndex(Constans.C_ID));
                    if(cursor.getInt(cursor.getColumnIndex(Constans.C_STATUS_NEW)) == 1){
                        checkStatusNew = 1;
                    }
                }
                if(idCard != 0){
                    dbHelper.insertNfcActive(idCard);
                    if(checkStatusNew==1){
                        mediaError.start();
                        final  SweetAlertDialog  alertDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                        alertDialog.setTitleText("Error!");
                        alertDialog.setContentText(CARD_ID);
                        alertDialog.setCancelable(false);
                        alertDialog.hideConfirmButton();
                        alertDialog.show();
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.hide();
                                    }
                                }, 1000);

                    }else{

                        mediaSuccess.start();
                        final  SweetAlertDialog  alertDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                        alertDialog.setTitleText("OK!");
                        alertDialog.setContentText(CARD_ID);
                        alertDialog.setCancelable(false);

                        alertDialog.hideConfirmButton();
                        alertDialog.show();
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.hide();
                                    }
                                }, 1000);
                    }


                }else{

                    int cardId = dbHelper.insertNfcInfo(CARD_ID);
                    dbHelper.insertNfcActive(cardId);
                    mediaError.start();
                    final  SweetAlertDialog  alertDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                    alertDialog.setTitleText("Error!");
                    alertDialog.setContentText(CARD_ID);
                    alertDialog.setCancelable(false);
                    alertDialog.hideConfirmButton();
                    alertDialog.show();
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    alertDialog.hide();
                                }
                            }, 1000);
                }




            }
            else {
                Log.d(TAG, "Parcelable NULL");
            }



            Parcelable[] messages1 = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (messages1 != null) {
                Log.d(TAG, "Found " + messages1.length + " NDEF messages");
            }
            else {
                Log.d(TAG, "Not EXTRA_NDEF_MESSAGES");
            }

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            if(ndef != null) {

                Log.d("onNewIntent:", "NfcAdapter.EXTRA_TAG");

                Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (messages != null) {
                    Log.d(TAG, "Found " + messages.length + " NDEF messages");
                }
            }
            else {
                Log.d(TAG, "Write to an unformatted tag not implemented");
            }
        }
    }



    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= bytes.length - 1; i++) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append("0");
            sb.append(getDecimal(Integer.toHexString(b)));

        }
        return sb.toString();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sync) {
            sync();
        }else if (id ==  R.id.action_exit){
            finish();
        }
        else if (id ==  R.id.action_info){
            final  SweetAlertDialog  alertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            alertDialog.setTitleText("Imei");
            alertDialog.setContentText(getImei(this));
            alertDialog.show();
        }else if(id == R.id.action_import){
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Хотите импортировать даннные?")
                    .setContentText("При импорте все данные очиститься!")
                    .setCancelText("Нет")
                    .setConfirmText("ДА")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) { sDialog.cancel();
                          importData();
                        }
                    })
                    .show();
        }else if(id == R.id.action_change_password){
            openDialog();
        }
        return super.onOptionsItemSelected(item);
    }
    public static String getDecimal(String hex){
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        int val = 0;
        for (int i = 0; i < hex.length(); i++)
        {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return String.valueOf(val);
    }
    public  void importData (){

        if (haveNetwork()){

                final  SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Импорт...");
                pDialog.setCancelable(false);
                pDialog.show();



                Call<ImportResponse> call = RetrofitClient.getApiService().importCards();
                call.enqueue(new Callback<ImportResponse>() {
                    @Override
                    public void onResponse(Call<ImportResponse> call, Response<ImportResponse> response) {
                        final ImportResponse importResponse = response.body();
                        dbHelper.importNfcCards(importResponse.getCards());

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        pDialog.hide();
                                        Toast.makeText(MainActivity.this, importResponse.getItemCount() +" успешно импортировано", Toast.LENGTH_LONG).show();

                                    }
                                }, 1000);

                    }

                    @Override
                    public void onFailure(Call<ImportResponse> call, Throwable t) {
                        Log.d("error", t.getMessage());
                        Toast.makeText(MainActivity.this, "Неизвестная ошибка", Toast.LENGTH_LONG).show();

                        pDialog.hide();
                    }
                });

        } else if (!haveNetwork()) {
            Toast.makeText(MainActivity.this, "Network connection is not available", Toast.LENGTH_SHORT).show();
        }

    }


    public  void sync (){
        ArrayList<NfcActive> nfcActives = new ArrayList<NfcActive>();
        final Cursor cursor =  dbHelper.getNfcActive();
        if (haveNetwork()){
            if(cursor.getCount() == 0){
                Toast.makeText(this, "Данные не существует", Toast.LENGTH_LONG).show();
            }else{
                final  SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Синхронизация");
                pDialog.setCancelable(false);
                pDialog.show();
                while (cursor.moveToNext()){
                    final String idCard =cursor.getString( cursor.getColumnIndex(Constans.C_ID_CARD));
                    final String createdAt = cursor.getString(cursor.getColumnIndex(Constans.C_CREATED_AT));
Log.d("created_at", createdAt);
                        nfcActives.add(new NfcActive(idCard, createdAt , getImei(this)));
                }


                Call<LoginResponse> call = RetrofitClient.getApiService().sync(nfcActives);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        final LoginResponse response1 = response.body();
                        dbHelper.nfcActiveUpdateStatus();
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {

                                        pDialog.hide();
                                        if(response1.isError()){
                                            Toast.makeText(MainActivity.this, response1.getUserId(), Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(MainActivity.this, cursor.getCount() +" успешно отправлено", Toast.LENGTH_LONG).show();

                                        }

                                    }
                                }, 1000);

                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Неизвестная ошибка", Toast.LENGTH_LONG).show();
                        pDialog.hide();
                    }
                });
            }
        } else if (!haveNetwork()) {
            Toast.makeText(MainActivity.this, "Нет подключение к сетю", Toast.LENGTH_SHORT).show();
        }

        }




    public static String getImei(Context context) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String managerImei = telephonyManager.getDeviceId().toString();
            return managerImei;
    }

    private boolean haveNetwork(){
        boolean have_WIFI= false;
        boolean have_MobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo info:networkInfos){
            if (info.getTypeName().equalsIgnoreCase("WIFI"))if (info.isConnected())have_WIFI=true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE DATA"))if (info.isConnected())have_MobileData=true;
        }
        return have_WIFI||have_MobileData;
    }

   public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            final Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "dd:MMMM:yyyy HH:mm:ss a", Locale.getDefault());
            final Date now = new Date();

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    timeTextView.setText( DateFormat.getTimeInstance().format(now));
                }
            });
        }
    }



    private void openDialog(){
        ChangePasswordDialog changePasswordDialog= new ChangePasswordDialog();
        Bundle bundle = new Bundle();

        bundle.putString("userId", userId);
        changePasswordDialog.setArguments(bundle);
        changePasswordDialog.show(getSupportFragmentManager(), "change password dialog");
    }

}