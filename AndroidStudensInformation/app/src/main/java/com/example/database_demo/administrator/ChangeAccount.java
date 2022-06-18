package com.example.database_demo.administrator;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import static com.example.database_demo.database.MD5Demo.md5;
import com.example.database_demo.R;
import com.example.database_demo.database.CommonDatabase;

/**
 * 管理员用于修改学生登录账户信息
 * @author Hao
 * @create 2022/6/1 12:56
 */
public class ChangeAccount extends AppCompatActivity {
    SQLiteDatabase db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account);

        final EditText edit_account = findViewById(R.id.edit_account);
        final EditText edit_password = findViewById(R.id.edit_account_password);
        Button button_account_change = findViewById(R.id.button_account_change);
        Button button_account_back = findViewById(R.id.button_account_back);
        //获取进来这个活动的intent
        final Intent intent = getIntent();

        edit_account.setText(intent.getStringExtra("account"));

        //初始化数据库对象
        CommonDatabase commonDatabase = new CommonDatabase();
        db = commonDatabase.getSqliteObject(ChangeAccount.this,"test_db");

        View.OnClickListener listener = new View.OnClickListener(

        ){
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.button_account_change:
                        if(edit_password.getText().toString().equals(""))
                        {
                            Toast.makeText(ChangeAccount.this,"登陆密码不能为空！",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            ContentValues values = new ContentValues();
                            values.put("account",edit_account.getText().toString());
                            values.put("password",md5(edit_password.getText().toString()));

                            db.update("load_account", values, "account = ? ", new String[]{intent.getStringExtra("account")});
                            Toast.makeText(ChangeAccount.this,"修改成功",Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case R.id.button_account_back:
                        finish();
                        break;
                    default:
                }
            }
        };
        button_account_back.setOnClickListener(listener);
        button_account_change.setOnClickListener(listener);
    }
}
