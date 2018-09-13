package com.everzones.simproject.domain;

/**
 * Created by sunny on 2017/8/14.
 * anthor:sunny
 * date: 2017/8/14
 * function:通话记录实体类
 */

public class CallInfo {
    public String number; // 号码
    public long date;     // 日期
    public int type;      // 类型：来电、去电、未接
    public String simid;     //sim卡的id

    public CallInfo(String number, long date, int type) {
        this.number = number;
        this.date = date;
        this.type = type;
    }

    public CallInfo(String number, long date, int type, String simid) {
        this.number = number;
        this.date = date;
        this.type = type;
        this.simid = simid;
    }

    @Override
    public String toString() {
        return "CallInfo{" +
                "number='" + number + '\'' +
                ", date=" + date +
                ", type=" + type +
                ", simid=" + simid +
                '}';
    }
}
