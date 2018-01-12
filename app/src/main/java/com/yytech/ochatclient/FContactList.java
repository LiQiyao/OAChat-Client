package com.yytech.ochatclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.LoginResultDTO;
import com.yytech.ochatclient.dto.data.UserDetailDTO;
import com.yytech.ochatclient.dto.data.UserInfo;

import java.util.List;


/**
 * Created by Trt on 2017/11/26.
 */

public class FContactList extends android.support.v4.app.Fragment {
    private MessageDTO<LoginResultDTO> loginMsg;
    private List<UserDetailDTO> friendList;
    private UserInfo userInfo;
    private TextView contactAddText;
    private LinearLayout newpeopleLinear;

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        friendList=((MainActivity)context).getFriendList();
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 1.1 创建一个adapter实例*/
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final Bundle bundle=getArguments();
        loginMsg= (MessageDTO<LoginResultDTO>) bundle.getSerializable("loginMsg");
        friendList=loginMsg.getData().getFriendList();
        userInfo= (UserInfo) bundle.getSerializable("userInfo");
        View view=inflater.inflate(R.layout.f_contact_list,container,false);
        //添加按钮事件
        contactAddText = (TextView) view.findViewById(R.id.contact_add);
        contactAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AddPeople.class);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });

        //新朋友点击事件
        newpeopleLinear = (LinearLayout) view.findViewById(R.id.f_contact_new_people_linear);
        newpeopleLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),NewPeople.class);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        if(friendList.size()>0&& friendList!=null) {
            System.out.println("===1188");
            MyExpandableListViewAdapter adapter;
            adapter = new MyExpandableListViewAdapter(friendList, getActivity());


        /* 1. 设置适配器*/
            ExpandableListView myExpandableListView = (ExpandableListView) view.findViewById(R.id.elv);
            myExpandableListView.setAdapter(adapter);
            System.out.println("===1111111111111111" + friendList.get(0).getUsername());
        }
//        myExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                    Intent intent=new Intent(getActivity(),Chat.class);
//                    Bundle bundle=new Bundle();
//                    startActivity(intent);
//                return true;
//            }
//        });
        return view;
    }
}


