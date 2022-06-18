package com.example.database_demo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Hao
 * @create 2022/6/3 11:54
 */
public class CommonDatabase {
    private DBOpenHelper dbHelper;
    private SQLiteDatabase sqlite;

    public SQLiteDatabase getSqliteObject(Context context, String db_name) {
        dbHelper = new DBOpenHelper(context, db_name, null, 1);
        sqlite = dbHelper.getWritableDatabase();
        return sqlite;
    }
}
