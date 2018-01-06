package com.zhupiter.hspot.cilent.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by zhupiter on 17-1-30.
 */

public class DbManager {

    private static final int db_version = 1;
    private static final String db_name = "HSpotDB.db";

    private static final String TAG = "DbManager";
    private SQLiteDatabase db = null;
    private DbHelper mHelper;
    private Context mContext;

    public DbManager(Context context){
        mHelper = new DbHelper(context, db_name, null, db_version);
        db = mHelper.getWritableDatabase();
        this.mContext = context;
    }

    //关闭数据库
    public void closeDataBase(){
        db.close();
        mHelper=null;
        db=null;
    }

    /*****************************
     * 插入数据
     * *************************************************/

    public void insert(String SQL) {
        if (db == null) {
            return;
        }
        db.execSQL(SQL);
    }

    //插入并返回主键id值
    public long insert(String table, ContentValues values){
        if (db == null) {
            return -1;
        }
        return db.insert(table, null, values);
    }

    /******************************
     * 查询
     **********************************************/

    public Cursor find(String table, String[] columns, String selection, String[] selectionArgs,
                       String groupBy, String having, String orderBy) {
        if (db == null) {
            return null;
        }
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public void SQLFind(String sql) {
        if (db == null) {
            return;
        }
        db.execSQL(sql);
    }

    public Cursor findAll(String tableName) {
        return find(tableName,null,null,null,null,null,null);
    }

    public Cursor findByArgs(String tableName, String select, String[] selectArgs) {
        return find(tableName, null, select, selectArgs, null, null, null);
    }

    public int getAllCount(String tableName) {
        int count = 0;
        Cursor cursor = findAll(tableName);
        while (cursor.moveToNext()) {
            count++;
        }
        return count;
    }

    public int getArgsCount(String tableName, String select, String[] selectArgs) {
        int count = 0;
        Cursor cursor = findByArgs(tableName, select, selectArgs);
        while (cursor.moveToNext()) {
            count++;
        }
        return count;
    }

    /***
     * 删除以及更新
     */

    //删除数据
    public int deleteDB(String tableName, String select, String[] selectArgs){
        if (db == null) {
            return -1;
        }
        return db.delete(tableName, select, selectArgs);
    }

    public int update(String tableName, ContentValues values, String select, String[] selectArgs) {
        if (db == null) {
            return -1;
        }
        return db.update(tableName, values, select, selectArgs);
    }

    /**
     * 数据库帮助类
     */

    class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context, String db_name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context,db_name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE Site(Sid integer primary key autoincrement," +
                    " siteURL text not null, siteName text not null, coverURL text," +
                    " searchURL not null, siteRule text not null, comicRule text not null," +
                    " chapterRule text not null, imageRule text not null)");
            db.execSQL("CREATE TABLE Comic(Cid integer primary key autoincrement," +
                    " belonged varchar(10) not null check (belonged = 'history' or " +
                    " belonged = 'download' or belonged='favorite')," +
                    " title text not null," + " author text, language text," +
                    " tag text not null, upTime date not null)");
            db.execSQL("CREATE TABLE Chapter(Chid integer primary key autoincrement," +
                    " chName text not null, chURL text not null," + " downloadStatue text," +
                    " comic integer not null," +
                    " FOREIGN KEY(comic) REFERENCES Comic(Cid))");
            db.execSQL("CREATE TABLE Categories(siteID integer not null," +
                    " cateName text not null, cateURL text not null," +
                    " FOREIGN KEY(siteID) REFERENCES Site(Sid))");
            db.execSQL("CREATE TABLE SearchHistory(history text primary key)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("DBHelper","onUpdate, oldVersion = " + oldVersion + ", newVersion = " + newVersion);
            //第一版暂时还用不到，以后升级时使用
        }

    }
}
