package com.yytech.ochatclient.adapter;

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

import com.yytech.ochatclient.DownLoadActivity;
import com.yytech.ochatclient.R;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.data.ChatLog;
import com.yytech.ochatclient.util.BitmapCache;
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
    private String myIcon;
    private String friendIcon;
    private List<ChatLog> chatLogs;
    private BitmapCache bitmapCache=new BitmapCache();
    private int[] heads=new int[]{R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8};
    public ChatAdapter(Context context, List<ChatLog> chatLogs,Long myId,String myIcon,String friendIcon) {
        super();
        this.context = context;
        this.chatLogs = chatLogs;
        this.myId=myId;
        this.myIcon=myIcon;
        this.friendIcon=friendIcon;
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
            ImageView head= (ImageView) view.findViewById(R.id.head);
            if (bitmapCache.getBitmap(heads[Integer.parseInt(myIcon)-1],context)!=null) {
                head.setImageBitmap(bitmapCache.getBitmap(heads[Integer.parseInt(myIcon) - 1], context));
            }
            else {
                head.setImageResource(heads[Integer.parseInt(friendIcon)-1]);
            }
            SimpleDateFormat lsdFormat = new SimpleDateFormat("MM-dd HH:mm");
            Date date=new Date(chatLog.getSendTime());
            System.out.println("===date"+chatLog.getSendTime());
            String lStrDate = lsdFormat.format(date);
            msgTime.setText(lStrDate);
            if (chatLog.getContentType()== Const.ChatLogContentType.FILE)
            {
                final String str=chatLog.getContent();
                    final int j=str.lastIndexOf("/")+1;
                if(str.contains(".")) {
                    final int i = str.lastIndexOf(".");
                    //图片处理
                    if (str.substring(i).equals(".png") || str.substring(i).equals(".jpg")||
                            str.substring(i).equals(".bmp") || str.substring(i).equals(".tiff")||
                            str.substring(i).equals(".gif")  || str.substring(i).equals(".pcd")|| str.substring(i).equals(".PNG")
                            || str.substring(i).equals(".JPG")|| str.substring(i).equals(".GIF")|| str.substring(i).equals(".BMP")
                            || str.substring(i).equals(".PCD")|| str.substring(i).equals(".TIFF")){
                        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.chat_right);
                        layout.setBackgroundResource(R.drawable.pop_bg);
                        ImageView imageView = (ImageView) view.findViewById(R.id.chat_image);
                        imageView.setImageResource(R.drawable.loading);
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
                    else {
                        ((TextView) view.findViewById(R.id.tv_me_chat_message)).setText(str.substring(j));
                        ImageView imageView = (ImageView) view.findViewById(R.id.chat_image);
                        imageView.setImageResource(R.drawable.file);
//                        imageView.setImageBitmap(bitmapCache.getBitmap(R.drawable.file,context));
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
            ImageView head= (ImageView) view.findViewById(R.id.head);
            if (bitmapCache.getBitmap(heads[Integer.parseInt(friendIcon)-1],context)!=null) {
                head.setImageBitmap(bitmapCache.getBitmap(heads[Integer.parseInt(friendIcon) - 1], context));
            }
            else {
            head.setImageResource(heads[Integer.parseInt(friendIcon)-1]);
            }
            SimpleDateFormat lsdFormat = new SimpleDateFormat("MM-dd HH:mm");
            Date date=new Date(chatLog.getSendTime());
            String lStrDate = lsdFormat.format(date);
            msgTime.setText(lStrDate);
            if (chatLog.getContentType()== Const.ChatLogContentType.FILE) {
                final String str1 = chatLog.getContent();
                final int j = str1.lastIndexOf("/") + 1;
                if (str1.contains(".")) {
                    int i = str1.lastIndexOf(".");
                    //图片处理
                    if (str1.substring(i).equals(".png") || str1.substring(i).equals(".jpg")||
                            str1.substring(i).equals(".bmp") || str1.substring(i).equals(".tiff")||
                            str1.substring(i).equals(".gif") || str1.substring(i).equals(".pcd")|| str1.substring(i).equals(".PNG")
                            || str1.substring(i).equals(".JPG")|| str1.substring(i).equals(".GIF")|| str1.substring(i).equals(".BMP")
                            || str1.substring(i).equals(".PCD")|| str1.substring(i).equals(".TIFF")) {
                        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.chat_left);
                        layout.setBackgroundResource(R.drawable.pop_bg);
                        File file = new File(getSDPath() + "/OAChat/DownLoad/" + str1.substring(j));
                        ImageView imageView = (ImageView) view.findViewById(R.id.chat_image);
                        imageView.setImageResource(R.drawable.loading);
                        if (file.exists()) {
                            Bitmap bitmap = getDiskBitmap(getSDPath() + "/OAChat/DownLoad/" + str1.substring(j));
                            imageView.setImageBitmap(bitmap);
                        } else {
                            MyFTPUtil myFTPUtil = new MyFTPUtil();
                            myFTPUtil.downLoadImage(Const.ftpHost, Const.ftpPort, Const.ftpUser, Const.ftpPwd, str1.substring(j), getSDPath() + "/OAChat/DownLoad/");
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
            bitmap=bitmapCache.getBitmapByPath(pathString,context);
//            bitmap = BitmapFactory.decodeFile(pathString);
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
