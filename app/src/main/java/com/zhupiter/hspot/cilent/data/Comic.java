package com.zhupiter.hspot.cilent.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhupiter on 17-1-26.
 */

public class Comic implements Parcelable{

    private int cid;
    private String cTitle, cAuthor, cLanguage, cIntro, tag;
    private long updateTime;
    private int chapterNum;


    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cid);
        dest.writeString(cTitle);
        dest.writeString(cAuthor);
        dest.writeString(cLanguage);
        dest.writeString(cIntro);
        dest.writeLong(updateTime);
        dest.writeInt(chapterNum);
        dest.writeString(tag);
    }

    public static final Parcelable.Creator<Comic> CREATOR = new Creator<Comic>() {
        @Override
        public Comic createFromParcel(Parcel source) {
            return new Comic(source);
        }

        @Override
        public Comic[] newArray(int size) {
            return new Comic[size];
        }
    };

    public Comic(){

    }

    public Comic(Parcel in){
        cid=in.readInt();
        cTitle=in.readString();
        cAuthor=in.readString();
        cLanguage=in.readString();
        cIntro=in.readString();
        updateTime=in.readLong();
        chapterNum=in.readInt();
        tag=in.readString();
    }

}
