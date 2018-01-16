package com.yytech.ochatclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.util.CallOtherOpeanFile;
import com.yytech.ochatclient.util.MyFTPUtil;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.IOException;

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
    private ImageView back;
    private static boolean isBreak=false;
    private static File file;
    private FTPClient ftpClient;
    private String code="iso-8859-1";
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
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        back= (ImageView) findViewById(R.id.back);
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
        final Handler pHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what==0x123)
                bar.setProgress(msg.getData().getInt("progress"));
                if (msg.what==0x456){
                    downLoad.setVisibility(View.GONE);
                    stop.setVisibility(View.GONE);
                    bar.setVisibility(View.GONE);
                    openPath.setVisibility(View.VISIBLE);
                }
            }
        };
        if (intent.getStringExtra("status").equals("myFile")) {
            File file1 = new File(strLocalFile);
            File file2=new File(getSDPath()+"/OAChat/DownLoad/"+ftpFileName);
            if (file1.exists()) {
                downLoad.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                bar.setVisibility(View.GONE);
                openPath.setVisibility(View.VISIBLE);
                file=file1;
            }
            else if(file2.exists()){
                downLoad.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                bar.setVisibility(View.GONE);
                openPath.setVisibility(View.VISIBLE);
                file=file2;
            }
            openPath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("===click");
                    CallOtherOpeanFile otherOpeanFile = new CallOtherOpeanFile();
                    otherOpeanFile.openFile(DownLoadActivity.this, file);
                }
            });
        }
        else {
            File file1= new File(strLocalFile);
            final File file2=new File(getSDPath()+"/OAChat/DownLoad/"+ftpFileName);
            if (file1.exists()) {
                downLoad.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                bar.setVisibility(View.GONE);
                openPath.setVisibility(View.VISIBLE);
                file=file1;
            }
            else if (file2.exists()){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ftpClient=new FTPClient();
                            ftpClient.connect(ftpHost,ftpPort);// 连接FTP服务器
                            ftpClient.setControlEncoding(code);
                        } catch (Exception e) {
                            System.out.println("===Open Failed"+e);
                            return;
                        }
                        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                            return;
                        }
                        try {
                            if (ftpClient.login(ftpUser, ftpPwd)) {
                                ftpClient.enterLocalPassiveMode();
                                // 设置以二进制方式传输
                                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                                // 检查远程文件是否存在
                                FTPFile[] files = ftpClient.listFiles(new String(
                                        ftpFileName.getBytes("GBK"), code));
                                System.out.println("===ftpFileName"+ftpFileName);
                                if (file2.length()>=files[0].getSize()){
                                    Message msg1=new Message();
                                    msg1.what=0x456;
                                    pHandler.sendMessage(msg1);
                                    file=file2;
                                }
                                int per = (int) (files[0].getSize() / 100);
                                long nowProcess = file2.length() / per;
                                progress = (int) nowProcess;
                                Message msg=new Message();
                                msg.what=0x123;
                                Bundle bundle=new Bundle();
                                bundle.putInt("progress",progress);
                                msg.setData(bundle);
                                pHandler.sendMessage(msg);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            openPath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CallOtherOpeanFile otherOpeanFile = new CallOtherOpeanFile();
                    otherOpeanFile.openFile(DownLoadActivity.this, file);
                }
            });
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

