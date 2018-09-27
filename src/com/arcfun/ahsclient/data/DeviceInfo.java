package com.arcfun.ahsclient.data;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceInfo implements Parcelable {
    /** code */
    private int code;
    /** msg */
    private String msg;
    /** id */
    private int id;
    /** user_nickname */
    private String nickname;
    /** signature */
    private String signature;
    /** token */
    private String token;
    /** token */
    private MachineType type1;
    /** token */
    private MachineType type2;

    public DeviceInfo(int _code, String _msg) {
        this.code = _code;
        this.msg = _msg;
    }

    public DeviceInfo(int _code, String _msg, int _id, String _name,
            String _signature, String _token, MachineType _type1,
            MachineType _type2) {
        this.code = _code;
        this.msg = _msg;
        this.id = _id;
        this.nickname = _name;
        this.signature = _signature;
        this.token = _token;
        this.type1 = _type1;
        this.type2 = _type2;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getToken() {
        return token;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MachineType getType1() {
        return type1;
    }

    public void setType1(MachineType type1) {
        this.type1 = type1;
    }

    public MachineType getType2() {
        return type2;
    }

    public void setType2(MachineType type2) {
        this.type2 = type2;
    }

    public static final Parcelable.Creator<DeviceInfo> CREATOR = new Creator<DeviceInfo>() {
        @Override
        public DeviceInfo createFromParcel(Parcel source) {
            return new DeviceInfo(source);
        }

        @Override
        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }
    };

    protected DeviceInfo(Parcel source) {
        code = source.readInt();
        msg = source.readString();
        id = source.readInt();
        nickname = source.readString();
        signature = source.readString();
        token = source.readString();
        type1 = source.readParcelable(MachineType.class.getClassLoader());
        type2 = source.readParcelable(MachineType.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(msg);
        dest.writeInt(id);
        dest.writeString(nickname);
        dest.writeString(signature);
        dest.writeString(token);
        dest.writeParcelable(type1, flags);
        dest.writeParcelable(type2, flags);
    }

    @Override
    public String toString() {
        return "[code=" + code + ", name=" + nickname + ", token=" + token
                + "]";
    }
}