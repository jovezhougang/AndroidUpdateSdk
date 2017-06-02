package com.mytian.mtupdatesdk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class UpdateBean implements Parcelable {
    String title;
    String message;
    String downloadUrl;
    boolean isForceUpdate;
    HashMap<String, String> headers;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isForceUpdate() {
        return isForceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        isForceUpdate = forceUpdate;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.message);
        dest.writeString(this.downloadUrl);
        dest.writeByte(this.isForceUpdate ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.headers);
    }

    public UpdateBean() {
    }

    protected UpdateBean(Parcel in) {
        this.title = in.readString();
        this.message = in.readString();
        this.downloadUrl = in.readString();
        this.isForceUpdate = in.readByte() != 0;
        this.headers = (HashMap<String, String>) in.readSerializable();
    }

    public static final Creator<UpdateBean> CREATOR = new Creator<UpdateBean>() {
        @Override
        public UpdateBean createFromParcel(Parcel source) {
            return new UpdateBean(source);
        }

        @Override
        public UpdateBean[] newArray(int size) {
            return new UpdateBean[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if ((obj instanceof UpdateBean
                && downloadUrl.equalsIgnoreCase(((UpdateBean) obj).downloadUrl))) {
            return true;
        }
        return super.equals(obj);
    }
}
