package com.android.waylinkage.core.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.waylinkage.bean.SearchHistoryBean;
import com.android.waylinkage.bean.ThreadInfo;
import com.android.waylinkage.core.fileload.FileLoadInfo;
import com.android.waylinkage.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.R.attr.id;

/**
 * 数据库具体操作类
 * Created by zeng on 2016/5/19.
 */
public class DatabaseManager {

    public static final String TAG = DatabaseManager.class.getSimpleName();

    private static DatabaseManager manager = null;
    private SQLiteDatabase db;

    private DatabaseManager(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public static DatabaseManager getInstance(Context context) {
        if (manager == null) {
            manager = new DatabaseManager(context);
        }
        return manager;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        if (db != null) {
            db.close();
        }
    }

    //////////////////////////////////////  FileInfo 表的增删改查 ///////////////////////////////////////////////


    /**
     * 添加FileLoadInfo到数据库中
     *
     * @param info 文件信息
     */
    public void addFileLoadInfo(FileLoadInfo info) {

        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME_FILEINFO + " where url = ?", new String[]{info.getUrl()});

        if (cursor.moveToFirst()) {
            this.deleteFileLoadInfoByUrl(info.getUrl());
        }
        cursor.close();

        String sql = "INSERT INTO " + DatabaseHelper.TABLE_NAME_FILEINFO + " VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?);";

        db.execSQL(sql, new Object[]{info.getName(), info.getUrl(), info.getMd5(), info.getPackageName(), info.getVersionCode(), info.getLength(), info.getFinished(), info.getStatus(),
                info.getTitle(), info.getPreviewUrl(), info.getServerId(), info.getType()});

        Log.d(TAG, "数据库操作：添加FileInfo成功");
    }

    /**
     * 通过URL删除文件信息
     *
     * @param url 文件的下载路径
     */
    public void deleteFileLoadInfoByUrl(String url) {
        db.execSQL("delete from " + DatabaseHelper.TABLE_NAME_FILEINFO + " where url = ?", new Object[]{url});
        Log.d(TAG, "删除文件：" + url + "在FileInfo表中的信息");
    }

    /**
     * 更新FileInfo的字段
     *
     * @param finished 文件下载进度
     * @param status   文件当前下载状态
     * @param url      文件下载地址
     */
    public synchronized void updateFileLoadInfoColumn(long finished, int status, String url) {

        db.execSQL("update " + DatabaseHelper.TABLE_NAME_FILEINFO + " set finished = ? , status = ?  where url = ?;", new Object[]{finished, status, url});

        Log.d(TAG, "更新FileInfo的finished 和 status 字段 finished " + finished + " status " + status);

    }

    /**
     * 通过apk包名查询 文件下载信息
     *
     * @param packageName apk文件包名
     * @return 下载文件的数据库中记录
     */
    public FileLoadInfo queryFileLoadInfoByPackageName(String packageName) {

        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME_FILEINFO + " where packageName = ?", new String[]{packageName});

        FileLoadInfo info = new FileLoadInfo();
        while (cursor.moveToNext()) {

            info.setId(cursor.getInt(cursor.getColumnIndex("id")));
            info.setName(cursor.getString(cursor.getColumnIndex("name")));
            info.setUrl(cursor.getString(cursor.getColumnIndex("fileUrl")));
            info.setMd5(cursor.getString(cursor.getColumnIndex("md5")));
            info.setPackageName(cursor.getString(cursor.getColumnIndex("packageName")));
            info.setVersionCode(cursor.getInt(cursor.getColumnIndex("versionCode")));
            info.setLength(cursor.getInt(cursor.getColumnIndex("length")));
            info.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            info.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

            info.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            info.setPreviewUrl(cursor.getString(cursor.getColumnIndex("previewUrl")));
            info.setServerId(cursor.getInt(cursor.getColumnIndex("serverId")));
            info.setType(cursor.getInt(cursor.getColumnIndex("type")));

            /*Log.e(TAG,"id "+info.id+" | name "+info.name+" | fileUrl "+info.fileUrl+" | md5 "+info.md5 +
                    " | packageName "+info.packageName+" | length "+info.length +" | finished "+info.finished+" | status "+info.status);*/
        }
        cursor.close();

