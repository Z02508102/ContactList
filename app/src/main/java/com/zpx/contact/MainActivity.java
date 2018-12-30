package com.zpx.contact;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
    //头像资源组
    private int[] headIds = {R.drawable.img01, R.drawable.img02, R.drawable.img03,
            R.drawable.img04, R.drawable.img05, R.drawable.img06, R.drawable.img07,
            R.drawable.img08, R.drawable.img09, R.drawable.img10, R.drawable.img11, R.drawable.img12,
            R.drawable.img13, R.drawable.img14, R.drawable.img15, R.drawable.img16,
            R.drawable.img17, R.drawable.img18};

    //自定义适配器对象
    private MyAdapter adapter;

   //Data Access Object:对contact表进行CRUD操作的类
    private  ContactDao dao;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.lv_list);
        //初始化组件
        initView();
        dao = new ContactDao(this);
        //动态权限申请
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }else{
            readContacts();
        }
        //给ListView添加item点击事件监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //拨打电话
               /* Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + datas.get(position).getNumber());
                intent.setData(data);
                startActivity(intent);*/

                Intent intent = new Intent();
                intent.setClassName("com.zpx.contact",
                        "com.android.contacts.activities.PeopleActivity");
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
        btnQuery = (Button) findViewById(R.id.btn_query);

        listView = (ListView) findViewById(R.id.lv_list);
        //为按钮注册监听
        btnInsert.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
    }

    //查询所有联系人信息
    private void readContacts(){
        Cursor cursor = null;
        try {
            cursor = dao.selectAll();
            /*if(cursor.getCount() == 0){
                tvList.setText("");
            }else{
                *//*cursor.moveToFirst();
                tvList.setText(cursor.getString(1) + ":" + cursor.getString(2) );*//*
            }*/
            /*while (cursor.moveToNext()){
                tvList.append("\n" + cursor.getString(1) + ":" + cursor.getString(2));
            }*/
            if(cursor != null && cursor.getCount() > 0){
                Contact contact = null;
                int i = 0;
                while(cursor.moveToNext()){
                    //创建一个联系人对象
                    contact = new Contact();
                    //获取联系人姓名
                    String name = cursor.getString(1);
                    //获取联系人手机号码
                    String number = cursor.getString(2);
                    //获取联系人的备注
                    //String remark = cursor.getString(2);
                    //设置联系人属性
                    contact.setHeadImgId(headIds[i++]);
                    contact.setName(name);
                    contact.setNumber(number);
                   // contact.setRemark(remark);
                    //将联系人添加到集合中
                    datas.add(contact);
                }
                //给ListView设置适配器
                adapter = new MyAdapter();
                listView.setAdapter(adapter);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
    }

    /*@Override
    public void onClick(View v) {

    }*/

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
            case R.id.btn_query:
                readContacts();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permisssions,
                                           @NonNull int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }else{
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    class MyAdapter extends BaseAdapter {
        //得到item的总数
        @Override
        public int getCount(){
            return datas.size();
        }

        //得到每个position位置上的item代表的对象
        @Override
        public Object getItem(int position){
            return datas.get(position);
        }

        //得到item的id
        @Override
        public long getItemId(int position){
            return position;
        }

        //得到item的view视图
        @Override
        public View  getView(int position, View convertView, ViewGroup parent){
            ViewHolder holder;
            if(convertView == null){
                //利用布局填充器填充自定义的ListView item的布局
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.contacts_list,null);
                holder = new ViewHolder();
                //利用得到的view来进行findViewById()
                holder.ivHead =(ImageView)convertView.findViewById(R.id.iv_head);
                holder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
                holder.tvNumber = (TextView)convertView.findViewById(R.id.tv_number);

                //将holder存入view的tag中
                convertView.setTag(holder);
            }else{
                //在convertview不为空的情况下，重复使用convertView
                //并去除存储的tag
                holder = (ViewHolder)convertView.getTag();
            }

            //设置holder中每个控件的内容
            holder.ivHead.setImageResource(datas.get(position).getHeadImgId());
            holder.tvName.setText(datas.get(position).getName());
            holder.tvNumber.setText(datas.get(position).getNumber());
            //最后不要忘了返回convertView
            return convertView;
        }

        //为了减少findViewById的次数
        class ViewHolder{
            ImageView ivHead;
            TextView tvName;
            TextView tvNumber;
        }
    }
}
