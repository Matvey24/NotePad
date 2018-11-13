package com.matvey.perelman.notepad.data;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
public class DataManager {
    private FileFilter filter;
    private Gson gson;
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
        gson = new Gson();
        updateFileNames();
    }
    private void updateFileNames(){
        File[] files = dir.listFiles(filter);
        names.visuals.clear();
        if(files == null)
            return;
        for (File file: files){
            Folder f = new Folder();
            f.header = file.getName();
            f.content = file.length() + " bytes";
            names.add(f);
        }
    }
    public void saveFile() {
        if (openedFile != null)
            try {
                FileOutputStream fos = new FileOutputStream(openedFile);
                String json = gson.toJson(openedFolder.toItem());
                fos.write(json.getBytes());
                fos.close();
            } catch (Exception e) {
                Toast.makeText(context, "Error with saving " + e.toString(), Toast.LENGTH_LONG).show();
            }
        updateFileNames();
    }

    public void createFile(Folder folder) {
        openedFolder = folder;
        openedFile = new File(dir, folder.header);
        if(!(folder.visuals.size() == 0 && openedFile.exists())) {
            saveFile();
        }
        updateFileNames();
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
        openedFile = new File(dir, fileName);
        try {
            FileInputStream fis = new FileInputStream(openedFile);
            byte[] bytes = new byte[(int)openedFile.length()];
            if(fis.read(bytes) != bytes.length){
                throw new Exception("too long file");
            }
            String json = new String(bytes);
            openedFolder = Folder.fromItem(gson.fromJson(json, Item.class));
            fis.close();
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
