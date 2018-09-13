package com.everzones.simproject.service;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;

import com.everzones.simproject.domain.CallInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunny on 2017/8/14.
 * anthor:sunny
 * date: 2017/8/14
 * function:
 */

public class CallInfoService {
    /**
     * 获取通话记录
     * @param context 上下文。通话记录需要从系统的【通话应用】中的内容提供者中获取，内容提供者需要上下文。
     * 通话记录保存在联系人数据库中：data/data/com.android.provider.contacts/databases/contacts2.db库中的calls表。
     * @return 包含所有通话记录的一个集合
     */
    public static List<CallInfo> getCallInfos(Context context) {
        Cursor cursor = null;
        List<CallInfo> infos = new ArrayList<CallInfo>();
        ContentResolver resolver = context.getContentResolver();
        // uri的写法需要查看源码JB\packages\providers\ContactsProvider\AndroidManifest.xml中内容提供者的授权
        // 从清单文件可知该提供者是CallLogProvider，且通话记录相关操作被封装到了Calls类中
        Uri uri = CallLog.Calls.CONTENT_URI;
        String[] projection = new String[]{
                CallLog.Calls.CACHED_FORMATTED_NUMBER, // 号码
                CallLog.Calls.DATE,   // 日期
                CallLog.Calls.TYPE   // 类型：来电、去电、未接
        };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CALL_LOG},1);
        }else {
            cursor = resolver.query(uri, null, null, null, null);
            while (cursor.moveToNext()){
           /* String number = cursor.getString(0);
            long date = cursor.getLong(1);
            int type = cursor.getInt(2);*/
                String number = cursor.getString(cursor.getColumnIndex("number"));
                long date = cursor.getLong(cursor.getColumnIndex("date"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                int simid = cursor.getInt(cursor.getColumnIndex("simid"));
                //infos.add(new CallInfo(number, date, type,simid+""));
            }
            cursor.close();
                    }
                    return infos;
                    }
                    }
