package com.yytech.ochatclient.common;

import android.os.Environment;

import java.io.File;

/**
 * Created by admin on 2017/12/2.
 */

public class Const {

    public static final String IP = "120.78.67.51";
    public static final String ftpHost="120.78.67.51";
//    public static final String IP = "172.18.16.141";
//    public static final String ftpHost="172.18.16.141";
    public static final int ftpPort=21;
    public static final String ftpUser="anonymous";
    public static final String ftpPwd="abc@abc.com";
    public static final String downLoadPath=getSDPath()+"/OAChat/DownLoad";
    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if   (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();

    }

    public static final int HTTP_PORT = 8080;
//    public static final int HTTP_PORT = 8088;

    public interface Status{
        int SUCCESS = 1;
        int FAILED = 0;
    }

    public interface ChatLogContentType{
        int TEXT=1;
        int FILE=2;
        int IMAGE=3;
    }

    public interface StatusDetail{
        String SUCCESS_DETAIL = "操作成功！";
        String FAILED_DETAIL = "操作失败！";
    }

    public interface Sign{
        int REQUEST = 1;
        int RESPONSE = 2;
        int NOTICE = 3;
    }

    public static final int PORT = 52621;

}
