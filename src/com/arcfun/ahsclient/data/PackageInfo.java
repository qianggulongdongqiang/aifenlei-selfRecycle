package com.arcfun.ahsclient.data;

import com.arcfun.ahsclient.utils.Utils;

import android.os.Parcel;
import android.os.Parcelable;

public class PackageInfo implements Parcelable {
    /** id */
    private int id;
    /** name */
    private String name;
    /** unit */
    private float unit;
    /** weight */
    private float weight;
    /** unit_name */
    private String uName;
    private int total;

    public PackageInfo(int _id, String _name, String _uname, float _unit, float _weight) {
        this.id = _id;
        this.name = _name;
        this.uName = _uname;
        this.unit = _unit;
        this.weight = Utils.formatFloat(_weight);
        this.total = Utils.formatInt(_unit * _weight);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
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

    public float getUnit() {
        return unit;
    }

    public void setUnit(float unit) {
        this.unit = unit;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
        this.total = Utils.formatInt(unit * weight);
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public static final Parcelable.Creator<PackageInfo> CREATOR = new Creator<PackageInfo>() {
        @Override
        public PackageInfo createFromParcel(Parcel source) {
            return new PackageInfo(source);
        }

        @Override
        public PackageInfo[] newArray(int size) {
            return new PackageInfo[size];
        }
    };

    protected PackageInfo(Parcel source) {
        id = source.readInt();
        name = source.readString();
        unit = source.readFloat();
        weight = source.readFloat();
        total = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeFloat(unit);
        dest.writeFloat(weight);
        dest.writeInt(total);
    }

    @Override
    public String toString() {
        return "[name=" + name + ", unit=" + unit + ", weight=" + weight
                + ",  total=" + total + "]";
    }
}