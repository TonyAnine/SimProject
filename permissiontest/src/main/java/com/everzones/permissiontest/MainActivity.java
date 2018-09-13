package com.everzones.permissiontest;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int CODE_MULTI_PERMISSION = 2;
    private static final int REQUESTALL = 3;
    private static final int REQUEST_WRITE_CONTACT_PERMISSIONS = 4 ;
    private static final int REQUEST_ALL = 99;
    private Button bt_getContact;
    private Button bt_call;
    private Button bt_callLog;
    private Button bt_all;
    private Button bt_addContact;
    // 声明一个数组，用来存储所有需要动态申请的权限
    String[] permissions = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS};
    // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权限
    List<String> mPermissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_getContact = (Button) findViewById(R.id.bt_getContact);
        bt_call = (Button) findViewById(R.id.bt_call);
        bt_callLog = (Button) findViewById(R.id.bt_callLog);
        bt_all = (Button) findViewById(R.id.bt_all);
        bt_addContact = (Button) findViewById(R.id.bt_addContact);

        bt_getContact.setOnClickListener(this);
        bt_call.setOnClickListener(this);
        bt_callLog.setOnClickListener(this);
        bt_all.setOnClickListener(this);
        bt_addContact.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_getContact:
                checkContact();
                break;
            case R.id.bt_callLog:
                requestAllPermission();
                break;
            case R.id.bt_all:
                PermissionUtil.requestMultiPermissions(MainActivity.this,mPermissionGrant);
                break;
            case R.id.bt_addContact:
                int hasWriteContactsPermission = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_CONTACTS);
                if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_CONTACTS)){

                    }
                    ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.WRITE_CONTACTS}, REQUEST_WRITE_CONTACT_PERMISSIONS);
                }else {
                    Toast.makeText(MainActivity.this,"已经授权成功",Toast.LENGTH_SHORT).show();
                    addContact();
                }
                break;
        }
    }

    //请求所有的权限
    private void requestAllPermission() {
        for (int i = 0;i < permissions.length;i++){
            if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissions[i])) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{permissions[i]}, REQUEST_ALL);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{permissions[i]}, REQUEST_ALL);
                }
            }
        }
    }

    //添加联系人
    private void addContact() {
        // Two operations are needed to insert a new contact.
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(2);

        // First, set up a new raw contact.
        ContentProviderOperation.Builder op =
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
        operations.add(op.build());

        // Next, set the name for the contact.
        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        "__DUMMY CONTACT from runtime permissions sample");
        operations.add(op.build());

        // Apply the operations.
        ContentResolver resolver = getContentResolver();
        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (RemoteException e) {
            Log.d("empty", "Could not add a new contact: " + e.getMessage());
        } catch (OperationApplicationException e) {
            Log.d("empty", "Could not add a new contact: " + e.getMessage());
        }
    }

    //判断获取联系人的权限
    private void checkContact() {
        //检查是否具有该权限（有：方法将返回 PackageManager.PERMISSION_GRANTED；没有：方法将返回 PERMISSION_DENIED）
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS);
        //没有该权限时
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_CONTACTS)){
                Toast.makeText(MainActivity.this,"解释：需要读取联系人",Toast.LENGTH_SHORT).show();
            }else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.
                        READ_CONTACTS},MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                Toast.makeText(MainActivity.this,"去请求授权",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(MainActivity.this,"已经授权成功了",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this,"授权成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this,"授权失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_WRITE_CONTACT_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addContact();
                } else {
                    Toast.makeText(MainActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUESTALL:
                PermissionUtil.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
                break;
            case REQUEST_ALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,permissions+"授权成功",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,permissions+"授权失败",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private PermissionUtil.PermissionGrant mPermissionGrant = new PermissionUtil.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtil.CODE_READ_PHONE_STATE:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_RECORD_AUDIO", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_READ_CALLLOG:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_GET_ACCOUNTS", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_CALL_PHONE:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_CALL_PHONE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_GET_CONTACT:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_CAMERA", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
