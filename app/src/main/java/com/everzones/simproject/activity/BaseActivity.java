package com.everzones.simproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.everzones.simproject.R;

public class BaseActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Button bt_simInfo = (Button) findViewById(R.id.bt_simInfo);
        Button bt_callLog = (Button) findViewById(R.id.bt_callLog);
        Button bt_simChange = (Button) findViewById(R.id.bt_simChange);
        bt_callLog.setOnClickListener(this);
        bt_simInfo.setOnClickListener(this);
        bt_simChange.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_simInfo:
                Intent intent1 = new Intent(BaseActivity.this,MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.bt_callLog:
                Intent intent2 = new Intent(BaseActivity.this,CallLogActivity.class);
                startActivity(intent2);
                break;
            case R.id.bt_simChange:
                Intent intent3 = new Intent(BaseActivity.this,ThirdActivity.class);
                startActivity(intent3);
                break;
        }
    }
}
