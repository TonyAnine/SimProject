package com.everzones.simproject.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.everzones.simproject.R;
import com.everzones.simproject.domain.CallInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CallLogActivity extends Activity {
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calllog_layout);

        // 条目显示
        ListView lv = (ListView) findViewById(R.id.lv_callLog);
        List<CallInfo> infos = null;
        try {
            infos = getCallInfos(this);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        adapter = new MyAdapter(infos);
        String pk = CallLog.class.getPackage().getName();

        Log.e("pk",pk);
        lv.setAdapter(adapter);
    }

    private class MyAdapter extends BaseAdapter {
        private List<CallInfo> infos;
        private LayoutInflater mInflater;

        public MyAdapter(List<CallInfo> infos) {
            super();
            this.infos = infos;
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int position) {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 加载布局
            View view = mInflater.inflate(R.layout.calllog_item, null);
            // 获取控件
            TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
            TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
            TextView tv_type = (TextView) view.findViewById(R.id.tv_type);
            TextView tv_sim1 = (TextView) view.findViewById(R.id.tv_sim);

            // 设置控件内容
            CallInfo info = infos.get(position);
            // 号码
            tv_number.setText(info.number);
            // 日期
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String dateString = format.format(info.date);
            tv_date.setText(dateString);
            //sim卡
            Log.e("sim",info.simid+"");
            tv_sim1.setText(info.simid);

            // 类型
            String type = null;
            int textColor = 0;
            switch (info.type){
                case CallLog.Calls.INCOMING_TYPE: // 来电，字体蓝色
                    type = "来电";
                    textColor = Color.BLUE;
                    break;
                case CallLog.Calls.OUTGOING_TYPE: // 去电，字体绿色
                    type = "去电";
                    textColor = Color.GREEN;
                    break;
                case CallLog.Calls.MISSED_TYPE:   // 未接，字体红色
                    type = "未接";
                    textColor = Color.RED;
                    break;
            }
            tv_type.setText(type);
            tv_type.setTextColor(textColor);
            return view;
        }
    }

    //获取相关的sim卡信息
    public static List<CallInfo> getCallInfos(Context context) throws InvocationTargetException {
        Cursor cursor = null;
        Uri uri = null;
        List<CallInfo> infos = new ArrayList<CallInfo>();
        ContentResolver resolver = context.getContentResolver();
        boolean s_bSamsung = true;//判断是否为三星
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT && s_bSamsung){
            uri = Uri.parse("content://logs/call");//三星手机
        }else {
            uri = CallLog.Calls.CONTENT_URI;
        }
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CALL_LOG},1);
            }else {
                cursor = resolver.query(uri, null, null, null, null);
                while (cursor.moveToNext()){
                    String number = cursor.getString(cursor.getColumnIndex("number"));
                    long date = cursor.getLong(cursor.getColumnIndex("date"));
                    int type = cursor.getInt(cursor.getColumnIndex("type"));
                    //小米手机没有双卡双待识别的功能
                    String  slotId = cursor.getString(cursor.getColumnIndex("sim_id"));//适用于三星手机
                    //String slotId = cursor.getString(cursor.getColumnIndex("simid"));//魅族手机
                    String slotStr;
                    if (slotId.equals("0")){
                        Log.e("pk","slotId==0");
                        slotStr = "卡1";
                    }else if(slotId.equals("1")){
                        slotStr = "卡2";
                    }else {
                        slotStr = "未知";
                    }
                    infos.add(new CallInfo(number, date, type,slotStr));
                }
                cursor.close();
            }
        }catch (Exception e){
            Log.e("mi",e.getMessage());
        }
        return infos;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            }
        }else{
            Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
        }
    }

    public static int getSlotIdUsingSubId(int subId,Context context) throws InvocationTargetException {
        int  result = 0;
        try {
            Class<?> clz = Class.forName(SubscriptionManager.class.getName());
            Object subSm;
            Constructor<?> constructor = clz.getDeclaredConstructor(Context.class);
            subSm  = constructor.newInstance(context);
            Method mth = clz.getMethod("getSlotId", int.class);
            result = (int)mth.invoke(subSm, subId);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }
}
