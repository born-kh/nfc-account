package com.example.account.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.example.account.R;
import com.example.account.api.RetrofitClient;
import com.example.account.models.ChangePassword;
import com.example.account.models.RdPxList;
import com.example.account.models.RdPxResponse;
import com.google.android.material.tabs.TabLayout;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TabsActivity extends AppCompatActivity {

    private String userId = "1";
    private  TabsAdapter tabsAdapter;
    private  Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
         toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        Bundle arguments = getIntent().getExtras();
        Log.d("userID", String.valueOf(arguments.get("mjId")));
        if(arguments!=null){
            userId =  String.valueOf(arguments.get("mjId"));
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Rd"));
        tabLayout.addTab(tabLayout.newTab().setText("Px"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager =(ViewPager)findViewById(R.id.view_pager);
         tabsAdapter = new TabsAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabsAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getListRdPx();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        } else if (item.getItemId() ==  R.id.action_exit){
            finish();
        }
        else if(item.getItemId() == R.id.action_change_password){
            openDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openDialog(){
        ChangePasswordDialog changePasswordDialog= new ChangePasswordDialog();
        Bundle bundle = new Bundle();
        Log.d("userId", userId);
        bundle.putString("userId", userId);
        changePasswordDialog.setArguments(bundle);
        changePasswordDialog.show(getSupportFragmentManager(), "change password dialog");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void getListRdPx (){
        Call<RdPxResponse> call = RetrofitClient.getApiService().getRdPx(userId);
        call.enqueue(new Callback<RdPxResponse>() {
            @Override
            public void onResponse(Call<RdPxResponse> call, Response<RdPxResponse> response) {
                final RdPxResponse result = response.body();

                tabsAdapter.pxFragment.setRdPxList(result.getPxList());
                tabsAdapter.rdFragment.setRdPxList(result.getRdList());

            }

            @Override
            public void onFailure(Call<RdPxResponse> call, Throwable t) {


                Toast.makeText(TabsActivity.this, "Неизвестная ошибка", Toast.LENGTH_LONG).show();

            }
        });
    }
}