package com.yytech.ochatclient;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.yytech.ochatclient.util.MyFTPUtil;

import org.apache.commons.net.ftp.FTPClient;

public class FtpActivity extends AppCompatActivity {
    private FTPClient ftpClient;
    private String ftpHost="192.168.1.228";
    private  int ftpPort=21;
    private String ftpUser="anonymous";
    private String ftpPwd="abc@abc.com";
    private String ftpFileName="QQChat.zip";
    private String strLocalFile="/Downloads/QQChat.zip";
    private int progress;
    private Button upLoad;
    private Button downLoad;
    private Button stop;
    private ProgressBar bar;
    private static boolean isBreak=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp);
        upLoad= (Button) findViewById(R.id.upload);
        downLoad= (Button) findViewById(R.id.download);
        stop= (Button) findViewById(R.id.stop);
        bar= (ProgressBar) findViewById(R.id.bar);
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
                myFTPUtil.downLoad(ftpHost,ftpPort,ftpUser,ftpPwd,ftpFileName,strLocalFile);
                myFTPUtil.handler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what==0x123){
                            progress=msg.getData().getInt("progress");
                            myFTPUtil.isBreak=isBreak;
                            bar.setProgress(progress);
                        }
                    }
                };
            }
        });
        upLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FtpActivity.this,FilePathList.class);
                startActivity(intent);
            }
        });
    }
}
