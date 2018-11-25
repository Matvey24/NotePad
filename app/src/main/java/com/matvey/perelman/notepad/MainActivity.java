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
import com.matvey.perelman.notepad.data.Visual;

import static com.matvey.perelman.notepad.Model.COPY_MODE;
import static com.matvey.perelman.notepad.Model.CUT_MODE;
import static com.matvey.perelman.notepad.Model.FILE_MAKER_MODE;

public class MainActivity extends AppCompatActivity {
    private MyRecyclerViewAdapter adapter;

    private DataManager dataManager;
    private Model model;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        model = new Model(this);
        dataManager = new DataManager(model, getString(R.string.app_file_type));

        RecyclerView recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new MyRecyclerViewAdapter(model);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                switch (model.mode) {
                    case CUT_MODE:
                    case COPY_MODE:
                        model.mode = FILE_MAKER_MODE;
                    case FILE_MAKER_MODE:
                        showCreaterDialog();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        model.save();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    public void onCreateBuffer() {
        MenuItem item = menu.findItem(R.id.paste_itm);
        item.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.cut_itm:
                model.mode = CUT_MODE;
                break;
            case R.id.copy_itm:
                model.mode = COPY_MODE;
                break;
            case R.id.paste_itm:
                paste();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (model.mode) {
            case CUT_MODE:
            case COPY_MODE:
                model.mode = FILE_MAKER_MODE;
                break;
            case FILE_MAKER_MODE:
                if (model.isVisibleFolderRoot()) {
                    super.onBackPressed();
                } else {
                    if (model.isVisibleFolderFile()) {
                        model.save();
                        dataManager.updateFileNames();
                    }
                    adapter.update(model.getVisibleFolder().parent);
                }
                break;
        }

    }

    private void paste() {
        if (model.isVisibleFolderRoot()) {
            for (Visual v : model.getBuffer()) {
                if (!model.isVisualFolder(v)) {
                    Toast.makeText(MainActivity.this, "Text can't be file", Toast.LENGTH_SHORT).show();
                    break;
                }
                Folder f = (Folder) v.getCopy();
                f.header = f.header.concat(getString(R.string.app_file_type));
                dataManager.createFile(f);
            }
        } else {
            for (Visual vis : model.getBuffer()) {
                Visual v = vis.getCopy();
                model.getVisibleFolder().add(v);
            }
            model.onUpdate();
        }
        adapter.update(model.getVisibleFolder());
    }

    public void showCreaterDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.file_maker_view);
        final EditText et = dialog.findViewById(R.id.name_et);
        Button btn = dialog.findViewById(R.id.create_btn);
        dialog.setTitle(R.string.file_maker_dialog);
        if (model.isVisibleFolderRoot()) {
            Switch sw = dialog.findViewById(R.id.is_folder);
            sw.setEnabled(false);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = et.getText().toString();
                if (s.isEmpty()) {
                    et.setError("Enter the name");
                    return;
                }
                if (model.isVisibleFolderRoot()) {
                    Folder f = new Folder();
                    f.header = s.concat(getString(R.string.app_file_type));
                    dataManager.createFile(f);
                } else {
                    Visual visual;
                    Switch sw = dialog.findViewById(R.id.is_folder);
                    if (sw.isChecked())
                        visual = new Folder();
                    else {
                        visual = new Visual();
                        visual.content = "";
                    }
                    visual.header = s;
                    model.getVisibleFolder().add(visual);
                    model.onUpdate();
                }
                adapter.update(model.getVisibleFolder());
                dialog.hide();
            }
        });
        dialog.show();
    }

    public void showEditerDialog(final MyRecyclerViewAdapter.MyViewHolder holder, final Visual visual) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.note_editer_view);
        dialog.setTitle(holder.getHeader());
        final EditText et = dialog.findViewById(R.id.content1);
        et.setText(visual.content);
        FloatingActionButton btn = dialog.findViewById(R.id.save_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = et.getText().toString();
                if (!visual.content.equals(text)) {
                    holder.setContent(text);
                    visual.content = text;
                    model.onUpdate();
                }
                dialog.hide();
            }
        });
        dialog.show();
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
