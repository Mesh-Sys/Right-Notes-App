package com.meshsys.rightnotes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.SupportActionModeWrapper;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    private RecyclerView recyclerView;
    private ArrayList<MainValue> miniAdapter = new ArrayList<>();
    public FloatingActionButton addNote,deletenote,cancelfab;
    private GridLayoutManager grdLmgr = new GridLayoutManager(MainActivity.this,3);
    private StaggeredGridLayoutManager stdgrdlmgr = new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL);
    private LinearLayoutManager linlay = new LinearLayoutManager(MainActivity.this);
    private MainRecAdapter adapter = new MainRecAdapter(MainActivity.this);
    private int inc = 0;
    public int numSelected = 0;
    private Boolean tv = false;
    private ActionMode actionMode;
    private MaterialToolbar toolbar;
    private String date;
    private MainLog log;
    private int tPos = 0;
    private Parcelable savedLayoutstate;
    private MainDBHelper dbHelper = new MainDBHelper(MainActivity.this,"notes.db",null,1);
    public static final String ARRAY_STATE = "ARRAY_STATE";
    public static final String LAYOUT_STATE = "LAYOUT_STATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.mainrec);
        if(savedInstanceState != null){
            miniAdapter = savedInstanceState.getParcelableArrayList(ARRAY_STATE);
            savedLayoutstate = savedInstanceState.getParcelable(LAYOUT_STATE);
            displayItem();
        }else{
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    reloadDB(null,0);
                }
            };
            new Thread(runnable).start();

        }

        Objects.requireNonNull(getSupportActionBar()).setTitle("Material Notes");
        addNote = findViewById(R.id.addNotefabbtn);
        deletenote = findViewById(R.id.deleteNotefabbtn);
        cancelfab = findViewById(R.id.cancelfabbtn);
        deletenote.hide();
        cancelfab.hide();
        Intent intent = new Intent(MainActivity.this,MainInput.class);
        ActivityResultLauncher<Intent> InputPageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            //createItem(result.getData(),date);
                            reloadDB(result.getData(),1);
                        }
                    }
                });
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //intent.putExtra("mode",0);
                InputPageLauncher.launch(intent);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARRAY_STATE,miniAdapter);
        if (recyclerView.getLayoutManager() != null) {
            outState.putParcelable(LAYOUT_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        miniAdapter = savedInstanceState.getParcelableArrayList(ARRAY_STATE);
        savedLayoutstate = savedInstanceState.getParcelable(LAYOUT_STATE);
        super.onRestoreInstanceState(savedInstanceState);
    }

    public static MainActivity getInstance(){
        return instance;
    }
    public void showToast(@Nullable Integer size, String name){
        if(size != null) {
            Toast.makeText(MainActivity.this, name + " " + size, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
        }
    }
    public void createItem(Intent data,String date){
        String mainText = data.getStringExtra("mainText");
        if(mainText != null){
            miniAdapter.add(new MainValue(mainText,date,null,null));
            adapter.addItem(miniAdapter);
            linlay.scrollToPosition(adapter.getItemCount() - 1);
            grdLmgr.scrollToPosition(adapter.getItemCount() - 1);
            stdgrdlmgr.scrollToPosition(adapter.getItemCount() - 1);
            stdgrdlmgr.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            inc++;

            linlay.setReverseLayout(true);
            linlay.setStackFromEnd(true);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linlay);
        }
    }
    public void displayItem(){
        //Collections.reverse(miniAdapter);
        adapter.addItem(miniAdapter);
        linlay.scrollToPosition(adapter.getItemCount() - 1);
        linlay.setReverseLayout(true);
        linlay.setStackFromEnd(true);
        stdgrdlmgr.scrollToPosition(adapter.getItemCount() - 1);
        stdgrdlmgr.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linlay);
        if(savedLayoutstate != null){
            recyclerView.getLayoutManager().onRestoreInstanceState(savedLayoutstate);
        }
    }
    private ActivityResultLauncher<Intent>LaunchEditor = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult()
            , new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        //createItem(result.getData(),date);
                        reloadDB(result.getData(),2);
                    }
                }
            }
    );
    public void updateItem(int pos){
        savedLayoutstate = recyclerView.getLayoutManager().onSaveInstanceState();
        tPos = pos;
        Intent intent = new Intent(MainActivity.this,MainInput.class);
        intent.putExtra("mode",1);
        intent.putExtra("dbId",adapter.cardPrinter.get(pos).getId());
        LaunchEditor.launch(intent);
    }
    public void deleteItem(int _id){
        dbHelper.delete(_id);
    }
    public void reloadDB(@Nullable Intent data, int mode){
        Parcelable tfy =  savedLayoutstate;
        int ty = 0;
        /*
          mode 0: Load all Notes From Database (When App Starts)
          mode 1: Load After Creating a New Note
          mode 2: Load After Editing Note
          "@deprecated" mode 3: Load After Delete
        */

        if(mode == 0){
            miniAdapter = dbHelper.getAllItemsFromDb();
            //Collections.reverse(miniAdapter);
            adapter.addItem(miniAdapter);
            linlay.scrollToPosition(adapter.getItemCount() - 1);
            grdLmgr.scrollToPosition(adapter.getItemCount() - 1);
            //stdgrdlmgr.scrollToPosition(adapter.getItemCount() - 1);
        }else if(mode == 1){
            if(data != null){
                if(data.getStringExtra("mainText") != null){
                    MainValue mv = dbHelper.getLastItemTryFromDb();
                    miniAdapter.add(new MainValue(mv.getInputText(), mv.getDateText(),mv.getId(), mv.getFileName()));
                    //Collections.reverse(miniAdapter);
                    adapter.addItem(miniAdapter);
                    adapter.notifyItemInserted(adapter.currentPosition());
                    linlay.scrollToPosition(adapter.getItemCount() - 1);
                    grdLmgr.scrollToPosition(adapter.getItemCount() - 1);
                    stdgrdlmgr.scrollToPosition(adapter.getItemCount() - 1);
                }
            }
        }else if(mode == 2){
            if(data != null){
                int _id_1 = data.getIntExtra("tyid",0);
                MainValue mv = dbHelper.getItemFromDb(_id_1);
                adapter.cardPrinter.get(tPos).setInputText(mv.getInputText());
                adapter.cardPrinter.get(tPos).setDateText(mv.getDateText());
                adapter.cardPrinter.get(tPos).setFileName(mv.getFileName());
                if(savedLayoutstate != null){
                    recyclerView.getLayoutManager().onRestoreInstanceState(savedLayoutstate);
                }
//                miniAdapter.add(new MainValue(mv.getInputText(), mv.getDateText(),mv.getId(), mv.getFileName()));
//                adapter.addItem(miniAdapter);
//                adapter.notifyItemInserted(adapter.currentPosition());
//                linlay.scrollToPosition(adapter.getItemCount() - 1);
//                grdLmgr.scrollToPosition(adapter.getItemCount() - 1);
            }
        }
        linlay.setReverseLayout(true);
        linlay.setStackFromEnd(true);
        grdLmgr.setReverseLayout(true);


        stdgrdlmgr.scrollToPosition(adapter.getItemCount() - 1);
        stdgrdlmgr.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);



        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linlay);
        stdgrdlmgr.invalidateSpanAssignments();
    }
    private ActionMode.Callback MainActionMode = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contexual_menu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.unCheckItem();
        }
    };
    public void startMulti(){
        actionMode = startSupportActionMode(MainActionMode);
        if(actionMode != null){
            actionMode.setTitle("1 selected");
        }
    }
    public void addMulti(int num){
        if(actionMode != null){
            actionMode.setTitle(num + " selected");
        }
    }
}