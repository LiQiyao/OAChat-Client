package com.yytech.ochatclient.util;

/**
 * Created by Trt on 2017/12/12.
 */


import java.io.File;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

    public class CallOtherOpeanFile {
        /**
         *
         * <code>openFile</code>
         * @description: TODO(打开附件)
         * @param context
         * @param file
         * @since   2012-5-19    liaoyp
         */
        public void openFile(Context context,File file){
            try{
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //设置intent的Action属性
                intent.setAction(Intent.ACTION_VIEW);
                //获取文件file的MIME类型
                String type = getMIMEType(file);
                System.out.println("===1111111"+type);
                //设置intent的data和Type属性。
                Uri uri=null;
                if (Build.VERSION.SDK_INT >= 24) {
                    System.out.println("===type"+type);
                    uri = FileProvider.getUriForFile(context,context.getPackageName()+".fileprovider",file);
                    intent.setDataAndType(uri,type);
                }
                else {
                    intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
                }
                //跳转
                context.startActivity(intent);
//      Intent.createChooser(intent, "请选择对应的软件打开该附件！");
            }catch (ActivityNotFoundException e) {
                // TODO: handle exception
                Toast.makeText(context, "sorry附件不能打开，请下载相关软件！",Toast.LENGTH_SHORT).show();
            }
        }
        private String getMIMEType(File file) {

            String type="*/*";
            String fName = file.getName();
            //获取后缀名前的分隔符"."在fName中的位置。
            int dotIndex = fName.lastIndexOf(".");
            if(dotIndex < 0){
                return type;
            }
        /* 获取文件的后缀名*/
            String end=fName.substring(dotIndex,fName.length()).toLowerCase();
            if(end=="")return type;
            //在MIME和文件类型的匹配表中找到对应的MIME类型。
            for(int i=0;i<MIME_MapTable.length;i++){
                if(end.equals(MIME_MapTable[i][0]))
                    type = MIME_MapTable[i][1];
            }
            return type;
        }
        // 可以自己随意添加
        private String [][]  MIME_MapTable={
                //{后缀名，MIME类型}
                {".3gp",    "video/3gpp"},
                {".apk",    "application/vnd.android.package-archive"},
                {".asf",    "video/x-ms-asf"},
                {".avi",    "video/x-msvideo"},
                {".bin",    "application/octet-stream"},
                {".bmp",    "image/bmp"},
                {".c",  "text/plain"},
                {".class",  "application/octet-stream"},
                {".conf",   "text/plain"},
                {".cpp",    "text/plain"},
                {".doc",    "application/msword"},
                {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
                {".xls",    "application/vnd.ms-excel"},
                {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
                {".exe",    "application/octet-stream"},
                {".gif",    "image/gif"},
                {".gtar",   "application/x-gtar"},
                {".gz", "application/x-gzip"},
                {".h",  "text/plain"},
                {".htm",    "text/html"},
                {".html",   "text/html"},
                {".jar",    "application/java-archive"},
                {".java",   "text/plain"},
                {".jpeg",   "image/jpeg"},
                {".jpg",    "image/jpeg"},
                {".js", "application/x-javascript"},
                {".log",    "text/plain"},
                {".m3u",    "audio/x-mpegurl"},
                {".m4a",    "audio/mp4a-latm"},
                {".m4b",    "audio/mp4a-latm"},
                {".m4p",    "audio/mp4a-latm"},
                {".m4u",    "video/vnd.mpegurl"},
                {".m4v",    "video/x-m4v"},
                {".mov",    "video/quicktime"},
                {".mp2",    "audio/x-mpeg"},
                {".mp3",    "audio/x-mpeg"},
                {".mp4",    "video/mp4"},
                {".mpc",    "application/vnd.mpohun.certificate"},
                {".mpe",    "video/mpeg"},
                {".mpeg",   "video/mpeg"},
                {".mpg",    "video/mpeg"},
                {".mpg4",   "video/mp4"},
                {".mpga",   "audio/mpeg"},
                {".msg",    "application/vnd.ms-outlook"},
                {".ogg",    "audio/ogg"},
                {".pdf",    "application/pdf"},
                {".png",    "image/png"},
                {".pps",    "application/vnd.ms-powerpoint"},
                {".ppt",    "application/vnd.ms-powerpoint"},
                {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
                {".prop",   "text/plain"},
                {".rc", "text/plain"},
                {".rmvb",   "audio/x-pn-realaudio"},
                {".rar",  "application/x-gzip"},
                {".rtf",    "application/rtf"},
                {".sh", "text/plain"},
                {".tar",    "application/x-tar"},
                {".tgz",    "application/x-compressed"},
                {".txt",    "text/plain"},
                {".wav",    "audio/x-wav"},
                {".wma",    "audio/x-ms-wma"},
                {".wmv",    "audio/x-ms-wmv"},
                {".wps",    "application/vnd.ms-works"},
                {".xml",    "text/plain"},
                {".z",  "application/x-compress"},
                {".zip",    "application/x-zip-compressed"},
                {"",        "*/*"}
        };


    }
