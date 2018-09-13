package com.everzones.shortcut;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ShortcutManager mShortcutManager;
    String[] array = new String[]{"张三","李四","王五","赵六","周七"};
    MyAdapter myAdapter;
    private TextView tv_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_main = (TextView) findViewById(R.id.tv_main);

        myAdapter = new MyAdapter(this,array);
        Log.e("short",array.toString());
        setupShortcuts();

        Intent intent = getIntent();
        String msg = intent.getStringExtra("msg");
        tv_main.setText(msg);

        removeItem(0);
        updItem(1);

      /*  ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        Log.e("short","最大："+shortcutManager.getMaxShortcutCountPerActivity());//5
        ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "id1")
                .setShortLabel("Web site")
                .setLongLabel("Open the web site")
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                .setIntent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.baidu.com/")))
                .build();

        shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));*/
    }



    //动态添加快捷方式
    private void setupShortcuts() {
        mShortcutManager = getSystemService(ShortcutManager.class);
        List<ShortcutInfo> infos = new ArrayList<>();
        for (int i = 0; i < mShortcutManager.getMaxShortcutCountPerActivity(); i++) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra("msg", "我和" + myAdapter.getItem(i) + "的对话");

            ShortcutInfo info = new ShortcutInfo.Builder(this, "id" + i)
                    .setShortLabel("联系人短:" + myAdapter.getItem(i))
                    .setLongLabel("联系人长:" + myAdapter.getItem(i))
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                    .setIntent(intent)
                    .build();
            infos.add(info);
        }

         mShortcutManager.setDynamicShortcuts(infos);
    }

    private void removeItem(int index) {
        List<ShortcutInfo> infos = mShortcutManager.getPinnedShortcuts();
        for (ShortcutInfo info : infos) {
            if (info.getId().equals("id" + index)) {
                mShortcutManager.disableShortcuts(Arrays.asList(info.getId()), "暂无该联系人");
            }
        }
        mShortcutManager.removeDynamicShortcuts(Arrays.asList("id" + index));
    }

    private void updItem(int index) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("msg", "我和" + myAdapter.getItem(index) + "的对话");

        ShortcutInfo info = new ShortcutInfo.Builder(this, "id" + index)
                .setShortLabel("联系人短:"+myAdapter.getItem(index))
                .setLongLabel("联系人长:" + myAdapter.getItem(index))
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                .setIntent(intent)
                .build();

        mShortcutManager.updateShortcuts(Arrays.asList(info));
    }

    class MyAdapter extends BaseAdapter{
        private Context context;
        private String[] list;

        public MyAdapter(Context context,String[] list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public Object getItem(int i) {
            return list[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null){
                view = LayoutInflater.from(context).inflate(R.layout.item_layout,null,false);
                ViewHolder viewholder = new ViewHolder();
                viewholder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(viewholder);
            }else {
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.tv_name.setText(list[i]);
            }
            return view;
        }

        class ViewHolder{
            private TextView tv_name;
        }
    }
}
