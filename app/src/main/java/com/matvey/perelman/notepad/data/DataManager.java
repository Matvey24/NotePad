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

    private Folder openedFolder;
    private File openedFile;

    private SharedPreferences sharedPreferences;
    private Context context;
    private final File dir;

    public DataManager(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set = sharedPreferences.getStringSet(ARRAY_NAME, null);
        ArrayList<String> names;
        dir = context.getExternalFilesDir(null);
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
        if (openedFile != null)
            try {
                Toast.makeText(context, "Saving", Toast.LENGTH_SHORT).show();
                FileOutputStream fos = new FileOutputStream(openedFile);
                ObjectOutputStream out = new ObjectOutputStream(fos);
                out.writeObject(openedFolder);
                out.flush();
                out.close();
            } catch (Exception e) {
                Toast.makeText(context, "Error with saving " + e.toString(), Toast.LENGTH_LONG).show();
            }
    }

    public void createFile(Folder folder) {
        names.add(folder);
        saveFileNames();
        openedFolder = folder;
        openedFile = new File(dir, folder.header);
        if(!(folder.visuals.size() == 0 && openedFile.exists()))
            saveFile();
    }

    public void deleteFile(String fileName) {
        File f = new File(dir, fileName);
        openedFile = null;
        if(!f.delete()){
            Toast.makeText(context, "Error deleting", Toast.LENGTH_SHORT).show();
        }
    }

    public Folder loadFile(String fileName) {
        Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show();
        openedFile = new File(dir, fileName);
        try {
            FileInputStream fis = new FileInputStream(openedFile);
            ObjectInputStream in = new ObjectInputStream(fis);
            openedFolder = (Folder) in.readObject();
            in.close();
        } catch (Exception e) {
            Toast.makeText(context, "Error with loading " + e.toString(), Toast.LENGTH_LONG).show();
        }
        if(openedFolder == null) {
            openedFolder = new Folder();
            openedFolder.header = fileName;
        }
        return openedFolder;
    }

    public Folder getFileNames() {
        return names;
    }

    public Folder getOpenedFolder() {
        return openedFolder;
    }
}
