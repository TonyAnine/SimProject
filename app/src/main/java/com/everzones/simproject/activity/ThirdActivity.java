package com.everzones.simproject.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.everzones.simproject.R;
import com.everzones.simproject.adapter.SimAdapter;

import java.util.ArrayList;
import java.util.List;

public class ThirdActivity extends Activity implements View.OnClickListener{
    String text;
    Button bt_change;
    Button bt_select_sim;
    List<SubscriptionInfo> list = new ArrayList<>();
    SubscriptionManager sm;
    private SubscriptionInfo info;
    private SubscriptionInfo info2;
    private String carriedName1;
    private String carriedName2;
    private int slotId;//sim卡的卡槽
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        //获取SubscriptionManager对象
        sm = SubscriptionManager.from(this);
        //SubscribInfo
        list = sm.getActiveSubscriptionInfoList();
        info = list.get(0);
        info2 = list.get(1);
        carriedName1 = info.getCarrierName().toString();
        carriedName2 = info2.getCarrierName().toString();

        bt_change = (Button) findViewById(R.id.bt_change);
        bt_select_sim = (Button) findViewById(R.id.bt_select_sim);
        bt_change.setOnClickListener(this);
        bt_select_sim.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_change:
                text = bt_change.getText().toString();
                if(text.equals("卡1")){
                    bt_change.setText("卡2");
                    bt_select_sim.setVisibility(View.GONE);
                    slotId = 1;
                    Toast.makeText(ThirdActivity.this,"转换到sim卡2 - "+carriedName2,Toast.LENGTH_SHORT).show();
                }else if(text.equals("卡2")){
                    bt_change.setText("?");
                    bt_select_sim.setVisibility(View.VISIBLE);
                    Toast.makeText(ThirdActivity.this,"总是询问",Toast.LENGTH_SHORT).show();
                }else {
                    bt_change.setText("卡1");
                    bt_select_sim.setVisibility(View.GONE);
                    slotId = 0;
                    Toast.makeText(ThirdActivity.this,"转换到sim卡1 - "+carriedName1,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_select_sim:
                selectSim();
                break;
        }
    }

    private void selectSim() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ThirdActivity.this);
        builder.setCancelable(true);//可取消
        builder.setTitle("从以下选项拨打号码给\n"+"10086");
        builder.setView(R.layout.sim_layout);
        builder.setAdapter(new SimAdapter(this, list), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ThirdActivity.this,"点击了卡"+(i+1),Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }
}
