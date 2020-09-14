package com.example.account.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.account.R;
import com.example.account.models.Login;
import com.example.account.api.RetrofitClient;
import com.example.account.models.LoginResponse;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextLogin;
    private EditText editTextPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);


        findViewById(R.id.buttonLogin).setOnClickListener(this);
        int permisI  = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if(permisI !=  PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 123);
        }


    }
    private void userLogin(){
        String login = editTextLogin.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if(login.isEmpty()){
            editTextLogin.setError("Login is required");
            editTextPassword.requestFocus();
            return;
        }

        if(login.isEmpty()){
            editTextLogin.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
        if (haveNetwork()){
            final  SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Зугрузка");
            pDialog.setCancelable(false);
            pDialog.show();
            Call<LoginResponse> call = RetrofitClient.getApiService().userLogin(new Login(login, password));
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    final LoginResponse loginResponse = response.body();


                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // On complete call either onLoginSuccess or onLoginFailed
                                    if(loginResponse.isError()){
                                        editTextLogin.setText("");
                                        editTextPassword.setText("");
                                        Toast.makeText(LoginActivity.this, "Логин или пароль неверны", Toast.LENGTH_LONG).show();
                                    }else{
                                        Intent intent = new Intent(LoginActivity.this, TabsActivity.class);
                                        intent.putExtra("mjId", loginResponse.getUserId());
                                        startActivity(intent);
                                    }
                                    // onLoginFailed();
                                    pDialog.hide();
                                }
                            }, 1000);

                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {

                    Toast.makeText(LoginActivity.this, "Неизвестная ошибка", Toast.LENGTH_LONG).show();
                    pDialog.hide();
                }
            });
        } else if (!haveNetwork()) {
            Toast.makeText(LoginActivity.this, "Нет подключение к сетю", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonLogin:
                userLogin();
                break;


        }
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
}
