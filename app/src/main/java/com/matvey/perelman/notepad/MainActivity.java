package com.matvey.perelman.notepad;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.matvey.perelman.notepad.data.DataManager;
import com.matvey.perelman.notepad.data.Folder;
import com.matvey.perelman.notepad.data.Note;
import com.matvey.perelman.notepad.data.Visual;

public class MainActivity extends AppCompatActivity {
    private MyRecyclerViewAdapter adapter;

    private DataManager dataManager;

    public static final int FILE_MAKER_MODE = 0;
    public static final int CUT_MODE = 1;
    public static final int COPY_MODE = 2;


    public int mode;

    private Visual buffer;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mode = FILE_MAKER_MODE;

        dataManager = new DataManager(this);

        //RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new MyRecyclerViewAdapter(this, dataManager.getFileNames());
        recyclerView.setAdapter(adapter);
        //

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(mode == CUT_MODE || mode == COPY_MODE){
                    mode = FILE_MAKER_MODE;
                }
                if(mode == FILE_MAKER_MODE){
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setTitle("Make file");
                    dialog.setContentView(R.layout.file_maker_view);
                    final EditText et = dialog.findViewById(R.id.name_et);
                    Button btn = dialog.findViewById(R.id.create_btn);

                    if(adapter.getFolder().parent == null){
                        Switch sw = dialog.findViewById(R.id.is_folder);
                        sw.setEnabled(false);
                    }
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String s = et.getText().toString();
                            if(s.isEmpty()){
                                et.setError("Enter the name");
                                return;
                            }
                            if(adapter.getFolder().parent == null){
                                Folder f = new Folder();
                                s += getString(R.string.app_file_type);
                                f.header = s;
                                dataManager.createFile(f);
                            }else {
                                Visual visual;
                                Switch sw = dialog.findViewById(R.id.is_folder);
                                if (sw.isChecked())
                                    visual = new Folder();
                                else
                                    visual = new Note();
                                visual.header = s;
                                adapter.getFolder().add(visual);
                            }
                            adapter.update(adapter.getFolder());
                            dialog.hide();
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(adapter.getFolder().parent != null)
            dataManager.saveFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }
    public void setBuffer(Visual v){
        if(buffer == null){
            MenuItem item = menu.findItem(R.id.paste_itm);
            item.setVisible(true);
        }
        buffer = v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.cut_itm:
                mode = CUT_MODE;
                break;
            case R.id.copy_itm:
                mode = COPY_MODE;
                break;
            case R.id.paste_itm:
                if(adapter.getFolder().parent == null){
                    if(buffer instanceof Note){
                        Toast.makeText(MainActivity.this, "Note can't be file", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Folder f = (Folder) buffer.getCopy();
                    if(buffer.parent.parent != null)
                        f.header += getString(R.string.app_file_type);

                    dataManager.createFile(f);
                    adapter.update(adapter.getFolder());
                }else {
                    Visual v = buffer.getCopy();
                    if(buffer.parent.parent == null){
                        v.header = v.header.substring(0,
                                v.header.length() - getString(R.string.app_file_type).length());
                    }
                    adapter.getFolder().add(v);
                    adapter.update(adapter.getFolder());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (mode){
            case CUT_MODE:
                mode = FILE_MAKER_MODE;
                return;
        }
        if(adapter.getFolder().parent == null){
            super.onBackPressed();
        }else {
            adapter.update(adapter.getFolder().parent);
            if(adapter.getFolder().parent == null){
                dataManager.saveFile();
                adapter.getFolder().visuals.remove(dataManager.getFile());
            }
        }
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
