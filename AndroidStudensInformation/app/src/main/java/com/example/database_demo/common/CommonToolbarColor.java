package com.example.database_demo.common;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import com.example.database_demo.R;

/**
 * @author Hao
 * @create 2022/6/7 13:44
 */
public class CommonToolbarColor {
    public void toolbarColorSet(Activity activity)
    {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getColor(R.color.colorPrimary));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
