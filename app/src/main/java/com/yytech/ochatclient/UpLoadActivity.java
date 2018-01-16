package com.yytech.ochatclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.util.MyFTPUtil;

public class UpLoadActivity extends Activity {

    private String ftpHost=Const.ftpHost;
    private  int ftpPort=21;
    private String ftpUser= Const.ftpUser;
    private String ftpPwd=Const.ftpPwd;
    private String ftpFileName;
    private String strLocalFile;
    private String path;
    private String fileName;
    private int progress;
    private Button upLoad;
    private Button stop;
    private TextView name;
    private ImageView back;
    private ProgressBar bar;
    private static boolean isBreak=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        final Intent intent=getIntent();
        path=intent.getStringExtra("path");
        fileName=intent.getStringExtra("name");
        strLocalFile=path+
                "/"+fileName;
        ftpFileName=fileName;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        back= (ImageView) findViewById(R.id.back);
        upLoad= (Button) findViewById(R.id.upload);
        stop= (Button) findViewById(R.id.stop);
        bar= (ProgressBar) findViewById(R.id.bar);
        name= (TextView) findViewById(R.id.name);
        name.setText(fileName);

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
        upLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBreak=false;
                final MyFTPUtil myUFTPUtil=new MyFTPUtil();
                myUFTPUtil.upLoad(ftpHost,ftpPort,ftpUser,ftpPwd,ftpFileName,strLocalFile);
                myUFTPUtil.handler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what==0x234){
                            progress=msg.getData().getInt("progress");
                            myUFTPUtil.isBreak=isBreak;
                            bar.setProgress(progress);
                            if (progress==100){
                                Intent intent1=new Intent(UpLoadActivity.this,ChatActivity.class);
                                Bundle bundle=new Bundle();
                                bundle.putString("strLocalFile",strLocalFile);
                                intent1.putExtras(bundle);
                                startActivity(intent1);
                            }
                        }
                    }
                };
            }
        });
    }
}
