package com.arcfun.ahsclient.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductAndOwnerInfo implements Parcelable {
    /** credit */
    private int credit;
    /** user_nickname */
    private OwnerInfo ownerInfo;

    public ProductAndOwnerInfo(int _credit, OwnerInfo _info) {
        this.credit = _credit;
        this.ownerInfo = _info;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public OwnerInfo getOwnerInfo() {
        return ownerInfo;
    }

    public void setOwnerInfo(OwnerInfo ownerInfo) {
        this.ownerInfo = ownerInfo;
    }

    public static final Parcelable.Creator<ProductAndOwnerInfo> CREATOR = new Creator<ProductAndOwnerInfo>() {
        @Override
        public ProductAndOwnerInfo createFromParcel(Parcel source) {
            return new ProductAndOwnerInfo(source);
        }

        @Override
        public ProductAndOwnerInfo[] newArray(int size) {
            return new ProductAndOwnerInfo[size];
        }
    };

    protected ProductAndOwnerInfo(Parcel source) {
        credit = source.readInt();
        ownerInfo = source.readParcelable(OwnerInfo.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(credit);
        dest.writeParcelable(ownerInfo, flags);
    }

    @Override
    public String toString() {
        return "[credit=" + credit + ", owner=" + ownerInfo.toString() + "]";
    }
}