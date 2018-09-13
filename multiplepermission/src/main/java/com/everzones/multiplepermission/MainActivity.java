package com.everzones.multiplepermission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ActivityCompat.OnRequestPermissionsResultCallback{
    private static final int ALL_PERS = 1;
    private Button bt_multiple;
    String pers[] = {Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_CONTACTS};

    private PermissionTool.PermissionGrant mPermissionGrant = new PermissionTool.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionTool.CODE_READ_PHONE_STATE:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionTool.CODE_READ_CONTACT:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_READ_CONTACT", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionTool.CODE_READ_CALLLOG:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_READ_CALLLOG", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionTool.CODE_CALL_PHONE:
                    Toast.makeText(MainActivity.this, "Result Permission Grant CODE_CALL_PHONE", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        bt_multiple = (Button) findViewById(R.id.bt_multiple);
        bt_multiple.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_multiple:
                PermissionTool.requestMultiPermissions(MainActivity.this,mPermissionGrant);
                //requestAllPers();
                //PermissionUtil.requestMultiPermissions(MainActivity.this);
                break;
        }
    }

    private void requestAllPers() {
        for (int i=0;i<pers.length;i++){
            int permission = ContextCompat.checkSelfPermission(MainActivity.this,pers[i]);
            if (permission != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        pers[i])){
                    ActivityCompat.requestPermissions(MainActivity.this,pers,ALL_PERS);
                }else {
                    ActivityCompat.requestPermissions(MainActivity.this,pers,ALL_PERS);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionTool.requestMultiPermissions(MainActivity.this,mPermissionGrant);
        //PermissionUtil.requestMultiPermissions(MainActivity.this);
    }

    /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.requestPermissionsResult(this, requestCode, permissions, grantResults/*, mPermissionGrant));
       switch (requestCode){
            case ALL_PERS:
                for (int i =0;i<permissions.length;i++){
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "授权成功！", 0).show();
                    } else {
                        Toast.makeText(MainActivity.this, "授权失败！", 0).show();
                    }
                }
                break;
        }
    }*/

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionTool.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }
}
