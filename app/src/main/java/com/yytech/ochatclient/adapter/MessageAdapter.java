package com.yytech.ochatclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yytech.ochatclient.R;
import com.yytech.ochatclient.XCRoundImageView;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/11/24.
 */

public class MessageAdapter extends BaseAdapter{

    private LayoutInflater mInflater = null;
    List<Map<String,Object>> data;
    String buttonString;
    Context context;
    static class ViewHolder{
        public XCRoundImageView headview;
        public TextView title;
        public TextView info;
        public Button agree;
    }


    public MessageAdapter(Context context, List<Map<String, Object>> data , String buttonString)
    {
        this.context = context;
        //根据context上下文加载布局，这里的是MainActivity本身，即this
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.buttonString = buttonString;
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
                    Toast.makeText(context,"第"+position+"个添加成功！",Toast.LENGTH_SHORT).show();
                }
            });

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.headview.setImageResource((Integer) data.get(position).get("imgid"));
        holder.title.setText((String) data.get(position).get("title"));
        holder.info.setText((String)data.get(position).get("info"));
        holder.agree.setText(buttonString);
        return convertView;
    }
}
