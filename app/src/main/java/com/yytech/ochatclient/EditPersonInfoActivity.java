package com.yytech.ochatclient;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.githang.statusbar.StatusBarCompat;

public class EditPersonInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        setContentView(R.layout.f_edit_person_info);
    }

    public void back(View source){
        finish();
    }

    //editText点击事件：由不可编辑到可编辑
    public void changeEditAble(View source){
        source.setFocusable(true);
        source.setFocusableInTouchMode(true);
    }


}
