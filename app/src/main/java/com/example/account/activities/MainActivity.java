package com.example.account.activities;

import android.annotation.SuppressLint;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.account.models.MjResponse;
import com.example.account.models.NfcActive;
import com.example.account.models.PostContentData;
import com.example.account.models.RdPxResponse;

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


public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "TAG" ;
    private TextView mTextView;
    private TextView timeTextView;
    private DatabaseHelper dbHelper;
    private MediaPlayer mediaSuccess;
    private MediaPlayer mediaError;
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    private String userId;
    private Button btnSave;
    private Button btnHistory;
    private EditText editTextPrice;
    private EditText editTextDescription;
    private TextView textViewContent1_2;
    private TextView textViewContent3;
    private String mjId;
    private  double content2 = 0;
    private  double content3 = 0;




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

        mTextView = (TextView) findViewById(R.id.textview_date);
        timeTextView = (TextView) findViewById(R.id.textview_time);
        btnSave =(Button) findViewById(R.id.btnSave);
        btnHistory =(Button) findViewById(R.id.btnHistory);
        textViewContent1_2 = (TextView) findViewById(R.id.textview_content1_2);
        textViewContent3= (TextView)findViewById(R.id.textview_content3);
        editTextPrice = (EditText)findViewById(R.id.editPrice);
        editTextDescription =(EditText)findViewById(R.id.editDescription);

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               saveContentData();
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openHistory();

            }
        });

        editTextPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    double price = Double.parseDouble(s.toString());
                    if(price> content2+content3) {
                        editTextPrice.setError("введите меньшую число "+ content2+content3);
                        btnSave.setEnabled(false);
                    }else{
                        editTextPrice.setError(null);
                        btnSave.setEnabled(true);
                    }
                }else{
                    btnSave.setEnabled(false);
                }

            }
        });



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
                getDataMj(CARD_ID);

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

    private  void getDataMj (String cardID){
        Call<MjResponse> call = RetrofitClient.getApiService().getMjData(cardID);
        call.enqueue(new Callback<MjResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<MjResponse> call, Response<MjResponse> response) {
                final MjResponse result = response.body();
                Log.d("result", String.valueOf(result));
                String text1 = result.getContent1().getName() + " "+ result.getContent3();
                Log.d(TAG,  text1);
                textViewContent1_2.setText(text1);
                textViewContent3.setText(result.getContent2());
                editTextDescription.setVisibility(View.VISIBLE);
                editTextPrice.setVisibility(View.VISIBLE);
                editTextPrice.requestFocus();
                btnSave.setVisibility(View.VISIBLE);
                btnHistory.setVisibility(View.VISIBLE);

                mjId = String.valueOf(result.getContent1().getId());
                content2 = Double.parseDouble(result.getContent2());
                content3 = Double.parseDouble(result.getContent3());
          }

            @Override
            public void onFailure(Call<MjResponse> call, Throwable t) {
                btnSave.setVisibility(View.INVISIBLE);
                btnHistory.setVisibility(View.INVISIBLE);
                textViewContent3.setText("");
                textViewContent1_2.setText("");
                editTextDescription.setVisibility(View.INVISIBLE);
                editTextPrice.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "нет", Toast.LENGTH_LONG).show();

            }
        });
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
       if (id ==  R.id.action_exit){
            finish();
        }
       else if(id == R.id.action_change_password){
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


    public  void saveContentData (){

        if (haveNetwork()){

                final  SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Загрузка");
                pDialog.setCancelable(false);
                pDialog.show();

    double price= Double.parseDouble(editTextPrice.getText().toString());
                Call<LoginResponse> call = RetrofitClient.getApiService().saveContentData(new PostContentData( userId,mjId,price , editTextDescription.getText().toString() ));
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        final LoginResponse response1 = response.body();
Log.d("res", response1.getUserId());
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        editTextDescription.setText("");
                                      double price = Double.parseDouble(editTextPrice.getText().toString());
                                      double content3new = content2- price;
                                        textViewContent3.setText( String.valueOf(content3new));
                                        editTextPrice.setText("");
                                        pDialog.hide();
                                        if(response1.isError()){
                                            Toast.makeText(MainActivity.this, "не получился сохранить", Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(MainActivity.this, "Успешно сохранено", Toast.LENGTH_LONG).show();
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

private  void openHistory (){
    Intent intent = new Intent(MainActivity.this, TabsActivity.class);
    intent.putExtra("mjId", mjId);
    startActivity(intent);
    }

    private void openDialog(){
        ChangePasswordDialog changePasswordDialog= new ChangePasswordDialog();
        Bundle bundle = new Bundle();

        bundle.putString("userId", userId);
        changePasswordDialog.setArguments(bundle);
        changePasswordDialog.show(getSupportFragmentManager(), "change password dialog");
    }

}