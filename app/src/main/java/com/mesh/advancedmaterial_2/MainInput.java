package com.mesh.advancedmaterial_2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainInput extends AppCompatActivity {
    private MaterialToolbar InputToolbar;
    private TextInputEditText inputEditText;
    private NestedScrollView mainscroll;
    private MainLog log = new MainLog();
    private MainRecAdapter adapter = new MainRecAdapter();
    private MainDBHelper dbHelper = new MainDBHelper(MainInput.this,"notes.db",null,1);
    private Boolean isUpdatemode = false,isExisting = false;
    private int _id = 0,_exists_id = 0;
    private int pMode;
    private Intent intent_tmp = new Intent();
    private SimpleDateFormat sdf = new SimpleDateFormat("h:mm a\nd MMM yy", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_layout);
        isExisting = false;
        InputToolbar = findViewById(R.id.inputToolbar);
        inputEditText = findViewById(R.id.MainInputedt);
        Intent intent = getIntent();
        pMode = intent.getIntExtra("mode",0);
        _id = intent.getIntExtra("dbId",0);
        if(pMode == 1){
            MainValue mv = dbHelper.getItemFromDb(_id);
            if(!mv.getInputText().isEmpty()) {
                inputEditText.setText(mv.getInputText());
                isUpdatemode = true;
                _exists_id = _id;
            }
        }
        showSoftKeyboard(inputEditText);
        InputToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String savedState = savedInstanceState.getString("saved_edt_state");
        inputEditText.setText(savedState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String toSave;
        if(!inputEditText.getText().toString().isEmpty()){
            toSave = inputEditText.getText().toString();
        }
        else {
            toSave = "";
        }
        outState.putString("saved_edt_state",toSave);
    }

    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        String date = sdf.format(new Date());
        MainValue mainValue = new MainValue(date,"Text");
        @Nullable String str;

        if(isUpdatemode){
            if(!inputEditText.getText().toString().isEmpty()){
                str = inputEditText.getText().toString();
                mainValue.setId(_exists_id);
                mainValue.setInputText(str);
                dbHelper.updateItemToDb(mainValue);
                intent_tmp.putExtra("tyid",_exists_id);
            }
        }
        else{
            Boolean result = false;
            if(!inputEditText.getText().toString().isEmpty()){
                str = inputEditText.getText().toString();
                mainValue.setInputText(str);
                result = dbHelper.addItemToDB(mainValue);
                if (pMode == 1){
                    _exists_id = _id;
                }else {
                    MainValue mv = dbHelper.getLastItemTryFromDb();
                    _exists_id = mv.getId();
                }
                isUpdatemode = true;
            }else {
                str = null;
            }
            intent_tmp.putExtra("mainText",str);
            if(result){
                Log.d(MainLog.DEBUG_TAG + " MainInput", "onBackPressed: notes added to database sucessfully");
            }else{
                Log.d(MainLog.DEBUG_TAG + " MainInput", "onBackPressed: error adding notes to database");
            }
        }
    }

    @Override
    public void onBackPressed() {
        onStop();
        setResult(Activity.RESULT_OK,intent_tmp);
        super.onBackPressed();
    }
}