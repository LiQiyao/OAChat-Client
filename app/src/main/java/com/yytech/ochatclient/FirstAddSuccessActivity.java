package com.yytech.ochatclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.githang.statusbar.StatusBarCompat;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.OnlineDTO;

public class FirstAddSuccessActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        setContentView(R.layout.activity_first_add_success);

        Intent intent=new Intent(FirstAddSuccessActivity.this,MainActivity.class);
        Bundle bundle=new Bundle();
        preferences = getSharedPreferences("userIdAndToken",MODE_PRIVATE);
        editor = preferences.edit();
        String userId = preferences.getString("userId",null);
        String token = preferences.getString("token",null);
        MessageDTO<OnlineDTO> onlineMsg=new MessageDTO<OnlineDTO>();
        onlineMsg.setToken(token);
        onlineMsg.setUserId(Long.valueOf(userId));
        bundle.putSerializable("onlineMsg",onlineMsg);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }
}
