package com.meshsys.rightnotes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainValue implements Parcelable {
    public String inputText,dateText,IdStr,fileName;
    public int id;

    public MainValue(String dateText, String fileName) {
        this.dateText = dateText;
        this.fileName = fileName;
    }

    public int step;
    public Boolean isChecked = false;

    protected MainValue(Parcel in) {
        inputText = in.readString();
        dateText = in.readString();
        IdStr = in.readString();
        fileName = in.readString();
        id = in.readInt();
        step = in.readInt();
        byte tmpIsChecked = in.readByte();
        isChecked = tmpIsChecked == 0 ? null : tmpIsChecked == 1;
    }

    public static final Creator<MainValue> CREATOR = new Creator<MainValue>() {
        @Override
        public MainValue createFromParcel(Parcel in) {
            return new MainValue(in);
        }

        @Override
        public MainValue[] newArray(int size) {
            return new MainValue[size];
        }
    };

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getIdStr() {
        return Integer.toString(id);
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MainValue(String inputText, String dateText, @Nullable Integer id,@Nullable String fileName) {
        this.inputText = inputText;
        this.dateText = dateText;
        if(id != null){
        this.id = id;}
        if(fileName != null){
            this.fileName = fileName;
        }
    }

    @Override
    public String toString() {
        return "MainValue{" +
                "inputText='" + inputText + '\'' +
                ", dateText='" + dateText + '\'' +
                ", IdStr='" + IdStr + '\'' +
                ", id=" + id +
                ", step=" + step +
                ", isChecked=" + isChecked +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(inputText);
        dest.writeString(dateText);
        dest.writeString(IdStr);
        dest.writeString(fileName);
        dest.writeInt(id);
        dest.writeInt(step);
        dest.writeByte((byte) (isChecked == null ? 0 : isChecked ? 1 : 2));
    }
}
