package com.zpx.contact;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddContactActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvaName;
    private TextView tvaPhoneNum;
    private EditText etaName;
    private EditText etaPhoneNum;
    private Button btnSave;

    //Data Access Object:对contact表进行CRUD操作的类
    private ContactDao contactDao;
    private String name;
    private String phoneNum;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);
        btnSave = findViewById(R.id.btn_save);

        tvaName = findViewById(R.id.tva_name);
        tvaPhoneNum = findViewById(R.id.tva_phoneNum);

        contactDao = new ContactDao(this);
        //初始化组件
        initView();
    }

    private void initView() {
        tvaName = findViewById(R.id.tva_name);
        tvaPhoneNum = findViewById(R.id.tva_phoneNum);
        etaName = findViewById(R.id.eta_name);
        etaPhoneNum = findViewById(R.id.eta_phoneNum);
        btnSave = findViewById(R.id.btn_add);
        //为按钮注册监听
        btnSave.setOnClickListener(this);
    }

    public void onClick(View v) {
        name = etaName.getText().toString().trim();
        phoneNum = etaPhoneNum.getText().toString().trim();
        try {
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phoneNum)) {
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("phonenumber", phoneNum);
                if (values != null) {
                    long number = contactDao.insert(values);
                    if (number > 0) {
                        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}