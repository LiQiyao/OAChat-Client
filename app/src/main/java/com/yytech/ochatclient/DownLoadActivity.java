package com.yytech.ochatclient;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.util.CallOtherOpeanFile;
import com.yytech.ochatclient.util.MyFTPUtil;

import java.io.File;

/**
 * Created by Trt on 2017/12/10.
 */

public class DownLoadActivity extends Activity {
    private String ftpHost=Const.ftpHost;
    private  int ftpPort=21;
    private String ftpUser=Const.ftpUser;
    private String ftpPwd=Const.ftpPwd;
    private String ftpFileName;
    private String strLocalFile;
    private String path= Const.downLoadPath;
    private int progress;
    private Button downLoad;
    private Button stop;
    private TextView name;
    private Button openPath;
    private ProgressBar bar;
    private static boolean isBreak=false;
    private static File file;
    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if   (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent=getIntent();
        ftpFileName=intent.getStringExtra("ftpFileName");
        strLocalFile=intent.getStringExtra("strLocalFile");
        path=path;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        downLoad= (Button) findViewById(R.id.download);
        stop= (Button) findViewById(R.id.stop);
        bar= (ProgressBar) findViewById(R.id.bar);
        name= (TextView) findViewById(R.id.name);
        openPath= (Button) findViewById(R.id.openPath);
        name.setText(ftpFileName);
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x123) {
                    downLoad.setVisibility(View.GONE);
                    stop.setVisibility(View.GONE);
                    bar.setVisibility(View.GONE);
                    openPath.setVisibility(View.VISIBLE);
                }
            }
        };
        if (!intent.getStringExtra("status").equals("myFile")) {
            File file1 = new File(path + "/" + ftpFileName);
            if (file1.exists()) {
                downLoad.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                bar.setVisibility(View.GONE);
                openPath.setVisibility(View.VISIBLE);
                file=file1;
            }
            openPath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("===click");
                    CallOtherOpeanFile otherOpeanFile = new CallOtherOpeanFile();
                    otherOpeanFile.openFile(DownLoadActivity.this, file);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
//                startActivity(Intent.createChooser(intent,"选择浏览工具"));
                }
            });
        }
        else {
            File file1= new File(strLocalFile);
            File file2=new File(getSDPath()+"/OAChat/DownLoad/"+ftpFileName);
            if (file1.exists()) {
                downLoad.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                bar.setVisibility(View.GONE);
                openPath.setVisibility(View.VISIBLE);
                file=file1;
            }
            else if (file2.exists()){
                downLoad.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                bar.setVisibility(View.GONE);
                openPath.setVisibility(View.VISIBLE);
                file=file2;
            }
            openPath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CallOtherOpeanFile otherOpeanFile = new CallOtherOpeanFile();
                    otherOpeanFile.openFile(DownLoadActivity.this, file);
                }
            });
        }
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBreak==false)
                    isBreak=true;
                else
                    isBreak=false;
            }
        });
        downLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBreak=false;
                final MyFTPUtil myFTPUtil=new MyFTPUtil();
                myFTPUtil.downLoad(ftpHost,ftpPort,ftpUser,ftpPwd,ftpFileName,path);
                System.out.println("===download");
                myFTPUtil.dHandler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what==0x123){
                            progress=msg.getData().getInt("progress");
                            myFTPUtil.isBreak=isBreak;
                            bar.setProgress(progress);
                            if (progress==100)
                                handler.sendEmptyMessage(0x123);
                        }
                    }
                };
            }
        });
    }
}

