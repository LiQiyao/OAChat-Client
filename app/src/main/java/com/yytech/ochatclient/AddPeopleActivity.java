package com.yytech.ochatclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.google.gson.reflect.TypeToken;
import com.yytech.ochatclient.adapter.AddPeopleAdapter;
import com.yytech.ochatclient.common.Const;
import com.yytech.ochatclient.dto.MessageDTO;
import com.yytech.ochatclient.dto.data.FoundUsersDTO;
import com.yytech.ochatclient.dto.data.LoginResultDTO;
import com.yytech.ochatclient.dto.data.OnlineDTO;
import com.yytech.ochatclient.dto.data.UserDetailDTO;
import com.yytech.ochatclient.util.GsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPeopleActivity extends AppCompatActivity implements Const.Status{
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private LinearLayout addPeopleLayout;
    private ImageView imageView;
    private TextView textView;
    private ListView listView;
    private List<Map<String,Object>> data;
    private EditText searchedit;
    private HttpURLConnection conn;
    private MessageDTO<LoginResultDTO> loginMsg;
    public String IP=Const.IP;
    public int HTTP_PORT=Const.HTTP_PORT;
    public static Handler handler;
    private int[] heads=new int[]{R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        setContentView(R.layout.activity_add_people);
        addPeopleLayout = (LinearLayout) findViewById(R.id.add_people_layout);
        watchSearch(); //给软键盘搜索按钮添加事件
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x123){
                    //得到索搜结果
                    listView = (ListView) findViewById(R.id.add_people_listview);
                    AddPeopleAdapter addPeopleAdapter = new AddPeopleAdapter(AddPeopleActivity.this,data,msg.getData());
                    listView.setAdapter(addPeopleAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //跳转到个人信息页面
                            Intent intent = new Intent(AddPeopleActivity.this, PersonInfoActivity.class);
                            UserDetailDTO userDetailDTO = (UserDetailDTO) data.get(position).get("userDetailDTO");
                            Bundle detailBundle = new Bundle();
                            detailBundle.putSerializable("userDetailDTO",userDetailDTO);
                            intent.putExtras(detailBundle);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                if(msg.what == 0x234){
                    Toast.makeText(AddPeopleActivity.this,"您搜索的好友为空！",Toast.LENGTH_SHORT).show();
                }
                if (msg.what == 0x666){
                    Toast.makeText(AddPeopleActivity.this,"添加好友成功！",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        };

        addPeopleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


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
                            .hideSoftInputFromWindow(AddPeopleActivity.this.getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);


                    // ***************搜索，进行自己要的操作...
                    getSearchResult();

                    return true;
                }
                return false;
            }
        });
    }

    //得到搜索调查结果方法
    public void getSearchResult() {

        //发送请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bundle = getIntent().getExtras();
                    loginMsg = (MessageDTO<LoginResultDTO>) bundle.getSerializable("loginMsg");
                    String key = searchedit.getText().toString();
                    bundle.putString("key",key);
                    //1：url对象
                    URL url = new URL("http://"+IP+":"+HTTP_PORT+"/api/users/find?"
                            +"key=" +key+"&token="+loginMsg.getToken()+ "&userId=" +loginMsg.getUserId());

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
                        Type objectType = new TypeToken<MessageDTO<FoundUsersDTO>>(){}.getType();
                        MessageDTO<FoundUsersDTO> messageDTO = GsonUtil.getInstance().fromJson(result, objectType);

                        System.out.println("=====================服务器返回的信息：：" + result);
                        if (messageDTO.getStatus() == SUCCESS){
                            System.out.println("===找到好友！");
                            FoundUsersDTO foundUsersDTO = messageDTO.getData();
                            System.out.println("===foundUsersDto count :"+foundUsersDTO.getCount());
                            //如果得到的好友数量为0，则弹出消息框；
                            if (foundUsersDTO.getCount() == 0){
                                handler.sendEmptyMessage(0x234);
                            }
                            //重新加载为空的数据，相当于刷新
                            //加载数据到布局
                            List<UserDetailDTO> userDetailDTOList = foundUsersDTO.getUsers();
                            data = getData(userDetailDTOList);//*************得到数据
                            Message msg = new Message();
                            msg.what = 0x123;
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        }else {
                            System.out.println("===没有找到好友！");
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
                    conn.disconnect();
                }
            }
        }).start();
    }



    //返回事件
    public void onBackPressed() {
        Intent intent=new Intent(AddPeopleActivity.this,MainActivity.class);
        Bundle bundle=new Bundle();
        preferences = getSharedPreferences("userIdAndToken",MODE_PRIVATE);
        editor = preferences.edit();
        String userId = preferences.getString("userId",null);
        String token = preferences.getString("token",null);
        MessageDTO<OnlineDTO> onlineMsg=new MessageDTO<OnlineDTO>();
        onlineMsg.setToken(token);
        onlineMsg.setUserId(Long.valueOf(userId));
        bundle.putSerializable("onlineMsg",onlineMsg);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


    //填充数据
    private List<Map<String, Object>> getData(List<UserDetailDTO> userDetailDTOList)
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for(UserDetailDTO userDetailDTO : userDetailDTOList)
        {
            map = new HashMap<String, Object>();
            map.put("imgid",heads[Integer.parseInt(userDetailDTO.getIcon())-1] );
            map.put("title", userDetailDTO.getNickName());
            map.put("info", userDetailDTO.getTelephoneNumber());
            map.put("toUserId",userDetailDTO.getId());
            map.put("alreadyFriend",userDetailDTO.getAlreadyFriend());
            map.put("userDetailDTO",userDetailDTO);
            list.add(map);
        }
        return list;
    }
}
