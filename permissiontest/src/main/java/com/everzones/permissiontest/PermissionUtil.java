package com.everzones.permissiontest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunny on 2017/8/25.
 * anthor:sunny
 * date: 2017/8/25
 * function:检查权限的工具类
 */

public class PermissionUtil {
    //private static final String TAG = "test";
    public static final int CODE_READ_PHONE_STATE = 1;  //手机状态
    public static final int CODE_CALL_PHONE = 2;    //打电话
    public static final int CODE_GET_CONTACT = 3;  //通讯录
    public static final int CODE_READ_CALLLOG = 4;  //通话记录
    public static final int CODE_MULTI_PERMISSION = 100;

    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;  //通讯录
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;  //手机状态
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;  //打电话
    public static final String PERMISSION_READ_CALLLOG = Manifest.permission.READ_CALL_LOG; //读取通话记录

    private static final String[] requestPermissions = {
            PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_CALL_PHONE,
            PERMISSION_READ_CALLLOG
    };

    interface PermissionGrant {
        void onPermissionGranted(int requestCode);
    }

    /**
     * 单个请求
     * @param activity
     * @param requestCode request code, e.g. if you need request CAMERA permission,parameters is PermissionUtils.CODE_CAMERA
     */
    public static void requestPermission(final Activity activity, final int requestCode, PermissionGrant permissionGrant) {
        if (activity == null) {
            return;
        }

        Log.e("permission", "requestPermission requestCode:" + requestCode);
        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            Log.e("permission", "requestPermission illegal requestCode:" + requestCode);
            return;
        }

        final String requestPermission = requestPermissions[requestCode];

