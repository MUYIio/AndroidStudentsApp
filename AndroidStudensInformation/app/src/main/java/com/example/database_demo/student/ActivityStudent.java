package com.example.database_demo.student;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.database_demo.common.CommonMethon;
import com.example.database_demo.common.CommonToolbarColor;
import com.example.database_demo.database.CommonDatabase;
import com.example.database_demo.database.image_store;
import com.example.database_demo.utils.AlertDialogBuilder;
import com.example.database_demo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 学生端主页面
 * @author Hao
 * @create 2022/6/7 13:52
 */
public class ActivityStudent extends AppCompatActivity {
    private SQLiteDatabase db;

    /**
     * 切换用户弹出的对话框
     */
    private AlertDialog.Builder builder;

    /**
     * Toolbar用于替代原有的actionBar
     */
    private Toolbar toolbar;

    /**
     * 用于显示学生选课信息的listview
     */
    private ListView listView_mycourse;

    //侧滑
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    /**
     * 用于获取NavigationView的headlayout，方便监听子项
     */
    private View headview;


    private TextView textView_welcome;


    private CircleImageView circleImageView;


    private Uri imageUri;

    private static final int TAKE_PHOTO = 1;

    private image_store imageStore;

    private Intent intent_1;

    /**
     * 悬浮按钮也就是加号，用于选课
     */
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student);

        initView();

        //获取登录信息，以锁定用户
        intent_1 = getIntent();

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            //设置左箭头图片
            actionBar.setHomeAsUpIndicator(R.drawable.a);

        }

        //headlayout中的欢迎实现
        textView_welcome.setText(findNameById(intent_1.getStringExtra("student_id")));


        //菜单栏实现
        navigationView.setCheckedItem(R.id.nav_menu_myinfo);
        navigationView.setCheckedItem(R.id.nav_menu_changeacc);


        //设置标题栏与状态栏颜色保持一致
        new CommonToolbarColor().toolbarColorSet(ActivityStudent.this);

        //头像初始化
        Bitmap bitmap_temp = imageStore.getBmp(db, intent_1.getStringExtra("student_id"));

        if (bitmap_temp != null) {
            circleImageView.setImageBitmap(bitmap_temp);
        }


        //NavigationView的菜单项监听器
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_menu_myinfo:
                        Intent intent_about = new Intent(ActivityStudent.this, AboutMe.class);
                        intent_about.putExtra("student_id", intent_1.getStringExtra("student_id"));
                        startActivity(intent_about);

                        break;
                    case R.id.nav_menu_changeacc:
                        builder = new AlertDialogBuilder(ActivityStudent.this).build();
                        //   显示出该对话框
                        builder.show();

                        break;
                    //留言
                    case R.id.nav_menu_liuyan:
                        Intent intent_submit = new Intent(ActivityStudent.this, SubmitMessage.class);
                        intent_submit.putExtra("student_id", intent_1.getStringExtra("student_id"));
                        startActivity(intent_submit);
                        break;

                    //查看选课结果
                    case R.id.nav_menu_look_hcourse:

                        //两表连接查询
                        Cursor cursor = db.rawQuery(
                                "select * from student_course inner join course " +
                                        "on student_course.course_name =course.course_name " +
                                        "AND student_course.teacher_name = course.teacher_name  " +
                                        "where student_id = ?", new String[]{intent_1.getStringExtra("student_id")});
                        ArrayList<Map<String, String>> arrayList_1 = new ArrayList<Map<String, String>>();
                        if (cursor.getCount() == 0) {
                            Toast.makeText(ActivityStudent.this, "您还没有选择任何课！", Toast.LENGTH_SHORT).show();
                        } else {
                            while (cursor.moveToNext()) {
                                Map<String, String> map = new HashMap<String, String>();

                                map.put("course_time", cursor.getString(cursor.getColumnIndex("course_time")));
                                map.put("course_name", cursor.getString(cursor.getColumnIndex("course_name")));
                                map.put("teacher_name", cursor.getString(cursor.getColumnIndex("teacher_name")));
                                map.put("course_period", cursor.getString(cursor.getColumnIndex("course_period")));
                                map.put("course_weight", cursor.getString(cursor.getColumnIndex("course_weight")));
                                arrayList_1.add(map);

                            }
                            //设置适配器，并绑定布局文件
                            SimpleAdapter simpleAdapter = new SimpleAdapter(ActivityStudent.this, arrayList_1, R.layout.choose_result,
                                    new String[]{"course_name", "teacher_name", "course_time", "course_weight", "course_period"}, new int[]{R.id.result_course_name, R.id.result_teacher_name, R.id.result_time, R.id.result_weight, R.id.result_period});
                            listView_mycourse.setAdapter(simpleAdapter);
                        }

                        break;

                    default:
                        break;
                }
                return true;
            }
        });


        //为listview设定监听器
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.floatingbutton_choose_course:
                        //再将从登陆界面接受的学生学号传给选择课程的活动
                        Intent intent_2 = new Intent(ActivityStudent.this, ChooseCourse.class);
                        intent_2.putExtra("student_id", intent_1.getStringExtra("student_id"));
                        startActivity(intent_2);
                        break;
                    case R.id.circleimage:
                        // 创建File对象，用于存储拍照后的图片
                        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }
                            outputImage.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (Build.VERSION.SDK_INT < 24) {
                            imageUri = Uri.fromFile(outputImage);
                        } else {
                            imageUri = FileProvider.getUriForFile(ActivityStudent.this, "com.example.database_demo.fileprovider", outputImage);
                        }
                        // 启动相机程序
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, TAKE_PHOTO);

                        break;
                    default:
                        break;
                }
            }
        };


        floatingActionButton.setOnClickListener(listener);
        circleImageView.setOnClickListener(listener);


    }

    /**
     * 点击头像拍照实现
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void  onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示到头像中
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        circleImageView.setImageBitmap(bitmap);

                        //更新本人资源表
                        Bitmap bitmap1 = CommonMethon.compressBoundsBitmap(ActivityStudent.this, imageUri, 200, 200);
                        imageStore.update(bitmap1, db, intent_1.getStringExtra("student_id"));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();

                    }

                }
                break;
            default:
                break;
        }
    }

    private void initView() {

        //获取数据库对象
        db = new CommonDatabase().getSqliteObject(ActivityStudent.this, "test_db");

        listView_mycourse = findViewById(R.id.listview_mycourse);

        toolbar = findViewById(R.id.toolbar_student);

        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerlayout_student);

        navigationView = findViewById(R.id.navigation_view);

        headview = navigationView.inflateHeaderView(R.layout.headlayout);


        textView_welcome = headview.findViewById(R.id.welcome_textview);


        circleImageView = headview.findViewById(R.id.circleimage);

        floatingActionButton = findViewById(R.id.floatingbutton_choose_course);

        imageStore = new image_store();

    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);

                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 根据用户的学号去查找姓名
     * @param id
     * @return
     */
    public String findNameById(String id) {
        Cursor cursor = db.query("student", null, "id = ?", new String[]{id}, null, null, null, null);

        //如果没查到
        if (cursor.getCount() == 0) {
            return "无法获取您的个人信息";
        } else {
            String str = "";
            while (cursor.moveToNext()) {
                str = cursor.getString(cursor.getColumnIndex("name"));
            }
            return str + " 欢迎您！";
        }

    }
}
