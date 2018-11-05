package com.matvey.perelman.notepad.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DataManager {
    private static final String ARRAY_NAME = "names_array";
    //private static final String SETTINGS_NAME = "settings";

    private Folder names;

    private Folder file;
    private File systemFile;

    private SharedPreferences sharedPreferences;
    private Context context;
    private final File dir;

    public DataManager(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set = sharedPreferences.getStringSet(ARRAY_NAME, null);
        ArrayList<String> names;
        dir = context.getExternalCacheDir();
        if (set != null) {
            names = new ArrayList<>(set);
        } else {
            names = new ArrayList<>();
        }
        this.names = new Folder();
        for (int i = 0; i < names.size(); ++i) {
            Folder f = new Folder();
            f.header = names.get(i);
            this.names.add(f);
        }
    }

    public void saveFileNames() {
        HashSet<String> strings = new HashSet<>();
        for (int i = 0; i < names.visuals.size(); ++i) {
            strings.add(names.visuals.get(i).header);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(ARRAY_NAME, strings);
        editor.apply();
    }

    public void saveFile() {
        if (systemFile != null)
            try {
                Toast.makeText(context, "Saving", Toast.LENGTH_SHORT).show();
                FileOutputStream fos = new FileOutputStream(systemFile);
                ObjectOutputStream out = new ObjectOutputStream(fos);
                out.writeObject(file);
                out.flush();
                out.close();
            } catch (Exception e) {
                Toast.makeText(context, "Error with saving " + e.toString(), Toast.LENGTH_LONG).show();
            }
    }

    public void createFile(Folder folder) {
        names.add(folder);
        saveFileNames();
        file = folder;
        systemFile = new File(dir, folder.header);
        saveFile();
    }

    public void deleteFile(String fileName) {
        File f = new File(dir, fileName);
        systemFile = null;
        if(!f.delete()){
            Toast.makeText(context, "Error deleting", Toast.LENGTH_SHORT).show();
        }
    }

    public Folder loadFile(String fileName) {
        Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show();
        systemFile = new File(dir, fileName);
        try {
            FileInputStream fis = new FileInputStream(systemFile);
            ObjectInputStream in = new ObjectInputStream(fis);
            file = (Folder) in.readObject();
            in.close();
        } catch (Exception e) {
            Toast.makeText(context, "Error with loading " + e.toString(), Toast.LENGTH_LONG).show();
        }
        if(file == null) {
            file = new Folder();
            file.header = fileName;
        }
        return file;
    }

    public Folder getFileNames() {
        return names;
    }

    public Folder getFile() {
        return file;
    }
}
