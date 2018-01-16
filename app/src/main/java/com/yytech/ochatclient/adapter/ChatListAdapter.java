package com.yytech.ochatclient.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yytech.ochatclient.R;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.data.ChatLogListDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Trt on 2017/12/13.
 */

public class ChatListAdapter extends BaseAdapter {
    private List<ChatLogListDTO> chatList;
    private Context context;
    private int[] head=new int[]{R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8};
    public ChatListAdapter(List<ChatLogListDTO> chatList,Context context){
        super();
        this.chatList=chatList;
        this.context=context;
    }
    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (chatList==null)
            return null;

        View view=View.inflate(context, R.layout.simple_item,null);
        ImageView header= (ImageView) view.findViewById(R.id.header);
        header.setImageResource(head[Integer.parseInt(chatList.get(position).getFriendIcon())-1]);
        TextView name= (TextView) view.findViewById(R.id.name);
        TextView lastMsg= (TextView) view.findViewById(R.id.lastMsg);
        TextView lastChatLogTime= (TextView) view.findViewById(R.id.lastChatLogTime);
        TextView messageNum= (TextView) view.findViewById(R.id.message_num);
        name.setText(chatList.get(position).getFriendNickName());
        if(chatList.get(position).getChatLogs().size()==0)
            lastMsg.setText("");
        else {
            String content=chatList.get(position).getChatLogs().get(chatList.get(position).getChatLogs().size() - 1).getContent();
            if (content==null)
                content=" ";
            lastMsg.setText(content);
            if (chatList.get(position).getChatLogs().get(chatList.get(position).getChatLogs().size() - 1).getContentType()== Const.ChatLogContentType.FILE){
                lastMsg.setText("[文件]");
                if (content.contains(".")) {
                    if (content.contains(".png")||content.contains(".jpg")||content.contains(".bmp")||content.contains(".tiff")
                            ||content.contains(".gif")||content.contains(".pcd")||content.contains(".JPG")||content.contains(".BMP")||content.contains(".TIFF")
                            ||content.contains(".GIF")||content.contains(".PCD")||content.contains(".PNG"))
                        lastMsg.setText("[图片]");
                }
            }
        }
        SimpleDateFormat lsdFormat = new SimpleDateFormat("MM-dd HH:mm");
        Date date=new Date(chatList.get(position).getLastChatLogTime());
        String lStrDate = lsdFormat.format(date);
        lastChatLogTime.setText(lStrDate);
        if (chatList.get(position).getUnReadChatLogCount()==0)
            messageNum.setVisibility(View.GONE);
        else {
            messageNum.setText(""+chatList.get(position).getUnReadChatLogCount());
        }
        return view;
    }
}
