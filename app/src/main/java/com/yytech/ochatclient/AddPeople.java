package com.yytech.ochatclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.yytech.ochatclient.adapter.AddPeopleAdapter;
import com.yytech.ochatclient.adapter.MessageAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPeople extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    ListView listView;
    List<Map<String,Object>> data;
    EditText searchedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_people);

        watchSearch();


    }

    //给软键盘的搜索按钮添加搜索功能
    public void watchSearch() {
        searchedit = (EditText) findViewById(R.id.add_people_searchEdit);
        searchedit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) searchedit.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(AddPeople.this.getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);


                    // ***************搜索，进行自己要的操作...
                    getSearchResult();



                    return true;
                }
                return false;
            }
        });
    }

    //得到搜索调查结果在此类中方法
    public void getSearchResult() {
        //得到索搜结果
        listView = (ListView) findViewById(R.id.add_people_listview);

        //*************得到数据
        data = getData();

        MessageAdapter messageAdapter = new MessageAdapter(AddPeople.this,data,"添加");
        listView.setAdapter(messageAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //我这里实现的是跳转功能**********
                Intent intent = new Intent(AddPeople.this, PersonInfo.class);
                startActivity(intent);

            }
        });
    }

    //得到搜索调查结果xml中的调用方法
    public void getSearchResult(View source) {
        //得到索搜结果
        listView = (ListView) findViewById(R.id.add_people_listview);

        //得到到数据
        data = getData();
        AddPeopleAdapter addPeopleAdapter = new AddPeopleAdapter(AddPeople.this,data);
        listView.setAdapter(addPeopleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AddPeople.this, PersonInfo.class);
                startActivity(intent);
            }
        });

    }

    //返回事件
    public void backToMessage(View source){
        finish();
    }


    //得到数据
    private List<Map<String, Object>> getData()
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for(int i=0;i<20;i++)
        {
            map = new HashMap<String, Object>();
            map.put("imgid", R.drawable.mine_avatar);
            map.put("title", "小红");
            map.put("info", "加个好友呗");
            list.add(map);
        }
        return list;
    }
}
