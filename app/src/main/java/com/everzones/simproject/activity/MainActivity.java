package com.everzones.simproject.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.everzones.simproject.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tv_subId1;
    private TextView tv_subId2;
    private TextView tv_simCount;
    private TextView tv_info1;
    private TextView tv_info2;
    private TextView tv_imsi1;
    private TextView tv_imsi2;
    private TextView tv_imei1;
    private TextView tv_imei2;
    private TextView tv_spn1;
    private TextView tv_spn2;
    private TextView tv_slot1;
    private TextView tv_slot2;
    private TextView tv_nativeNum;
    private TextView tv_nativeNum2;
    private int simCount;
    private SubscriptionInfo info;
    private SubscriptionInfo info2;
    List<SubscriptionInfo> list = new ArrayList<>();
    TelephonyManager tm;
    SubscriptionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {

        Log.e("pk","包名："+SubscriptionManager.class.getPackage().toString()+"-->类名"+SubscriptionManager.class.getName());
        //获取TelephonyManager实例对象
        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //获取SubscriptionManager对象
        sm = SubscriptionManager.from(this);

        //获取SIM卡个数
        //simCount = tm.getPhoneCount();
        simCount = sm.getActiveSubscriptionInfoCount();
        tv_simCount.setText(simCount+"张卡");

        //SubscribInfo
            if(simCount==2){
                list = sm.getActiveSubscriptionInfoList();//获取所有sim卡的信息集合
                info = list.get(0);//卡1的sim卡信息
                info2 = list.get(1);//卡2的sim卡信息
                tv_info1.setText(info.toString());
                tv_info2.setText(info2.toString());
                Log.e("info",info.toString());
            }else {
                tv_info1.setText("无卡");
                tv_info2.setText("无卡");
            }

            try{
        //获取双卡的subId
        //tv_subId1.setText(getSubId(0,this)+"");
        //tv_subId2.setText(getSubId(1,this)+"");
        tv_subId1.setText(info.getSubscriptionId()+"");
        tv_subId2.setText(info2.getSubscriptionId()+"");

        //IMSI
        //String  IMSI1 = tm.getSubscriberId();//方1
        String IMSI1=getSubscriberId(info);//方2
        tv_imsi1.setText(IMSI1);
        String IMSI2=getSubscriberId(info2);
        tv_imsi2.setText(IMSI2);

        //IMEI
        tv_imei1.setText(getDeviced(this,info.getSimSlotIndex())+"");
        tv_imei2.setText(getDeviced(this,info2.getSimSlotIndex())+"");

        //获取服务提供商的名字
        String SPN1 = tm.getSimOperatorName();
        tv_spn1.setText(SPN1);
        String SPN2 = getSimOperatorName(info2);
        tv_spn2.setText(SPN2);

        //获取卡槽slotId
        tv_slot1.setText(info.getSimSlotIndex()+"");
        tv_slot2.setText(info2.getSimSlotIndex()+"");

        //手机号
        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String te1 = tm.getLine1Number();//获取卡1号码(方法1)
        tv_nativeNum.setText(getNumber(0,this));//方法2
        tv_nativeNum2.setText(getNumber(-1,this));
            }catch (Exception e){
                Log.e("error","出错啦~"+e.getMessage());
            }
        }

    }

    private void initView() {
        tv_simCount = (TextView) findViewById(R.id.tv_simCount);
        tv_subId1 = (TextView) findViewById(R.id.tv_subId1);
        tv_subId2 = (TextView) findViewById(R.id.tv_subId2);
        tv_info1 = (TextView) findViewById(R.id.tv_info1);
        tv_info2 = (TextView) findViewById(R.id.tv_info2);
        tv_imsi1 = (TextView) findViewById(R.id.tv_imsi1);
        tv_imsi2 = (TextView) findViewById(R.id.tv_imsi2);
        tv_imei1 = (TextView) findViewById(R.id.tv_imei1);
        tv_imei2 = (TextView) findViewById(R.id.tv_imei2);
        tv_spn1 = (TextView) findViewById(R.id.tv_spn1);
        tv_spn2 = (TextView) findViewById(R.id.tv_spn2);
        tv_slot1 = (TextView) findViewById(R.id.tv_slot1);
        tv_slot2 = (TextView) findViewById(R.id.tv_slot2);
        tv_nativeNum = (TextView) findViewById(R.id.tv_nativeNum);
        tv_nativeNum2 = (TextView) findViewById(R.id.tv_nativeNum2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    //imsi
    private String getSubscriberId(SubscriptionInfo sir) {
        if (sir == null) {
            return null;
        }
        return getSubscriberId(sir.getSubscriptionId());
    }

    //获取imsi
    private String getSubscriberId(int subId) {
        String subscriberId = null;
        try {
            Method method = TelephonyManager.class.getDeclaredMethod("getSubscriberId", int.class);
            subscriberId = (String) method.invoke(tm, subId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return subscriberId;
    }

    //获取双卡SPN方法

    /**
     * @param sir:sim卡的信息
     * @return
     */
    private String getSimOperatorName(SubscriptionInfo sir) {
        if (sir == null) {
            return null;
        }
        return getSimOperatorName(sir.getSubscriptionId());
    }
    private String getSimOperatorName(int subId) {
        String simOperatorName = null;
        try {
            Method method = TelephonyManager.class.getDeclaredMethod("getSimOperatorNameForSubscription", int.class);
            simOperatorName = (String) method.invoke(tm, subId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return simOperatorName;
    }

    //获取subId的第二种方法,根据slotId获取

    /**
     * @param slotId:卡槽的序号：0代表卡槽1,1代表卡槽2
     * @param context：上下文
     * @return：subId（可以理解为sim卡的索引）
     */
    public static int getSubId(int slotId, Context context) {
        Uri uri = Uri.parse("content://telephony/siminfo");
        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            cursor = contentResolver.query(uri, new String[] {"_id", "sim_id"}, "sim_id = ?", new String[] {String.valueOf(slotId)}, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    return cursor.getInt(cursor.getColumnIndex("_id"));
                }
            }
        } catch (Exception e) {
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return -1;
    }

    //获取双卡的手机号码
    public static String getNumber(int slotId,Context context){
        Uri uri = Uri.parse("content://telephony/siminfo");
        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            cursor = contentResolver.query(uri, new String[] {"number", "sim_id"}, "sim_id = ?", new String[] {String.valueOf(slotId)}, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex("number"));
                }
            }
        } catch (Exception e) {
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return null;
    }

    //根据subId得到slotId
    /**
     * http://www.jianshu.com/p/7b7e771a07cd
     *  _id:主键，有多少条数据，就说明保存了多少个sim卡的信息；
     * icc_id：卡的唯一身份标识（身份证）
     * sim_id：卡的id序号，与卡槽匹配，-1无卡
     * display_name:分配给卡的显示名字，从代码上来看开机后会尝试使用运营商名字，如果取不到，就使用简单的SUB 01这样的字串表示，等拿到运营商名字之后重新set，另外从下面的name_source字段的设计来看，android是允许用户来自己指定这个显示名的
     * name_source:表明display_name字段的来源，有两种来源，一是系统自动，name_source取值为0，另一种就是来自用户指定，取值为1；
     * number:该卡对应的号码
     */
    public static int getSlotId(Context context) {
        Uri uri = Uri.parse("content://telephony/siminfo");
        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            cursor = contentResolver.query(uri, null, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    return cursor.getInt(cursor.getColumnIndex("sim_id"));
                }
            }
        } catch (Exception e) {
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return -1;
    }

    // imei(国际移动设备标志，15位)
    /**
     * @param context:上下文
     * @param soltId：卡槽的序号，0代表卡槽1；1代表卡槽2
     * @return
     */
    public static String getDeviced(Context context, int soltId) {
        return (String) getPhoneInfo(soltId, "getDeviceId", context);
    }

    //通过subId和方法名来获取该方法的返回值
    public static Object getPhoneInfo(int subId, String methodName, Context context) {
        Object value = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= 21) {
                Method method = tm.getClass().getMethod(methodName, getMethodParamTypes(methodName));
                if (subId >= 0) {
                    value = method.invoke(tm, subId);
                }
            }
        } catch (Exception e) {
        }
        return value;
    }

    //通过方法名来获取方法的参数列表
    public static Class[] getMethodParamTypes(String methodName) {
        Class[] params = null;
        try {
            Method[] methods = TelephonyManager.class.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methodName.equals(methods[i].getName())) {
                    params = methods[i].getParameterTypes();
                    if (params.length >= 1) {
                        //LogUtil.d("length:", "" + params.length);
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return params;
    }
}