        //如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
        // 但是，如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission(),会导致程序崩溃(java.lang.RuntimeException: Unknown exception code: 1 msg null)，
        // 你可以使用try{}catch(){},处理异常，也可以在这个地方，低于23就什么都不做，
        // 个人建议try{}catch(){}单独处理，提示用户开启权限。
//        if (Build.VERSION.SDK_INT < 23) {
//            return;
//        }

        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
        } catch (RuntimeException e) {
            Toast.makeText(activity, "please open this permission", Toast.LENGTH_SHORT)
                    .show();
            Log.e("permission", "RuntimeException:" + e.getMessage());
            return;
        }

        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            Log.e("permission", "ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED");


            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                Log.e("permission", "requestPermission shouldShowRequestPermissionRationale");
                shouldShowRationale(activity, requestCode, requestPermission);

            } else {
                Log.e("permission", "requestCameraPermission else");
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }

        } else {
            Log.e("permission", "ActivityCompat.checkSelfPermission ==== PackageManager.PERMISSION_GRANTED");
            Toast.makeText(activity, "opened:" + requestPermissions[requestCode], Toast.LENGTH_SHORT).show();
            permissionGrant.onPermissionGranted(requestCode);
        }
    }

    private static void requestMultiResult(Activity activity, String[] permissions, int[] grantResults, PermissionGrant permissionGrant) {
        if (activity == null) {
            return;
        }

        //TODO
        Log.e("permission", "onRequestPermissionsResult permissions length:" + permissions.length);
        Map<String, Integer> perms = new HashMap<>();

        ArrayList<String> notGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            Log.e("permission", "permissions: [i]:" + i + ", permissions[i]" + permissions[i] + ",grantResults[i]:" + grantResults[i]);
            perms.put(permissions[i], grantResults[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permissions[i]);
            }
        }

        if (notGranted.size() == 0) {
            Toast.makeText(activity, "all permission success" + notGranted, Toast.LENGTH_SHORT)
                    .show();
            permissionGrant.onPermissionGranted(CODE_MULTI_PERMISSION);
        } else {
            openSettingActivity(activity, "those permission need granted!");
        }

    }


    /**
     * 一次申请多个权限
     */
    public static void requestMultiPermissions(final Activity activity, PermissionGrant grant) {

        final List<String> permissionsList = getNoGrantedPermission(activity, false);
        final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, true);

        //TODO checkSelfPermission
        if (permissionsList == null || shouldRationalePermissionsList == null) {
            return;
        }
        Log.e("permission", "requestMultiPermissions permissionsList:" + permissionsList.size() + ",shouldRationalePermissionsList:" + shouldRationalePermissionsList.size());

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                    CODE_MULTI_PERMISSION);
            Log.e("permission", "showMessageOKCancel requestPermissions");

        } else if (shouldRationalePermissionsList.size() > 0) {
            showMessageOKCancel(activity, "should open those permission",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(new String[shouldRationalePermissionsList.size()]),
                                    CODE_MULTI_PERMISSION);
                            Log.e("permission", "showMessageOKCancel requestPermissions");
                        }
                    });
        } else {
            grant.onPermissionGranted(CODE_MULTI_PERMISSION);
        }

    }


    private static void shouldShowRationale(final Activity activity, final int requestCode, final String requestPermission) {
        //TODO
        String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
        showMessageOKCancel(activity, "Rationale: " + permissionsHint[requestCode], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{requestPermission},
                        requestCode);
                Log.e("permission", "showMessageOKCancel requestPermissions:" + requestPermission);
            }
        });
    }

    private static void showMessageOKCancel(final Activity context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();

    }

    /**
     * @param activity
     * @param requestCode  Need consistent with requestPermission
     * @param permissions
     * @param grantResults
     */
    public static void requestPermissionsResult(final Activity activity, final int requestCode, @NonNull String[] permissions,
                                                @NonNull int[] grantResults, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }
        Log.e("permission", "requestPermissionsResult requestCode:" + requestCode);

        if (requestCode == CODE_MULTI_PERMISSION) {
            requestMultiResult(activity, permissions, grantResults, permissionGrant);
            return;
        }

        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            Log.e("permission", "requestPermissionsResult illegal requestCode:" + requestCode);
            Toast.makeText(activity, "illegal requestCode:" + requestCode, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.e("permission", "onRequestPermissionsResult requestCode:" + requestCode + ",permissions:" + permissions.toString()
                + ",grantResults:" + grantResults.toString() + ",length:" + grantResults.length);

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e("permission", "onRequestPermissionsResult PERMISSION_GRANTED");
            //TODO success, do something, can use callback
            permissionGrant.onPermissionGranted(requestCode);

        } else {
            //TODO hint user this permission function
            Log.e("permission", "onRequestPermissionsResult PERMISSION NOT GRANTED");
            //TODO
            String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
            openSettingActivity(activity,  "Result" + permissionsHint[requestCode]);
        }

    }

    private static void openSettingActivity(final Activity activity, String message) {

        showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Log.e("permission", "getPackageName(): " + activity.getPackageName());
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
    }


    /**
     * @param activity
     * @param isShouldRationale true: return no granted and shouldShowRequestPermissionRationale permissions, false:return no granted and !shouldShowRequestPermissionRationale
     * @return
     */
    public static ArrayList<String> getNoGrantedPermission(Activity activity, boolean isShouldRationale) {

        ArrayList<String> permissions = new ArrayList<>();

        for (int i = 0; i < requestPermissions.length; i++) {
            String requestPermission = requestPermissions[i];


            //TODO checkSelfPermission
            int checkSelfPermission = -1;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
            } catch (RuntimeException e) {
                Toast.makeText(activity, "please open those permission", Toast.LENGTH_SHORT)
                        .show();
                Log.e("permission", "RuntimeException:" + e.getMessage());
                return null;
            }

            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                Log.e("permission", "getNoGrantedPermission ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED:" + requestPermission);

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                    Log.e("permission", "shouldShowRequestPermissionRationale if");
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }

                } else {

                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                    Log.e("permission", "shouldShowRequestPermissionRationale else");
                }

            }
        }

        return permissions;
    }
}
