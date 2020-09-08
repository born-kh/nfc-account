package com.example.account.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.account.R;
import com.example.account.api.RetrofitClient;
import com.example.account.models.ChangePassword;
import com.example.account.models.LoginResponse;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordDialog  extends AppCompatDialogFragment {
    private EditText editTextOldPassword;
    private EditText editTextNewPassword;
    private String userId;
    public Dialog onCreateDialog (Bundle savedInstanceState){
        AlertDialog.Builder builder=  new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.change_password_dialog, null);
        editTextNewPassword = view.findViewById(R.id.editNewPassword);
        editTextOldPassword = view.findViewById((R.id.editOldPassword));
        Intent intent = getActivity().getIntent();
        Bundle b = intent.getExtras();

        if(b!=null)
        {
           userId = (String) b.get("userId");

        }
        builder.setView(view)
                .setTitle("Сменить пароль")
                .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changePasswordDialogListener();
                    }
                });
        return builder.create();
    }

    public void changePasswordDialogListener(){
        String newPassword = editTextNewPassword.getText().toString().trim();
        String password = editTextOldPassword.getText().toString().trim();
        if(newPassword.isEmpty()){
            editTextNewPassword.setError("Login is required");
            editTextOldPassword.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextNewPassword.setError("Password is required");
            editTextOldPassword.requestFocus();
            return;
        }

        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Загрузка...");
        pDialog.setCancelable(false);
        pDialog.show();
        Call<Boolean> call = RetrofitClient.getApiService().changePasswordUser(new ChangePassword(newPassword, password, userId));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                final Boolean result = response.body();
                Log.d("result", String.valueOf(result));

                               pDialog.hide();
                               if(result){
                                   Toast.makeText(getActivity() , "Ваш пароль успешно изменен", Toast.LENGTH_LONG).show();
                               }else{
                                   Toast.makeText(getActivity(), "Текущий пароль неправильный", Toast.LENGTH_LONG).show();
                               }



            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {


                Toast.makeText(getActivity(), "Неизвестная ошибка", Toast.LENGTH_LONG).show();
              pDialog.hide();
            }
        });
    }

}
