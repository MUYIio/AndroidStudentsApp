package com.example.database_demo.administrator;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database_demo.R;
import static com.example.database_demo.database.MD5Demo.md5;

import com.example.database_demo.database.CommonDatabase;

/**
 * 实现添加管理员信息
 * @author Hao
 * @create 2022/6/1 12:47
 */
public class Addadmin extends AppCompatActivity {
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);

        db =new CommonDatabase().getSqliteObject(Addadmin.this,"test_db");

        final EditText edit_admin_account = findViewById(R.id.edit_admin_account);
        final EditText edit_admin_password = findViewById(R.id.edit_admin_password);

        Button change = findViewById(R.id.button_add_admin_ok);
        change.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String account = edit_admin_account.getText().toString();
                final String password = edit_admin_password.getText().toString();
                ContentValues values = new ContentValues();
                values.put("account",account);
                values.put("password",md5(password));
                db.insert("administractor",null,values);
                Toast.makeText(Addadmin.this,"增加管理员成功！",Toast.LENGTH_SHORT).show();
                finish();

            }
        });
    }
}
