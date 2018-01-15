package com.yytech.ochatclient.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by admin on 2018/1/12.
 */

public class addFriendRequestBroad extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"收到一条好友请求！",Toast.LENGTH_SHORT).show();


    }
}
