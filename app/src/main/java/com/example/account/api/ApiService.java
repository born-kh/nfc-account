package com.example.account.api;

import com.example.account.models.ChangePassword;
import com.example.account.models.ImportResponse;
import com.example.account.models.Login;
import com.example.account.models.LoginResponse;
import com.example.account.models.MjResponse;
import com.example.account.models.NfcActive;
import com.example.account.models.PostContentData;
import com.example.account.models.RdPxResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("login.php")
    Call<LoginResponse> userLogin(
            @Body Login login
    );
    @POST("sync.php")
    Call<LoginResponse> sync(
            @Body ArrayList<NfcActive> nfcActives
    );

    @GET("api.php")
    Call<ImportResponse> importCards();

    @GET("getRdPxList.php")
    Call<RdPxResponse> getRdPx(@Query("mjId") String mjId  );
    @GET("get-content-data.php")
    Call<MjResponse> getMjData(@Query("cardNumber") String cardNumber, @Query("imei") String imei  );

    @POST("change-password-person.php")
    Call<Boolean> changePasswordUser(
            @Body ChangePassword changePassword
            );

    @POST("save-content-data.php")
    Call<LoginResponse> saveContentData(
            @Body PostContentData postContentData
    );

}
