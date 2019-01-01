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

public class PeopleActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linearLayout;
    private TextView tviName;
    private TextView tviPhoneNum;

    //Data Access Object:对contact表进行CRUD操作的类
    private ContactDao dao;
    private String name;
    private String phoneNum;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_info);
        linearLayout = findViewById(R.id.call);

        tviName = findViewById(R.id.tvi_name);
        tviPhoneNum = findViewById(R.id.tvi_phoneNum);

        //动态权限申请
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            readContacts();
        }

        //给ListView添加item点击事件监听器
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拨打电话
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phoneNum);
                intent.setData(data);
                startActivity(intent);
            }
        });
    }

    private void readContacts() {

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phoneNum = intent.getStringExtra("phoneNum");
        try {
            if (name != null && !name.equals("") && phoneNum != null && !phoneNum.equals("")) {
                tviName.setText(name);
                tviPhoneNum.setText(phoneNum);
            } else {
                tviName.setText("");
                tviPhoneNum.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
    }
}