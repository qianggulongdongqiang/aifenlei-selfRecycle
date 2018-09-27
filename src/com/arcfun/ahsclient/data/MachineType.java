package com.arcfun.ahsclient.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MachineType implements Parcelable {
    /** id */
    private int id;
    /** name */
    private String name;
    /** unit_name */
    private String uName;
    /** unit */
    private String unit;
    /** is_qrcode */
    private int isQrcode;
    /** purchasing_point */
    private float point;

    public MachineType(int _id, String _name, String _uName, String _unit,
            String _point) {
        this.id = _id;
        this.name = _name;
        this.uName = _uName;
        this.unit = _unit;
        this.point = Float.valueOf(_point);
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

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getIsQrcode() {
        return isQrcode;
    }

    public void setIsQrcode(int isQrcode) {
        this.isQrcode = isQrcode;
    }

    public float getPoint() {
        return point;
    }

    public void setPoint(float point) {
        this.point = point;
    }

    public static final Parcelable.Creator<MachineType> CREATOR = new Creator<MachineType>() {
        @Override
        public MachineType createFromParcel(Parcel source) {
            return new MachineType(source);
        }

        @Override
        public MachineType[] newArray(int size) {
            return new MachineType[size];
        }
    };

    protected MachineType(Parcel source) {
        id = source.readInt();
        name = source.readString();
        uName = source.readString();
        unit = source.readString();
        point = source.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(uName);
        dest.writeString(unit);
        dest.writeFloat(point);
    }

    @Override
    public String toString() {
        return "[id=" + id + ", name=" + name + ", uName=" + uName + ", unit"
                + unit + ", point=" + point + "]";
    }
}