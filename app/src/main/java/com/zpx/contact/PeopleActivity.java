package com.zpx.contact;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class PeopleActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName;
    private EditText etPhoneNumber;
    private Button btnInsert;
    private Button btnDelete;
    private Button btnUpdate;
    private Button btnQuery;
    private TextView tvList;

    private ListView listView;
    //数据源
    private List<Contact> datas = new ArrayList<Contact>();
    //自定义适配器对象
    private MainActivity.MyAdapter adapter;
    //Data Access Object:对contact表进行CRUD操作的类
    private  ContactDao dao;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_info);
        //初始化组件
        initView();

        //给ListView添加item点击事件监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //拨打电话
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + datas.get(position).getNumber());
                intent.setData(data);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        etName =(EditText)findViewById(R.id.et_name);
        etPhoneNumber =(EditText)findViewById(R.id.et_phone_number);

        btnInsert = (Button) findViewById(R.id.btn_insert);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        //btnQuery = (Button) findViewById(R.id.btn_query);

        listView = (ListView) findViewById(R.id.lv_list);
        //为按钮注册监听
        btnInsert.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        //btnQuery.setOnClickListener(this);
    }

    public void onClick(View v){
        String name = null;
        String phoneNum = null;

        switch (v.getId()) {
            case R.id.btn_insert:
                name = etName.getText().toString().trim();
                phoneNum = etPhoneNumber.getText().toString().trim();
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phoneNum)){
                    ContentValues values = new ContentValues();
                    values.put("name",name);
                    values.put("phonenumber",phoneNum);
                    if(dao.insert(values) > 0){
                        Toast.makeText(this,"添加成功", Toast.LENGTH_SHORT).show();
                        //添加成功后将输入框设为空值
                        etName.setText("");
                        etPhoneNumber.setText("");
                    }
                }
                break;
            case R.id.btn_delete:
                name = etName.getText().toString().trim();
                //若不为空则根据姓名删除数据
                if(!TextUtils.isEmpty(name)){
                    if(dao.deleteByName(name) > 0){
                        Toast.makeText(this,"删除成功", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(dao.deleteAll() > 0){
                        Toast.makeText(this, "全部删除成功", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btn_update:
                name = etName.getText().toString().trim();
                phoneNum = etPhoneNumber.getText().toString().trim();
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phoneNum)){
                    ContentValues values = new ContentValues();
                    values.put("phonenumber",phoneNum);
                    if(dao.update(values,name) > 0){
                        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                        etName.setText("");
                        etPhoneNumber.setText("");
                    }
                }
                break;
            /*case R.id.btn_query:
                readContacts();
                break;*/
        }
    }

}