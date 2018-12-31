package com.zpx.contact;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
    private Button btnSave;
    private Button btnInsert;
    private Button btnDelete;
    private Button btnUpdate;
    private Button btnQuery;
    private TextView tvList;

    private ListView listView;
    //数据源
    private List<Contact> datas = new ArrayList<>();
    //头像资源组
    /*private int[] headIds = {R.drawable.img01, R.drawable.img02, R.drawable.img03,
            R.drawable.img04, R.drawable.img05, R.drawable.img06, R.drawable.img07,
            R.drawable.img08, R.drawable.img09, R.drawable.img10, R.drawable.img11, R.drawable.img12,
            R.drawable.img13, R.drawable.img14, R.drawable.img15, R.drawable.img16,
            R.drawable.img17, R.drawable.img18};*/

    //自定义适配器对象
    MyAdapter adapter = new MyAdapter();
    //Data Access Object:对contact表进行CRUD操作的类
    private ContactDao contactDao;
    private Contact contact;
    private Cursor cursor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.lv_list);
        //初始化组件
        initView();
        contactDao = new ContactDao(this);
        //动态权限申请
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }else{
            readContacts();
        }
        //给ListView添加点击事件监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contact contact = (Contact) adapter.getItem(position);
                String name = contact.getName();
                String phoneNumber = contact.getNumber();
                //Toast.makeText(MainActivity.this,item.getNumber(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, PeopleActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
            }
        });
        //给ListView添加长按事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int param = position;
                AlertDialog.Builder edit1 = new AlertDialog.Builder(MainActivity.this);
                edit1.setTitle("编辑联系人");
                edit1.setItems(new String[] {"删除","编辑"}, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        switch (which){
                            case 0:
                                AlertDialog.Builder edit3 = new AlertDialog.Builder(MainActivity.this);
                                edit3.setTitle("删除联系人");
                                edit3.setTitle("确定删除吗");
                                edit3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        View redactView = getLayoutInflater().inflate(R.layout.redact_contact,null);
                                        contactDao.deleteByName(datas.get(param));
                                        readContacts();
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                edit3.setNeutralButton("取消", null);
                                edit3.create().show();
                                break;
                            case 1:
                                AlertDialog.Builder addcontact = new AlertDialog.Builder(MainActivity.this);
                                addcontact.setTitle("编辑联系人");
                                View redactView = getLayoutInflater().inflate(R.layout.redact_contact,null);
                                etName = redactView.findViewById(R.id.tv_name);
                                etPhoneNumber = redactView.findViewById(R.id.tv_number);
                                btnSave = redactView.findViewById(R.id.btn_save);
                                btnSave.setVisibility(View.GONE);
                                etName.setText(datas.get(param).getName());
                                etPhoneNumber.setText(datas.get(param).getNumber());
                                addcontact.setView(redactView);
                                addcontact.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        contact.setName(etName.getText().toString().trim());
                                        contact.setNumber(etPhoneNumber.getText().toString().trim());
                                    }
                                });
                                addcontact.setNegativeButton("取消", null);
                                addcontact.create().show();
                                break;
                            default:
                                break;
                        }
                    }
                });
                edit1.create().show();
                return true;
            }
        });
    }

    private void initView(){
        etName = findViewById(R.id.et_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);

        btnInsert = findViewById(R.id.btn_insert);
        btnDelete = findViewById(R.id.btn_delete);
        btnUpdate = findViewById(R.id.btn_update);
        btnQuery = findViewById(R.id.btn_query);

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
        datas.clear();
        try {
            cursor = contactDao.selectAll();
            if(cursor != null && cursor.getCount() > 0){
                //Contact contact = null;
                //int i = 0;
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
                    //contact.setHeadImgId(headIds[i++]);
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

   /* @Override
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
                    if(contactDao.insert(values) > 0){
                        Toast.makeText(this,"添加成功", Toast.LENGTH_SHORT).show();
                        //添加成功后将输入框设为空值
                        etName.setText("");
                        etPhoneNumber.setText("");
                    }
                }
                break;
            /*case R.id.btn_delete:
                name = etName.getText().toString().trim();
                //若不为空则根据姓名删除数据
                if(!TextUtils.isEmpty(name)){
                    if(contactDao.deleteByName(name) > 0){
                        Toast.makeText(this,"删除成功", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(contactDao.deleteAll() > 0){
                        Toast.makeText(this, "全部删除成功", Toast.LENGTH_SHORT).show();
                    }
                }
                break;*/
            case R.id.btn_update:
                name = etName.getText().toString().trim();
                phoneNum = etPhoneNumber.getText().toString().trim();
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phoneNum)){
                    ContentValues values = new ContentValues();
                    values.put("phonenumber",phoneNum);
                    if(contactDao.update(values,name) > 0){
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
        public View  getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (!datas.isEmpty()) {
                if (convertView == null) {
                    //利用布局填充器填充自定义的ListView item的布局
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.contacts_list, null);
                    holder = new ViewHolder();
                    //利用得到的view来进行findViewById()
                    //holder.ivHead =convertView.findViewById(R.id.iv_head);
                    holder.tvName = convertView.findViewById(R.id.tv_name);
                    holder.tvNumber = convertView.findViewById(R.id.tv_number);
                /*//设置holder中每个控件的内容
                holder.tvName.setText("");
                holder.tvNumber.setText("");*/
                    //将holder存入view的tag中
                    convertView.setTag(holder);
                } else {
                    //在convertview不为空的情况下，重复使用convertView
                    //并去除存储的tag
                    holder = (ViewHolder) convertView.getTag();
                }

                //设置holder中每个控件的内容
                //holder.ivHead.setImageResource(datas.get(position).getHeadImgId());
                holder.tvName.setText(datas.get(position).getName());
                holder.tvNumber.setText(datas.get(position).getNumber());
                //最后不要忘了返回convertView
                return convertView;
            }
            return null;
        }

        //为了减少findViewById的次数
        class ViewHolder{
            ImageView ivHead;
            TextView tvName;
            TextView tvNumber;
        }
    }
}