        return info;
    }

    /**
     * 查询本地库中所有file信息
     *
     * @param type 0.所有 1.游戏 2.视频
     * @return 数据库中的下载文件信息
     */
    public List<FileLoadInfo> queryAllFileLoadInfo(int type) {

        ArrayList<FileLoadInfo> fileInfoList = new ArrayList<>();

        Cursor cursor;
        if (type == 0) {
            cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME_FILEINFO, null);
        } else {
            cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME_FILEINFO + " where type = ?", new String[]{String.valueOf(type)});
        }

        while (cursor.moveToNext()) {
            FileLoadInfo info = new FileLoadInfo();
            info.setId(cursor.getInt(cursor.getColumnIndex("id")));
            info.setName(cursor.getString(cursor.getColumnIndex("name")));
            info.setUrl(cursor.getString(cursor.getColumnIndex("fileUrl")));
            info.setMd5(cursor.getString(cursor.getColumnIndex("md5")));
            info.setPackageName(cursor.getString(cursor.getColumnIndex("packageName")));
            info.setVersionCode(cursor.getInt(cursor.getColumnIndex("versionCode")));
            info.setLength(cursor.getInt(cursor.getColumnIndex("length")));
            info.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            info.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

            info.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            info.setPreviewUrl(cursor.getString(cursor.getColumnIndex("previewUrl")));
            info.setServerId(cursor.getInt(cursor.getColumnIndex("serverId")));
            info.setType(cursor.getInt(cursor.getColumnIndex("type")));

            fileInfoList.add(info);

            /*Log.e(TAG,"id "+info.id+" | name "+info.name+" | fileUrl "+info.fileUrl+" | md5 "+info.md5 +
                    " | packageName "+info.packageName+" | length "+info.length +" | finished "+info.finished+" | status "+info.status);*/
        }
        cursor.close();


        return fileInfoList;
    }


    //////////////////////////////////////  ThreadInfo 表的增删改查 ///////////////////////////////////////////////

    /**
     * 添加下载线程的信息到数据库中
     *
     * @param info 线程信息
     */
    public void addThreadInfo(ThreadInfo info) {

        String sql = "INSERT INTO " + DatabaseHelper.TABLE_NAME_THREADINFO + " VALUES(null,?,?,?,?,?);";
        db.execSQL(sql, new Object[]{info.name, info.url, info.start, info.finished, info.end});

        Log.d(TAG, "数据库操作：添加下载线程信息成功！");
    }

    /**
     * 通过ID删除线程信息
     * @param id 线程信息的ID
     */
    /*public void deleteThreadInfoById(int id){
        db.execSQL("delete from "+DatabaseHelper.TABLE_NAME_THREADINFO+" where id = ?",new Object[]{id});

        Log.e(TAG,"数据库操作：删除下载线程 "+id+" 的信息成功！");
    }*/

    /**
     * 通过URL删除线程信息
     *
     * @param url 线程信息的url
     */
    public void deleteThreadInfoByUrl(String url) {
        db.execSQL("delete from " + DatabaseHelper.TABLE_NAME_THREADINFO + " where url = ?", new Object[]{url});
        Log.d(TAG, "数据库操作：删除下载线程 " + url + " 的信息成功！");
    }

    /**
     * 更新下载线程的字段
     *
     * @param info 线程信息
     */
    public void updateThreadInfoFinishedColumn(ThreadInfo info) {
        db.execSQL("update " + DatabaseHelper.TABLE_NAME_THREADINFO + " set finished = ? where id = ?;", new Object[]{info.finished, info.id});
        Log.d(TAG, "数据库操作：更新下载线程 " + info.url + " 的 finished = " + info.finished + " 字段成功！");
    }

    /**
     * 查询文件的下载线程信息
     *
     * @param url 文件的下载地址
     */
    public ArrayList<ThreadInfo> queryThreadInfoByUrl(String url) {

        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME_THREADINFO + " where url = ?", new String[]{url});
        ArrayList<ThreadInfo> infoList = new ArrayList<>();
        while (cursor.moveToNext()) {
            ThreadInfo info = new ThreadInfo();
            info.id = cursor.getInt(cursor.getColumnIndex("id"));
            info.name = cursor.getString(cursor.getColumnIndex("name"));
            info.url = cursor.getString(cursor.getColumnIndex("fileUrl"));
            info.start = cursor.getLong(cursor.getColumnIndex("start"));
            info.finished = cursor.getLong(cursor.getColumnIndex("finished"));
            info.end = cursor.getLong(cursor.getColumnIndex("end"));
            infoList.add(info);
        }
        cursor.close();

        return infoList;
    }

    ///////////////////////////////////// searchHistory 表的增删改查 ////////////////////////////////////////

    /**
     * 添加搜索历史
     *
     * @param title 搜索信息
     */
    public void addSearchHistory(String title) {
        if (null==title) {
            return;
        }
        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME_SEARCH_HISTORY + " where title = ?", new String[]{title});
        if (cursor.moveToFirst()) {
            //存在搜索记录
            String sql = "UPDATE " + DatabaseHelper.TABLE_NAME_SEARCH_HISTORY + " SET date = ?  where title = ?;";
            db.execSQL(sql, new Object[]{System.currentTimeMillis(), title});
        } else {
            //不存在搜索记录
            String sql = "INSERT INTO " + DatabaseHelper.TABLE_NAME_SEARCH_HISTORY + " VALUES(null,?,?,?);";
            db.execSQL(sql, new Object[]{"1", title, System.currentTimeMillis()});
        }
        Log.d(TAG, "数据库操作：添加搜索记录成功！");

    }

    /**
     * 删除所有搜索记录
     */
    public void deleteAllSearchHistory() {
        db.execSQL("delete from " + DatabaseHelper.TABLE_NAME_SEARCH_HISTORY);
        Log.d(TAG, "删除所有搜索记录");
    }

    /**
     * 通过ID删除搜索记录
     *
     * @param title
     */
    public void deleteSearchHistoryById(String title) {
        db.execSQL("delete from " + DatabaseHelper.TABLE_NAME_SEARCH_HISTORY + " where title = ?", new Object[]{title});
        Log.d(TAG, "删除搜索记录：" + id);
    }

    /**
     * 查询搜索记录（需求最多存5条）
     *
     * @return
     */
    public List<SearchHistoryBean> queryAllSearchHistory() throws ParseException {
        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME_SEARCH_HISTORY
                + " order by date desc limit 5", new String[]{});
        ArrayList<SearchHistoryBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            SearchHistoryBean searchHistoryBean = new SearchHistoryBean();

            searchHistoryBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            searchHistoryBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            searchHistoryBean.setType(cursor.getString(cursor.getColumnIndex("type")));
            long time = cursor.getLong(cursor.getColumnIndex("date"));
            searchHistoryBean.setDate(time);
            list.add(searchHistoryBean);
        }
        cursor.close();
        return list;
    }

    /**
     * 获取刚存储对象的自增长ID
     *
     * @param tableName 表名
     * @return 最新的自增长ID值
     */
    public int getLastRowId(String tableName) {

        Cursor cursor = db.rawQuery("select last_insert_rowid() from " + tableName, null);
        int id = 0;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    private Long getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }

    private Long getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime().getTime();
    }

}
