package com.yytech.ochatclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.LoginResultDTO;
import com.yytech.ochatclient.dto.data.UserInfo;
import com.yytech.ochatclient.tcpconnection.TCPClient;
import com.yytech.ochatclient.util.GsonUtil;
import com.yytech.ochatclient.util.LocalUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by admin on 2017/12/17.
 */

public class FEditPersonInfo extends Fragment implements Const.Status{
    private MessageDTO<LoginResultDTO> loginMsg;
    private static String IP= Const.IP;
    private static int HTTP_PORT=Const.HTTP_PORT;
    private HttpURLConnection conn;
    private EditText nicknameEdit;
    private EditText genderEdit;
    private EditText telEdit;
    private ImageView ensureImage;
    private UserInfo userInfo;
    private String updateNickname;
    private String updateGender;
    private String updateTel;
    private Handler handler;
    private int[] heads=new int[]{R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0x123){
                    Toast.makeText(getContext(),"修改个人信息成功！",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle=getArguments();
        loginMsg= (MessageDTO<LoginResultDTO>) bundle.getSerializable("loginMsg");
        userInfo = loginMsg.getData().getSelf();

        View view = inflater.inflate(R.layout.f_edit_person_info,container,false);

        nicknameEdit = (EditText) view.findViewById(R.id.edit_personinfo_nickname);
        genderEdit = (EditText) view.findViewById(R.id.edit_personinfo_gender);
        telEdit = (EditText) view.findViewById(R.id.edit_personinfo_tel);
        ImageView head= (ImageView) view.findViewById(R.id.head);
        head.setImageResource(heads[Integer.parseInt(userInfo.getIcon())-1]);
        nicknameEdit.setText(userInfo.getNickName());
        genderEdit.setText(userInfo.getGender());
        telEdit.setText(userInfo.getTelephoneNumber());

        //修改个人信息请求
        ensureImage = (ImageView) view.findViewById(R.id.edit_personinfo_ensure_image);
        ensureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //得到编辑框中的内容
                updateNickname = nicknameEdit.getText().toString();
                updateGender = genderEdit.getText().toString();
                updateTel = telEdit.getText().toString();

                //post方式修改个人信息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //urlConnection请求服务器，验证
                        try {
                            //1：url对象
                            URL url = new URL("http://"+IP+":"+HTTP_PORT+"/api/users/update");
                            //2;url.openconnection
                            conn = (HttpURLConnection) url.openConnection();

                            //3设置请求参数
                            conn.setRequestMethod("POST");
                            conn.setConnectTimeout(10 * 1000);
                            //请求头的信息
                            String body = "nickName=" + URLEncoder.encode(updateNickname) +
                                    "&gender=" + URLEncoder.encode(updateGender) +
                                    "&telephoneNumber=" + URLEncoder.encode(updateTel) +
                                    "&token=" + URLEncoder.encode(loginMsg.getToken()) +
                                    "&id=" + URLEncoder.encode(String.valueOf(loginMsg.getUserId())) ;
                            conn.setRequestProperty("Content-Length", String.valueOf(body.length()));
                            conn.setRequestProperty("Cache-Control", "max-age=0");
                            conn.setRequestProperty("Origin", "http://"+IP);

                            //设置conn可以写请求的内容
                            conn.setDoOutput(true);
                            conn.setDoInput(true);//默认设置为true

                            OutputStream os = conn.getOutputStream();
                            os.write(body.getBytes());
                            os.flush();

                            //4响应码
                            int code = conn.getResponseCode();
                            if (code == 200) {
                                InputStream inputStream = conn.getInputStream();
                                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                                String line;
                                String result = "";

                                while((line = br.readLine()) != null){
                                    result += line;
                                }

                                br.close();
                                MessageDTO messageDTO = GsonUtil.getInstance().fromJson(result, MessageDTO.class);
                                System.out.println("=====================服务器返回的信息：：" + result);
                                System.out.println("==========" + messageDTO.getStatus());
                                if(messageDTO.getStatus() == SUCCESS){
                                    Message msg = new Message();
                                    msg.what = 0x123;
                                    handler.sendMessage(msg);
                                }
//
                            }else {
                                System.out.println("链接失败！"+code);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


        //注销请求
        TextView cancellationView = (TextView)view.findViewById(R.id.person_info_cancellation);
        cancellationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LocalUtil localUtil = new LocalUtil();

                System.out.println("===cancellationView on click!");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //1：url对象
                            URL url = new URL("http://"+IP+":"+HTTP_PORT+"/api/users/logout?"+"userId="+loginMsg.getUserId()+ "&token=" +loginMsg.getToken());

                            conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            System.out.println("=====HTTP");
                            conn.setConnectTimeout(10 * 1000);
                            //请求头的信息
                            conn.setRequestProperty("accept","*/*");
                            conn.setRequestProperty("connection", "Keep-Alive");
                            conn.setRequestProperty("Origin", "http://"+IP);
                            conn.connect();

                            int code = conn.getResponseCode();
                            if(code == 200){
                                InputStream inputStream = conn.getInputStream();
                                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                                String line;
                                String result = "";
                                while((line = br.readLine()) != null){
                                    result += line;
                                }
                                br.close();
                                MessageDTO messageDTO = GsonUtil.getInstance().fromJson(result, MessageDTO.class);
                                System.out.println("=====================服务器返回的信息：：" + result);
                                System.out.println("==========" + messageDTO.getStatus());
                                if (messageDTO.getStatus() == SUCCESS){
                                    localUtil.removeUserIdAndToken(getActivity());//清除userId和token
                                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    getActivity().startActivity(intent);
                                    getActivity().finish();
                                }else {
                                    Toast.makeText(getContext(),"注销失败！",Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                System.out.println("===发送请求失败： " + code);
                            }

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {
                            TCPClient.getInstance().disConnect();
                            conn.disconnect();
                        }
                    }
                }).start();
            }
        });


        return view;
    }


}
