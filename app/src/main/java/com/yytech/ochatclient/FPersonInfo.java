package com.yytech.ochatclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.LoginResultDTO;
import com.yytech.ochatclient.util.GsonUtil;
import com.yytech.ochatclient.util.LocalUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.yytech.ochatclient.R.id.person_info_address;

/**
 * Created by admin on 2017/12/17.
 */

public class FPersonInfo extends Fragment implements Const.Status{
    private MessageDTO<LoginResultDTO> loginMsg;
    private static String IP=Const.IP;
    private static int HTTP_PORT=Const.HTTP_PORT;
    private HttpURLConnection conn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle=getArguments();
        loginMsg= (MessageDTO<LoginResultDTO>) bundle.getSerializable("loginMsg");

        View view = inflater.inflate(R.layout.activity_person_info,container,false);
        TextView nickNameText = (TextView) view.findViewById(R.id.person_info_nickname);
        TextView genderText = (TextView) view.findViewById(R.id.person_info_gender);
        TextView addressText = (TextView) view.findViewById(person_info_address);

        nickNameText.setText(loginMsg.getData().getSelf().getNickName());
        genderText.setText(loginMsg.getData().getSelf().getGender());
        addressText.setText(loginMsg.getData().getSelf().getAddress());

        TextView cancellationView = (TextView) view.findViewById(R.id.person_info_cancellation);
        cancellationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LocalUtil localUtil = new LocalUtil();
                final String userId = localUtil.getUserId(getActivity());
                final String token = localUtil.getToken(getActivity());

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
                    }
                }).start();
            }
        });




        return view;
    }
}
