package com.yytech.ochatclient.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yytech.ochatclient.R;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.AddFriendRequestDTO;
import com.yytech.ochatclient.dto.data.LoginResultDTO;
import com.yytech.ochatclient.dto.data.UserInfo;
import com.yytech.ochatclient.oaview.XCRoundImageView;
import com.yytech.ochatclient.tcpconnection.TCPClient;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/11/26.
 */

public class AddPeopleAdapter extends BaseAdapter implements Const.Status{
    private String tag = "==AddPeopleAdapter";
    private LayoutInflater mInflater = null;
    private List<Map<String,Object>> data;
    private Context context;
    private MessageDTO<LoginResultDTO> loginMsg;
    private UserInfo userInfo;
    public String IP= Const.IP;
    public int HTTP_PORT=Const.HTTP_PORT;
    Bundle bundle;

    static class ViewHolder{
        public XCRoundImageView headview;
        public TextView title;
        public TextView info;
        public Button agree;
    }


    public AddPeopleAdapter(Context context, List<Map<String, Object>> data, Bundle bundle)
    {
        this.context = context;
        //根据context上下文加载布局，这里的是MainActivity本身，即this
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.bundle = bundle;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.message_item_list,null);
            holder.headview = (XCRoundImageView) convertView.findViewById(R.id.headview);
            holder.title = (TextView) convertView.findViewById(R.id.tv);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            holder.agree = (Button) convertView.findViewById(R.id.message_agree_button);

            //给同意按钮设置带点击事件
            holder.agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginMsg = (MessageDTO<LoginResultDTO>) bundle.getSerializable("loginMsg");
                    userInfo = (UserInfo) bundle.getSerializable("userInfo");
                    //开始发送添加好友信号
                    System.out.println("=====发送添加好友信息");
                    //发送请求
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MessageDTO<AddFriendRequestDTO> addRequestMsg = new MessageDTO<AddFriendRequestDTO>();
                                AddFriendRequestDTO addFriendRequestDTO = new AddFriendRequestDTO();
                                addFriendRequestDTO.setAccepted(false);
                                addFriendRequestDTO.setFromGender(userInfo.getGender());
                                addFriendRequestDTO.setFromNickName(userInfo.getNickName());
                                addFriendRequestDTO.setFromUserId(loginMsg.getUserId());
                                addFriendRequestDTO.setFromUsername(userInfo.getUsername());
                                addFriendRequestDTO.setToUserId((long)data.get(position).get("toUserId"));
                                System.out.println("====toUserId : " +(long)data.get(position).get("toUserId") );
                                addRequestMsg.setData(addFriendRequestDTO);
                                addRequestMsg.setDataName("addFriendRequestDTO");
                                addRequestMsg.setSign(Const.Sign.REQUEST);
                                addRequestMsg.setToken(loginMsg.getToken());
                                addRequestMsg.setUserId(loginMsg.getUserId());

                                System.out.println("====addRequestMsg" + addRequestMsg);
                                if (TCPClient.getInstance().getChannel()==null) {
                                    TCPClient.getInstance().connect();
                                }

                                TCPClient.getInstance().sendMessage(addRequestMsg);
                                System.out.println("=====发送好友请求成功");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.headview.setImageResource((Integer) data.get(position).get("imgid"));
        holder.title.setText((String) data.get(position).get("title"));
        holder.info.setText((String)data.get(position).get("info"));
        if((data.get(position).get("alreadyFriend")) == null || !(boolean)(data.get(position).get("alreadyFriend"))){
            Log.i(tag,"==1==(data.get(position).get(\"alreadyFriend\"))"+(data.get(position).get("alreadyFriend")));
            holder.agree.setText("添加");
            holder.agree.setBackgroundResource(R.color.button_bg);
            holder.agree.setFocusable(true);
            holder.agree.setClickable(true);
        }else{
            Log.i(tag,"==2==(data.get(position).get(\"alreadyFriend\"))"+(data.get(position).get("alreadyFriend")));
            holder.agree.setText("已添加");
            holder.agree.setBackgroundColor(Color.GRAY);
            holder.agree.setFocusable(false);
            holder.agree.setClickable(false);
        }
        return convertView;
    }
}
