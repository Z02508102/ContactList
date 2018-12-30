package com.zpx.contact;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PeopleActivity extends AppCompatActivity implements View.OnClickListener{

   /* private EditText etName;
    private EditText etPhoneNumber;
    private Button btnInsert;
    private Button btnDelete;
    private Button btnUpdate;
    private Button btnQuery;
    private TextView tvView;*/

    //Data Access Object:对contact表进行CRUD操作的类
    private  ContactDao dao;
    //private Contact contact;

    private LinearLayout linearLayout;
    private TextView tvName;
    private TextView tvNumber;
    private String name;
    private String phoneNumber;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_info);
        linearLayout = findViewById(R.id.call);

        tvName = findViewById(R.id.tv_name);
        tvNumber = findViewById(R.id.tv_number);

        //动态权限申请
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }else{
            readContacts();
        }

        //给ListView添加item点击事件监听器
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拨打电话
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phoneNumber);
                intent.setData(data);
                startActivity(intent);
            }
        });
    }

    private void readContacts (){

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phoneNumber = intent.getStringExtra("phoneNumber");
        //Toast.makeText(PeopleActivity.this,position,Toast.LENGTH_SHORT).show();
        //Cursor cursor = null;
        try {
            //cursor = dao.selectAll(id);
            //Contact contact = null;
            if (name != null && !name.equals("") && phoneNumber != null && !phoneNumber.equals("")){
                //创建一个联系人对象
                /*contact = new Contact();
                //获取联系人姓名
                String name = cursor.getString(1);
                //获取联系人手机号码
                String number = cursor.getString(2);
                //获取联系人的备注
                //String remark = cursor.getString(2);
                //设置联系人属性
                contact.setName(name);
                contact.setNumber(number);
                // contact.setRemark(remark);
                //将联系人添加到集合中
                //datas.add(contact);
*/
                tvName.setText(name);
                tvNumber.setText(phoneNumber);
            }else {
                tvName.setText("");
                tvNumber.setText("");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


   /*private void initView(){
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
    }*/

    public void onClick(View v){
        /*String name = null;
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
        }*/
    }


    /*class MyAdapter extends BaseAdapter {
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
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder holder;
            if(convertView == null){
                //利用布局填充器填充自定义的ListView item的布局
                convertView = LayoutInflater.from(PeopleActivity.this).inflate(R.layout.detailed_info,null);
                holder = new ViewHolder();
                //利用得到的view来进行findViewById()
                //holder.ivHead =(ImageView)convertView.findViewById(R.id.iv_head);
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
            //holder.ivHead.setImageResource(datas.get(position).getHeadImgId());
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
    }*/

}