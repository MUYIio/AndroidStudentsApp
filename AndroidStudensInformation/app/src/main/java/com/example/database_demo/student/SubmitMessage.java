package com.example.database_demo.student;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database_demo.database.CommonDatabase;
import com.example.database_demo.R;

/**
 * @author Hao
 * @create 2022/6/5 14:13
 */
public class SubmitMessage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_message);
        final EditText editText = findViewById(R.id.et_message);
        Button button_submit = findViewById(R.id.button_submit);


        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(SubmitMessage.this, "留言不可以为空", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("student_id", getIntent().getStringExtra("student_id"));
                    contentValues.put("message", editText.getText().toString());

                    new CommonDatabase().getSqliteObject(SubmitMessage.this, "test_db").
                            insert("message", null, contentValues);
                    Toast.makeText(SubmitMessage.this, "留言成功！", Toast.LENGTH_SHORT).show();
                    finish();
                }


            }
        });
    }
}
