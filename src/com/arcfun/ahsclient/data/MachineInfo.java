package com.arcfun.ahsclient.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MachineInfo implements Parcelable {
    /** code */
    private String code;
    /** signature */
    private String signature;
    /** user_type */
    private int userType;
    /** machine_type_1 */
    private MachineType type1;
    /** machine_type_2 */
    private MachineType type2;

    public MachineInfo(String _code, String _signature, int _userType,
            MachineType _type1, MachineType _type2) {
        this.code = _code;
        this.signature = _signature;
        this.userType = _userType;
        this.type1 = _type1;
        this.type2 = _type2;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public static final Parcelable.Creator<MachineInfo> CREATOR = new Creator<MachineInfo>() {
        @Override
        public MachineInfo createFromParcel(Parcel source) {
            return new MachineInfo(source);
        }

        @Override
        public MachineInfo[] newArray(int size) {
            return new MachineInfo[size];
        }
    };

    protected MachineInfo(Parcel source) {
        code = source.readString();
        signature = source.readString();
        userType = source.readInt();
        type1 = source.readParcelable(MachineType.class.getClassLoader());
        type2 = source.readParcelable(MachineType.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(signature);
        dest.writeInt(userType);
        dest.writeParcelable(type1, flags);
        dest.writeParcelable(type2, flags);
    }

    @Override
    public String toString() {
        return "[code=" + code + ", signature=" + signature + ", userType="
                + userType + "]";
    }
}