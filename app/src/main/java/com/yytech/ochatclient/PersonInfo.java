package com.yytech.ochatclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.yytech.ochatclient.common.Const;

import static com.yytech.ochatclient.R.id.person_info_address;
import static com.yytech.ochatclient.R.id.person_info_tel;

public class PersonInfo extends AppCompatActivity implements Const.Status{
    private EditText editText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_person_info);

        TextView nickNameText = (TextView) findViewById(R.id.person_info_nickname);
        TextView genderText = (TextView) findViewById(R.id.person_info_gender);
        TextView addressText = (TextView) findViewById(person_info_address);
        TextView telText = (TextView)findViewById(person_info_tel);

//        nickNameText.setText(loginMsg.getData().getSelf().getNickName());
//        genderText.setText(loginMsg.getData().getSelf().getGender());
//        addressText.setText(loginMsg.getData().getSelf().getAddress());
//        telText.setText(loginMsg.getData().getSelf().getTelephoneNumber());





    }

}
