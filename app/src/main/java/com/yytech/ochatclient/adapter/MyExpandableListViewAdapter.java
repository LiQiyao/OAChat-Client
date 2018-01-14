package com.yytech.ochatclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yytech.ochatclient.R;
import com.yytech.ochatclient.dto.data.UserDetailDTO;

import java.util.List;

/**
 * Created by Trt on 2017/11/20.
 */

public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LayoutInflater inflater;
    private String[] group = new String[]{"我的好友", "同学", "同事"};
    private String[] names;
    private String[][] child;
    private int[] head=new int[]{R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8};
    private int[][] heads;
    private int[] friendHead;
    private List<UserDetailDTO> friendList;
    public MyExpandableListViewAdapter(List<UserDetailDTO> friendList,Context context){
        if (friendList==null||friendList.size()==0)
            names=new String[0];
        names=new String[friendList.size()];
        friendHead=new int[friendList.size()];
        this.friendList=friendList;
        this.context=context;
        inflater=LayoutInflater.from(context);
        for (int i=0;i<friendList.size();i++){//contantList null
            names[i]=friendList.get(i).getNickName();
            friendHead[i]=head[Integer.parseInt(friendList.get(i).getIcon())-1];
            child= new String[][]{names,
                    {"ez","走四方","比巴卜", "coco", "努努", "皮卡喵" },
                    { "jack", "乒乒乓乓", "天上天下" }};
            heads=new int[][]{friendHead,
                    {R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img2,R.drawable.img3},
                    {R.drawable.img1,R.drawable.img6,R.drawable.img8}};
        }
    }
    @Override
    public int getGroupCount() {
        return group.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return  child[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return group[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view=inflater.inflate(R.layout.item_elv_group,null);
        ImageView iv_group_icon = (ImageView) view.findViewById(R.id.iv_group_icon);
        TextView tv_group_name = (TextView) view.findViewById(R.id.tv_group_name);
        TextView tv_group_number = (TextView) view.findViewById(R.id.tv_group_number);

        tv_group_name.setText(group[groupPosition]);
        tv_group_number.setText(child[groupPosition].length+"/"+child[groupPosition].length);

        /*isExpanded 子列表是否展开*/
        if(isExpanded){
            iv_group_icon.setImageResource(R.drawable.down_point);
        }else {
            iv_group_icon.setImageResource(R.drawable.right_point);
        }

        return view;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_elv_child,null);

        ImageView iv_child_icon = (ImageView) view.findViewById(R.id.header);
        iv_child_icon.setImageResource(heads[groupPosition][childPosition]);
        TextView tv_child_info = (TextView) view.findViewById(R.id.tv_child_info);
        TextView tv_child_name = (TextView) view.findViewById(R.id.tv_child_name);
        TextView tv_child_network = (TextView) view.findViewById(R.id.tv_child_network);

        tv_child_name.setText(child[groupPosition][childPosition]);
        tv_child_network.setText(childPosition % 2 == 0?"5G":"6G");
        return view;
    }

    /*子选项是否可选 */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

