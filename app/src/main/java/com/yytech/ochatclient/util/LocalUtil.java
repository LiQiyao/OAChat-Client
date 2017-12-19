package com.yytech.ochatclient.util;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by admin on 2017/12/19.
 */

public class LocalUtil {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public String getUserId(Activity activity){
        preferences = activity.getSharedPreferences("userIdAndToken",activity.MODE_PRIVATE);
        String userId = preferences.getString("userId",null);
        return userId;
    }

    public String getToken(Activity activity){
        preferences = activity.getSharedPreferences("userIdAndToken",activity.MODE_PRIVATE);
        String token = preferences.getString("token",null);
        return token;
    }

    public void commitUserIdAndToken(Activity activity,String userId,String token){
        preferences = activity.getSharedPreferences("userIdAndToken",activity.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("userId",userId);
        editor.putString("token",token);
        editor.commit();
    }

    public void removeUserIdAndToken(Activity activity){
        preferences = activity.getSharedPreferences("userIdAndToken",activity.MODE_PRIVATE);
        editor = preferences.edit();
        editor.clear();
        editor.commit();
    }


}
