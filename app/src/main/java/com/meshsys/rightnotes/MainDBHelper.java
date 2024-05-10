package com.meshsys.rightnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MainDBHelper extends SQLiteOpenHelper {

    public static final String INPUT_TABLE = "INPUT_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_FILE_NAME = "FILE_NAME";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_INPUT = "INPUT";

    public MainDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryString = "CREATE TABLE " + INPUT_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_FILE_NAME + " TEXT, " + COLUMN_DATE + " TEXT, " + COLUMN_INPUT + " TEXT)";
        sqLiteDatabase.execSQL(queryString);
    }
    public Boolean addItemToDB(MainValue mv){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_FILE_NAME,mv.getFileName());
        cv.put(COLUMN_DATE,mv.getDateText());
        cv.put(COLUMN_INPUT,mv.getInputText());

        long result = db.insert(INPUT_TABLE,null,cv);
        if(result == -1){
            return false;
        }else {
            return true;
        }
    }
    public ArrayList<MainValue> getAllItemsFromDb(){
        ArrayList<MainValue> retDBitem = new ArrayList<>();
        String queryString = "SELECT * FROM " + INPUT_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int InputID = cursor.getInt(0);
                String InputFileName = cursor.getString(1);
                String InputDate = cursor.getString(2);
                String InputText = cursor.getString(3);

                MainValue mv = new MainValue(InputText,InputDate,InputID,InputFileName);
                retDBitem.add(mv);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  retDBitem;
    }
    public MainValue getLastItemTryFromDb(){
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + INPUT_TABLE;
        Cursor cursor = db.rawQuery(queryString,null);
        int InputID = 0;
        String InputFileName = "",InputDate = "",InputText = "";
        if (cursor.moveToFirst()){
            cursor.moveToLast();
            InputID = cursor.getInt(0);
            InputFileName = cursor.getString(1);
            InputDate = cursor.getString(2);
            InputText = cursor.getString(3);
        }
        MainValue mv = new MainValue(InputText,InputDate,InputID,InputFileName);
        cursor.close();
        db.close();
        return mv;
    }
    public MainValue getLastItemFromDb(int _id){
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + INPUT_TABLE + " WHERE " + COLUMN_ID + " = " + _id;
        Cursor cursor = db.rawQuery(queryString,null);
        int InputID = 0;
        String InputFileName = "",InputDate = "",InputText = "";
        if (cursor.moveToFirst()){
            InputID = cursor.getInt(0);
            InputFileName = cursor.getString(1);
            InputDate = cursor.getString(2);
            InputText = cursor.getString(3);
       }
        MainValue mv = new MainValue(InputText,InputDate,InputID,InputFileName);
        cursor.close();
        db.close();
        return mv;
    }
    public MainValue getItemFromDb(int _id){
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + INPUT_TABLE + " WHERE " + COLUMN_ID + " = " + _id;
        Cursor cursor = db.rawQuery(queryString,null);
        int InputID = 0;
        String InputFileName = "",InputDate = "",InputText = "";
        if (cursor.moveToFirst()){
            InputID = cursor.getInt(0);
            InputFileName = cursor.getString(1);
            InputDate = cursor.getString(2);
            InputText = cursor.getString(3);
        }
        MainValue mv = new MainValue(InputText,InputDate,InputID,InputFileName);
        cursor.close();
        db.close();
        return mv;
    }
    public Boolean isExist(int e_id){
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + INPUT_TABLE + " WHERE " + COLUMN_ID + " = " + e_id;
        Cursor cursor = db.rawQuery(queryString,null);

        if (cursor.moveToFirst()){
            cursor.close();
            db.close();
            return true;
        }else {
            cursor.close();
            db.close();
            return false;
        }
    }
    public Boolean delete(int _id){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + INPUT_TABLE + " WHERE " + COLUMN_ID + " = " + _id;

        Cursor cursor = db.rawQuery(queryString,null);
        if (cursor.moveToFirst()){
            cursor.close();
            return true;
        }else {
            cursor.close();
         return false;
        }
    }
    public int updateItemToDb(MainValue mv){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INPUT,mv.getInputText());
        cv.put(COLUMN_DATE,mv.getDateText());
        cv.put(COLUMN_FILE_NAME,mv.getFileName());
        int result = db.update(INPUT_TABLE,cv,COLUMN_ID + "=" + mv.getId(),null);
        return result;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop Table if exists " + INPUT_TABLE);
    }
}
