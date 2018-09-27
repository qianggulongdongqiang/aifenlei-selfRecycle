package com.arcfun.ahsclient.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ScoreInfo implements Parcelable {
    /** code */
    private int code;
    /** msg */
    private String msg;
    /** id */
    private int id;
    /** name */
    private String name;
    /** mobile */
    private String mobile;
    /** score */
    private int score;

    public ScoreInfo(int _code, String _msg) {
        code = _code;
        msg = _msg;
    }

    public ScoreInfo(int _code, String _msg, int _id, String _name,
            String _mobile, int _socre) {
        code = _code;
        msg = _msg;
        id = _id;
        name = _name;
        mobile = _mobile;
        score = _socre;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "[id=" + id + ",name=" + name + ",mobile=" + mobile + ",score="
                + score + "]";
    }

    public static final Parcelable.Creator<ScoreInfo> CREATOR = new Creator<ScoreInfo>() {
        @Override
        public ScoreInfo createFromParcel(Parcel source) {
            return new ScoreInfo(source);
        }

        @Override
        public ScoreInfo[] newArray(int size) {
            return new ScoreInfo[size];
        }
    };

    protected ScoreInfo(Parcel source) {
        id = source.readInt();
        name = source.readString();
        mobile = source.readString();
        score = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(mobile);
        dest.writeInt(score);
    }
}