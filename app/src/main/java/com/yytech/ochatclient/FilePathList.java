package com.yytech.ochatclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Trt on 2017/12/10.
 */

public class FilePathList extends Activity{
    ListView listView;
    TextView textView;
    File currentParent;
    File[] currentFiles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filepathlist);
        listView= (ListView) findViewById(R.id.list);
        textView= (TextView) findViewById(R.id.path);
        File root=new File("mnt/sdcard/");
        if (root.exists()){
            currentParent=root;
            currentFiles=root.listFiles();
            inflateListView(currentFiles);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentFiles[position].isFile()) {
                    try {
                        Intent intent=new Intent(FilePathList.this,UpLoadActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("path",currentParent.getCanonicalPath());
                        bundle.putString("name",currentFiles[position].getName());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                File[] tmp=currentFiles[position].listFiles();
                if (tmp==null||tmp.length==0){
                    Toast.makeText(FilePathList.this,"当前路径不可访问",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    currentParent=currentFiles[position];
                    currentFiles=tmp;
                    inflateListView(currentFiles);
                }
            }
        });
        Button parent= (Button) findViewById(R.id.parent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (currentParent.getParentFile().getCanonicalPath().equals("/mnt"))
                        finish();
                    else {
                        if (!currentParent.getCanonicalPath().equals("/mnt/sdcard")) {
                            System.out.println("===" + currentParent.getCanonicalPath());
                            currentParent = currentParent.getParentFile();
                            currentFiles = currentParent.listFiles();
                            inflateListView(currentFiles);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void inflateListView(File[] files){
        List<Map<String,Object>> listItems=new ArrayList<Map<String,Object>>();
        for (int i=0;i<files.length;i++){
            Map<String,Object>listItem=new HashMap<String,Object>();
        if (files[i].isDirectory())
            listItem.put("icon",R.drawable.folder);
         else
            listItem.put("icon",R.drawable.file);
            listItem.put("fileName",files[i].getName());
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,listItems,R.layout.line,new String[]{"icon","fileName"},new int[]{R.id.icon,R.id.fileName});
        listView.setAdapter(simpleAdapter);
        try {
            textView.setText("当前路径为："+currentParent.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
