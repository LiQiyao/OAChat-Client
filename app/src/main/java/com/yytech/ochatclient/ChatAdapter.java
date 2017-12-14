package com.yytech.ochatclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.data.ChatLog;
import com.yytech.ochatclient.util.MyFTPUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Trt on 2017/11/28.
 */

public class ChatAdapter extends BaseAdapter {
    private Long myId;
    private Context context;
    private List<ChatLog> chatLogs;
    public ChatAdapter(Context context, List<ChatLog> chatLogs,Long myId) {
        super();
        this.context = context;
        this.chatLogs = chatLogs;
        this.myId=myId;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return chatLogs.size();
    }

    @Override
    public Object getItem(int position) {
        return chatLogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (chatLogs==null)
            return null;
        ChatLog chatLog=chatLogs.get(position);
        //右边
        if(chatLog.getSenderId().equals(myId)){
            view=View.inflate(context, R.layout.item_chat_right, null);
            TextView msgTime= (TextView) view.findViewById(R.id.message_time);
            SimpleDateFormat lsdFormat = new SimpleDateFormat("MM-dd HH:mm");
            Date date=new Date(chatLog.getReceiverId());
            String lStrDate = lsdFormat.format(date);
            msgTime.setText(lStrDate);
            if (chatLog.getContentType()== Const.ChatLogContentType.FILE)
            {
                final String str=chatLog.getContent();
                    final int i=str.lastIndexOf(".");
                    final int j=str.lastIndexOf("/")+1;
                    //图片处理
                    if(str.substring(i).equals(".png")||str.substring(i).equals(".jpg")) {
                        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.chat_right);
                        layout.setBackgroundResource(R.drawable.pop_bg);
                        ImageView imageView = (ImageView) view.findViewById(R.id.chat_image);
                        System.out.println("===" + str.substring(i));
                        imageView.setImageBitmap(getDiskBitmap(str));
                        RelativeLayout.LayoutParams params;
                        params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                        imageView.setLayoutParams(params);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LayoutInflater inflater = LayoutInflater.from(context);
                                View imgEntryView = inflater.inflate(R.layout.dialog_photo_entry, null); // 加载自定义的布局文件
                                final AlertDialog dialog = new AlertDialog.Builder(context).create();
                                ImageView img = (ImageView) imgEntryView.findViewById(R.id.large_image);
                                img.setImageBitmap(getDiskBitmap(str));
                                dialog.setView(imgEntryView); // 自定义dialog
                                dialog.show();
                                // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
                                imgEntryView.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View paramView) {
                                        dialog.cancel();
                                    }
                                });
                            }
                        });
                    }
                    //文件处理
                    else {
                        ((TextView) view.findViewById(R.id.tv_me_chat_message)).setText(str.substring(j));
                        ImageView imageView = (ImageView) view.findViewById(R.id.chat_image);
                        imageView.setImageResource(R.drawable.file);
                        RelativeLayout.LayoutParams params;
                        params= (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                        params.height=300;
                        params.width=300;
                        imageView.setLayoutParams(params);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(context,DownLoadActivity.class);
                                Bundle bundle=new Bundle();
                                bundle.putString("ftpFileName",str.substring(j));
                                bundle.putString("strLocalFile",str);
                                bundle.putString("status","myFile");
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            }
                        });
                    }
            }
            //文本处理
            else
                ((TextView)view.findViewById(R.id.tv_me_chat_message)).setText(chatLog.getContent());
        }
        //左边
        else{
            view=View.inflate(context,R.layout.item_chat_left,null);
            TextView msgTime= (TextView) view.findViewById(R.id.message_time);
            SimpleDateFormat lsdFormat = new SimpleDateFormat("MM-dd HH:mm");
            Date date=new Date(chatLog.getReceiverId());
            String lStrDate = lsdFormat.format(date);
            msgTime.setText(lStrDate);
            if (chatLog.getContentType()== Const.ChatLogContentType.FILE) {
                final String str1 = chatLog.getContent();
                int i = str1.lastIndexOf(".");
                final int j = str1.lastIndexOf("/") + 1;
                //图片处理
                if (str1.substring(i).equals(".png")||str1.substring(i).equals(".jpg")) {
                    RelativeLayout layout= (RelativeLayout) view.findViewById(R.id.chat_left);
                    layout.setBackgroundResource(R.drawable.pop_bg);
                    File file=new File(getSDPath()+"/OAChat/DownLoad/"+str1.substring(j));
                    ImageView imageView= (ImageView) view.findViewById(R.id.chat_image);
                    if (file.exists()){
                        Bitmap bitmap=getDiskBitmap(getSDPath()+"/OAChat/DownLoad/"+str1.substring(j));
                        imageView.setImageBitmap(bitmap);
                    }
                    else{
                        MyFTPUtil myFTPUtil=new MyFTPUtil();
                        myFTPUtil.downLoadImage(Const.ftpHost,Const.ftpPort,Const.ftpUser,Const.ftpPwd,str1.substring(j),getSDPath()+"/OAChat/DownLoad/");
                        while (file.exists()) {
                            Bitmap bitmap = getDiskBitmap(getSDPath() + "/OAChat/DownLoad/" + str1.substring(j));
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                    RelativeLayout.LayoutParams params;
                    params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                    imageView.setLayoutParams(params);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater inflater = LayoutInflater.from(context);
                            View imgEntryView = inflater.inflate(R.layout.dialog_photo_entry, null); // 加载自定义的布局文件
                            final AlertDialog dialog = new AlertDialog.Builder(context).create();
                            ImageView img = (ImageView) imgEntryView.findViewById(R.id.large_image);
                            img.setImageBitmap(getDiskBitmap(getSDPath() + "/OAChat/DownLoad/" + str1.substring(j)));
                            dialog.setView(imgEntryView); // 自定义dialog
                            dialog.show();
                            // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
                            imgEntryView.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View paramView) {
                                    dialog.cancel();
                                }
                            });
                        }
                    });
                }
//                //文件处理
                else {
                    ((TextView) view.findViewById(R.id.tv_chat_me_message)).setText(str1.substring(j));
                    System.out.println("===1111111111111" + str1.substring(j));
                    ImageView imageView = (ImageView) view.findViewById(R.id.chat_image);
                    imageView.setImageResource(R.drawable.file);
                    RelativeLayout.LayoutParams params;
                    params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                    params.height = 300;
                    params.width = 300;
                    imageView.setLayoutParams(params);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(context,DownLoadActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putString("ftpFileName",str1.substring(j));
                            bundle.putString("strLocalFile","");
                            bundle.putString("status","1");
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }
                    });
                }
            }
            else {
                //文本处理
                ((TextView) view.findViewById(R.id.tv_chat_me_message)).setText(chatLog.getContent());
            }
        }
        return view;
    }
//    public interface onItemImageListener {
//        void onImageClick(int i);
//    }

//    private onItemImageListener mOnItemImageListener;

//    public void setOnItemImageClickListener(onItemImageListener mOnItemImageListener) {
//        this.mOnItemImageListener = mOnItemImageListener;
//    }
private Bitmap getDiskBitmap(String pathString)
{
    Bitmap bitmap = null;
    try
    {
        File file = new File(pathString);
        if(file.exists())
        {
            bitmap = BitmapFactory.decodeFile(pathString);
        }
    } catch (Exception e)
    {
        // TODO: handle exception
    }


    return bitmap;
}
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
}
