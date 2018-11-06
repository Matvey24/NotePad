package com.matvey.perelman.notepad.data;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataManager {
    private FileFilter filter;
    private Context context;
    private final File dir;

    private Folder names;

    private Folder openedFolder;
    private File openedFile;

    public DataManager(final Context context,final String appFileType) {
        this.context = context;
        dir = context.getExternalFilesDir(null);
        names = new Folder();
        filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName()
                        .substring(pathname.getName().lastIndexOf('.'))
                        .equals(appFileType);
            }
        };
        updateFileNames();
    }

    private void updateFileNames(){
        File[] files = dir.listFiles(filter);
        names.visuals.clear();
        for (File file: files){
            Folder f = new Folder();
            f.header = file.getName();
            //f.content = file.length() + " bytes";
            names.add(f);
        }
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
        updateFileNames();
    }

    public void createFile(Folder folder) {
        this.names.add(folder);
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
        updateFileNames();
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
