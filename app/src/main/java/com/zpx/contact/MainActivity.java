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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvlName;
    private TextView tvlPhoneNum;
    private EditText etuName;
    private EditText etuPhoneNum;
    private Button btnSave;
    private Button btnInsert;
    private Button btnDelete;
    private Button btnUpdate;
    private Button btnQuery;
    private TextView tvList;
    private ListView listView;

    //数据源
    private List<Contact> datas = new ArrayList<>();
    //自定义适配器对象
    MyAdapter adapter = new MyAdapter();
    //Data Access Object:对contact表进行CRUD操作的类
    private ContactDao contactDao;
    private Contact contact;
    private Cursor cursor;
    private String name;
    private String phoneNum;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.lv_list);

        contactDao = new ContactDao(this);
        //动态权限申请
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            readContacts();
        }
        //给ListView添加点击事件监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //联系人详情页面
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = (Contact) adapter.getItem(position);
                name = contact.getName();
                phoneNum = contact.getNumber();
                Intent intent = new Intent(MainActivity.this, PeopleActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("phoneNum", phoneNum);
                startActivity(intent);
            }
        });

        //给ListView添加长按事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final String updateByName = datas.get(position).getName();
                final int param = position;
                AlertDialog.Builder edit1 = new AlertDialog.Builder(MainActivity.this);
                edit1.setTitle(datas.get(position).getName());
                edit1.setItems(new String[]{"编辑", "删除"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                AlertDialog.Builder addcontact = new AlertDialog.Builder(MainActivity.this);
                                addcontact.setTitle("编辑联系人");
                                View redactView = getLayoutInflater().inflate(R.layout.redact_contact, null);
                                etuName = redactView.findViewById(R.id.etu_name);
                                etuPhoneNum = redactView.findViewById(R.id.etu_phoneNum);
                                btnSave = redactView.findViewById(R.id.btn_save);
                                btnSave.setVisibility(View.GONE);
                                etuName.setText(datas.get(param).getName());
                                etuPhoneNum.setText(datas.get(param).getNumber());
                                addcontact.setView(redactView);
                                addcontact.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ContentValues values = new ContentValues();
                                        contact.setName(etuName.getText().toString().trim());
                                        contact.setNumber(etuPhoneNum.getText().toString().trim());
                                        values.put("name", contact.getName());
                                        values.put("phonenumber", contact.getNumber());
                                        int update = contactDao.update(values, updateByName);
                                        if (update > 0) {
                                            Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                                        }
                                        readContacts();
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                addcontact.setNegativeButton("取消", null);
                                addcontact.create().show();
                                break;
                            case 1:
                                AlertDialog.Builder edit3 = new AlertDialog.Builder(MainActivity.this);
                                edit3.setTitle("删除联系人");
                                edit3.setTitle("确定删除吗");
                                edit3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        View redactView = getLayoutInflater().inflate(R.layout.redact_contact, null);
                                        int delete = contactDao.deleteByName(datas.get(param));
                                        if (delete > 0) {
                                            Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                        }
                                        readContacts();
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                edit3.setNeutralButton("取消", null);
                                edit3.create().show();
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

        //找到控件
        FloatingActionButton fab = findViewById(R.id.fab);
        //设置监听
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this,"FAB clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });
    }

    //查询所有联系人信息
    private void readContacts() {
        datas.clear();
        try {
            cursor = contactDao.selectAll();
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permisssions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                } else {
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    class MyAdapter extends BaseAdapter {
        //得到item的总数
        @Override
        public int getCount() {
            return datas.size();
        }

        //得到每个position位置上的item代表的对象
        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        //得到item的id
        @Override
        public long getItemId(int position) {
            return position;
        }

        //得到item的view视图
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (!datas.isEmpty()) {
                if (convertView == null) {
                    //利用布局填充器填充自定义的ListView item的布局
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.contacts_list, null);
                    holder = new ViewHolder();
                    //利用得到的view来进行findViewById()
                    //holder.ivHead =convertView.findViewById(R.id.iv_head);
                    holder.tvName = convertView.findViewById(R.id.tvl_name);
                    holder.tvNumber = convertView.findViewById(R.id.tvl_phoneNum);
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
        class ViewHolder {
            ImageView ivHead;
            TextView tvName;
            TextView tvNumber;
        }
    }
}
