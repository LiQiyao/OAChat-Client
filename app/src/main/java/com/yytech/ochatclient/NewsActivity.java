package com.yytech.ochatclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.DeleteFriendSuccessDTO;
import com.yytech.ochatclient.dto.data.OnlineDTO;

public class NewsActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        setContentView(R.layout.activity_news);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle detailBundle = getIntent().getExtras();
                    DeleteFriendSuccessDTO deleteFriendSuccessDTO = (DeleteFriendSuccessDTO) detailBundle.getSerializable("deleteFriendSuccessDTO");
                    TextView textView = (TextView) findViewById(R.id.news_text);
                    textView.setText("很遗憾的通知你，你被"+deleteFriendSuccessDTO.getDeleteFriendId()+"删除好友关系！");
                    Thread.sleep(3000);
                    Intent intent=new Intent(NewsActivity.this,MainActivity.class);
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
