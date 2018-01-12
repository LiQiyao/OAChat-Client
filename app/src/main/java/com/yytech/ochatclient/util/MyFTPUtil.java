package com.yytech.ochatclient.util;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Created by Trt on 2017/12/9.
 */

public class MyFTPUtil {
    private FTPClient ftpClient;
    public  volatile boolean isBreak=false;
    private int progress;
    private String code="iso-8859-1";
    public Handler handler;
    public Handler dHandler;
    public Handler imageHandle;
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
    public void downLoad(final String ftpHost, final int ftpPort, final String ftpUser, final String ftpPwd, final String ftpFileName, final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        ftpClient=new FTPClient();
                        ftpClient.connect(ftpHost,ftpPort);// 连接FTP服务器
                        ftpClient.setControlEncoding(code);
                    } catch (Exception e) {
//                        mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                        System.out.println("===Open Failed"+e);
                        return;
                    }
                    if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                        System.out.println("===11111111111");
                        return;
                    }
                    if (ftpClient.login(ftpUser, ftpPwd)) {
                        // 设置被动模式
                        ftpClient.enterLocalPassiveMode();
                        // 设置以二进制方式传输
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        // 检查远程文件是否存在
                        FTPFile[] files = ftpClient.listFiles(new String(
                                ftpFileName.getBytes("GBK"), code));
                        int per = (int) (files[0].getSize() / 100);
                        FileOutputStream output = null;
                        InputStream input = null;
                        long localSize = 0L;
                        if (files.length == 0) {// 文件不存在，下载失败
//                            mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                            System.out.println("===DOWNLOAD_FAILED");
                            return;
                        }
                        else {
                            // 开始下载
                            File f=new File(path);
                            if (!f.exists()){
                                f.mkdirs();
                            }
                            File file = new File(path+"/"+ftpFileName);
                            if (file.exists()) {
                                // 存在，开始续传
                                localSize = file.length();
                                if (localSize > files[0].getSize()) {// 下载完成
//                                    mHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                                    System.out.println("===DOWNLOAD_SUCCESS");
                                    return;
                                }
                                // 从本地文件上续传
                                output = new FileOutputStream(file, true);
                                // 将文件指向已下载的位置
                                ftpClient.setRestartOffset(localSize);
                                input = ftpClient.retrieveFileStream(new String(
                                        ftpFileName.getBytes("GBK"),code));
                                progress = (int) (localSize / per);
//                                mHandler.sendEmptyMessage(DOWNLOAD_UPDATE);
                                System.out.println("===DOWNLOAD_UPDATE");

                            }
                            else {// 直接下载
                                System.out.println("===download  "+per);
                                output = new FileOutputStream(file);
                                input = ftpClient.retrieveFileStream(new String(
                                        ftpFileName.getBytes("GBK"),code));
//                                mHandler.sendEmptyMessage(DOWNLOAD_START);
                                System.out.println("===DOWNLOAD_START");
                            }
                            if (input == null) {
//                                mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                                System.out.println("===222222");
                                System.out.println("===DOWNLOAD_FAILED");
                                return;
                            }
                            byte[] bytes = new byte[1024];
                            int c;
                            while ((c = input.read(bytes)) != -1) {
                                if (isBreak) {
                                    Log.i("xxx", "已停止下载！");
//                                    mHandler.sendEmptyMessage(DOWNLOAD_STOP);
                                    System.out.println("===DOWNLOAD_STOP");
                                    break;
                                }
//                                if (isBreak) {
//                                    Log.i("xxx", "已停止下载！");
////                                    mHandler.sendEmptyMessage(DOWNLOAD_STOP);
//                                    System.out.println("===DOWNLOAD_STOP");
//                                    break;
//                                }
                                output.write(bytes, 0, c);
                                localSize += c;
                                long nowProcess = localSize / per;

                                if (nowProcess > progress) {
                                    progress = (int) nowProcess;
                                    if (progress % 1 == 0) {
                                        Message msg=new Message();
                                        msg.what=0x123;
                                        Bundle bundle=new Bundle();
                                        bundle.putInt("progress",progress);
                                        msg.setData(bundle);
                                        dHandler.sendMessage(msg);
                                        Log.i("xxx", "下载进度：" + progress);
                                    }
//                                    mHandler.sendEmptyMessage(DOWNLOAD_UPDATE);
                                    System.out.println("===DOWNLOAD_UPDATE");
                                }
                            }
                            input.close();
                            output.close();
                            if (c <= 0) {// 下载完成
//                                mHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                                System.out.println("===DOWNLOAD_SUCCESS");
                            } else {
//                                mHandler.sendEmptyMessage(DOWNLOAD_STOP);
                                System.out.println("===DOWNLOAD_STOP");
                            }
                        }
                    }

                } catch (Exception e) {
                    try {
                        ftpClient.disconnect();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void upLoad(final String ftpHost, final int ftpPort, final String ftpUser, final String ftpPwd, final String ftpFileName, final String strLocalFile){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        ftpClient=new FTPClient();
                        System.out.println("===Open");
                        ftpClient.connect(ftpHost,ftpPort);// 连接FTP服务器
                        System.out.println("===Ope11n");
                        ftpClient.setControlEncoding(code);
                    } catch (Exception e) {
                        System.out.println("===Open Failed"+e);
                        return;
                    }
                    if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                        System.out.println("===11111111111");
                        return;
                    }
                    if (ftpClient.login(ftpUser, ftpPwd)) {
                        // 设置被动模式
                        ftpClient.enterLocalPassiveMode();
                        // 设置以二进制方式传输
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        // 检查远程文件是否存在
                        FTPFile[] files = ftpClient.listFiles(new String(
                                ftpFileName.getBytes("GBK"), code));
                        System.out.println("==="+ftpFileName.getBytes("GBK"));
                        File file = new File(strLocalFile);
                        int per = (int) (file.length() / 100);
                        OutputStream output = null;
                        RandomAccessFile raf ;
                        long FTPSize = 0L;
                        if (!file.exists()) {// 文件不存在，下载失败
//                            mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                            System.out.println("===11111111"+strLocalFile+getSDPath());
                            System.out.println("===UPLOAD_FAILED");
                            return;
                        }
                        else {
                            // 开始上传
                            FileInputStream fis = new FileInputStream(file);
                            if (files.length > 0) {
                                // 存在，开始续传
                                FTPSize = files[0].getSize();
                                if (FTPSize > file.length()) {
                                    // 上传完成
//                                    mHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                                    System.out.println("===UPLOAD_SUCCESS");
                                    return;
                                }
                                // 续传
                                // 将文件指向已下载的位置
                                ftpClient.setRestartOffset(FTPSize);
                                raf = new RandomAccessFile(strLocalFile, "r");
                                raf.seek(FTPSize);
                                output = ftpClient.appendFileStream(new String(
                                        ftpFileName.getBytes("GBK"), code));
                                progress = (int) (FTPSize / per);
//                                mHandler.sendEmptyMessage(DOWNLOAD_UPDATE);
                                System.out.println("===UPLOAD_UPDATE");

                            } else {
                                // 直接下载
                                output = ftpClient.appendFileStream(new String(
                                        ftpFileName.getBytes("GBK"), code));
                                raf = new RandomAccessFile(strLocalFile, "r");
                                raf.seek(0L);
//                                mHandler.sendEmptyMessage(DOWNLOAD_START);
                                System.out.println("===UPLOAD_START");
                            }
                            if (output == null || raf == null) {
//                                mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                                System.out.println("===222222");
                                System.out.println("===UPLOAD_FAILED");
                                return;
                            }
                            byte[] bytes = new byte[1024];
                            int c;
                            while ((c = raf.read(bytes)) != -1) {
                                if (isBreak) {
                                    Log.i("xxx", "已停止上传！");
//                                    mHandler.sendEmptyMessage(DOWNLOAD_STOP);
                                    System.out.println("===DOWNLOAD_STOP");
                                    break;
                                }
                                output.write(bytes, 0, c);
                                FTPSize += c;
                                long nowProcess = FTPSize / per;

                                if (nowProcess > progress) {
                                    progress = (int) nowProcess;
                                    if (progress % 1 == 0) {
                                        Message msg=new Message();
                                        msg.what=0x234;
                                        Bundle bundle=new Bundle();
                                        bundle.putInt("progress",progress);
                                        msg.setData(bundle);
                                        handler.sendMessage(msg);
                                        Log.i("xxx", "上传进度：" + progress);
                                    }
//                                    mHandler.sendEmptyMessage(DOWNLOAD_UPDATE);
                                    System.out.println("===UPLOAD_UPDATE");
                                }
                            }
                            raf.close();
                            output.close();
                            if (c <= 0) {// 下载完成
//                                mHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                                System.out.println("===UPLOAD_SUCCESS");
                            } else {
//                                mHandler.sendEmptyMessage(DOWNLOAD_STOP);
                                System.out.println("===UPLOAD_STOP");
                            }
                        }
                    }

                } catch (Exception e) {
                    try {
                        ftpClient.disconnect();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }).start();

    }
    public void downLoadImage(final String ftpHost, final int ftpPort, final String ftpUser, final String ftpPwd, final String ftpFileName, final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        ftpClient=new FTPClient();
                        ftpClient.connect(ftpHost,ftpPort);// 连接FTP服务器
                        ftpClient.setControlEncoding(code);
                    } catch (Exception e) {
//                        mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                        System.out.println("===Open Failed"+e);
                        return;
                    }
                    if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                        System.out.println("===11111111111");
                        return;
                    }
                    if (ftpClient.login(ftpUser, ftpPwd)) {
                        // 设置被动模式
                        ftpClient.enterLocalPassiveMode();
                        // 设置以二进制方式传输
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        // 检查远程文件是否存在
                        FTPFile[] files = ftpClient.listFiles(new String(
                                ftpFileName.getBytes("GBK"), code));
                        int per = (int) (files[0].getSize() / 100);
                        FileOutputStream output = null;
                        InputStream input = null;
                        long localSize = 0L;
                        if (files.length == 0) {// 文件不存在，下载失败
//                            mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                            System.out.println("===DOWNLOAD_FAILED");
                            return;
                        }
                        else {
                            // 开始下载
                            File f=new File(path);
                            if (!f.exists()){
                                f.mkdirs();
                            }
                            File file = new File(path+"/"+ftpFileName);
                            if (file.exists()) {
                                // 存在，开始续传
                                localSize = file.length();
                                if (localSize > files[0].getSize()) {// 下载完成
//                                    mHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                                    System.out.println("===DOWNLOAD_SUCCESS");
                                    return;
                                }
                                // 从本地文件上续传
                                output = new FileOutputStream(file, true);
                                // 将文件指向已下载的位置
                                ftpClient.setRestartOffset(localSize);
                                input = ftpClient.retrieveFileStream(new String(
                                        ftpFileName.getBytes("GBK"),code));
                                progress = (int) (localSize / per);
//                                mHandler.sendEmptyMessage(DOWNLOAD_UPDATE);
                                System.out.println("===DOWNLOAD_UPDATE");

                            }
                            else {// 直接下载
                                System.out.println("===download  "+per);
                                output = new FileOutputStream(file);
                                input = ftpClient.retrieveFileStream(new String(
                                        ftpFileName.getBytes("GBK"),code));
//                                mHandler.sendEmptyMessage(DOWNLOAD_START);
                                System.out.println("===DOWNLOAD_START");
                            }
                            if (input == null) {
//                                mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                                System.out.println("===222222");
                                System.out.println("===DOWNLOAD_FAILED");
                                return;
                            }
                            byte[] bytes = new byte[1024];
                            int c;
                            while ((c = input.read(bytes)) != -1) {
                                if (isBreak) {
                                    Log.i("xxx", "已停止下载！");
//                                    mHandler.sendEmptyMessage(DOWNLOAD_STOP);
                                    System.out.println("===DOWNLOAD_STOP");
                                    break;
                                }
                                output.write(bytes, 0, c);
                                localSize += c;
                                long nowProcess = localSize / per;

                                if (nowProcess > progress) {
                                    progress = (int) nowProcess;
                                    if (progress % 1 == 0) {
                                        Message msg=new Message();
                                        msg.what=0x789;
                                        Bundle bundle=new Bundle();
                                        bundle.putInt("progress",progress);
                                        msg.setData(bundle);
                                        if (progress==100)
                                        imageHandle.sendMessage(msg);
                                        Log.i("xxx", "下载进度：" + progress);

                                    }
//                                    mHandler.sendEmptyMessage(DOWNLOAD_UPDATE);
                                    System.out.println("===DOWNLOAD_UPDATE");
                                }
                            }
                            input.close();
                            output.close();
                            if (c <= 0) {// 下载完成
//                                mHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                                System.out.println("===DOWNLOAD_SUCCESS");
                            } else {
//                                mHandler.sendEmptyMessage(DOWNLOAD_STOP);
                                System.out.println("===DOWNLOAD_STOP");
                            }
                        }
                    }

                } catch (Exception e) {
                    try {
                        ftpClient.disconnect();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

