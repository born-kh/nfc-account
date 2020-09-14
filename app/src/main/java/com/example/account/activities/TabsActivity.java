package com.example.account.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TabsActivity extends AppCompatActivity {

    private String mjId;
    private  TabsAdapter tabsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle arguments = getIntent().getExtras();
        Log.d("userID", String.valueOf(arguments.get("mjId")));
        if(arguments!=null){
            mjId =  arguments.get("mjId").toString();
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Затраты"));
        tabLayout.addTab(tabLayout.newTab().setText("Пополнение"));
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
        }

        return super.onOptionsItemSelected(item);
    }


    public void getListRdPx (){
        Call<RdPxResponse> call = RetrofitClient.getApiService().getRdPx(mjId);
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