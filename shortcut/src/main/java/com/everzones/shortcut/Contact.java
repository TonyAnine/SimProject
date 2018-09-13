package com.everzones.shortcut;

import android.graphics.Bitmap;

/**
 * Created by sunny on 2017/8/21.
 * anthor:sunny
 * date: 2017/8/21
 * function:联系人
 */

class Contact {
    private Bitmap icon;
    private String name;

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
