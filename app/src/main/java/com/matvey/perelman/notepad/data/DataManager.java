package com.matvey.perelman.notepad.data;

import android.widget.Toast;

import com.google.gson.Gson;
import com.matvey.perelman.notepad.Model;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
public class DataManager {
    private FileFilter filter;
    private Gson gson;
    private final File dir;
    private Model model;
    private File openedFile;
    public DataManager(Model model,final String appFileType) {
        this.model = model;
        dir = model.getActivity().getExternalFilesDir(null);
        filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(appFileType);
            }
        };
        gson = new Gson();
        updateFileNames();
    }
    public void updateFileNames(){
        File[] files = dir.listFiles(filter);
        model.getRootFolder().visuals.clear();
        if(files == null)
            return;
        for (File file: files){
            Folder f = new Folder();
            f.header = file.getName();
            f.content = file.length() + " bytes";
            model.getRootFolder().add(f);
        }
    }
    public void saveFile() {
        if (openedFile != null && model.getVisibleFile() != null)
            try {
                FileOutputStream fos = new FileOutputStream(openedFile);
                Item item = model.getVisibleFile().toItem();
                item.header = null;
                String json = gson.toJson(item);
                fos.write(json.getBytes());
                fos.close();
            } catch (Exception e) {
                Toast.makeText(model.getActivity(), "Error with saving " + e.toString(), Toast.LENGTH_LONG).show();
            }
    }

    public void createFile(Folder folder) {
        openedFile = new File(dir, folder.header);
        if(!(folder.visuals.size() == 0 && openedFile.exists())) {
            model.setVisibleFile(folder);
            saveFile();
            updateFileNames();
        }
    }

    public void deleteFile(String fileName) {
        File f = new File(dir, fileName);
        openedFile = null;
        if(!f.delete()){
            Toast.makeText(model.getActivity(), "Error deleting", Toast.LENGTH_SHORT).show();
        }
        updateFileNames();
    }

    private Folder load(String fileName) {
        openedFile = new File(dir, fileName);
        Folder f = null;
        try {
            FileInputStream fis = new FileInputStream(openedFile);
            byte[] bytes = new byte[(int)openedFile.length()];
            if(fis.read(bytes) != bytes.length){
                throw new Exception("too long file");
            }
            String json = new String(bytes);
            Item item = gson.fromJson(json, Item.class);
            item.header = fileName;
            item.content = openedFile.length() + " bytes";
            f = Folder.fromItem(item);
            fis.close();
        } catch (Exception e) {
            Toast.makeText(model.getActivity(), "Error with loading " + e.toString(), Toast.LENGTH_LONG).show();
        }
        if(f == null) {
            f = new Folder();
            f.header = fileName;
        }
        return f;
    }
    private void loadToFolder(Folder folder, String fileName){
        Folder f = load(fileName);
        folder.visuals.clear();
        for (Visual v : f.visuals){
            folder.add(v);
        }
    }
    public Folder loadFile(String fileName){
        Folder f = load(fileName);
        f.parent = model.getRootFolder();
        return f;
    }
    public void upload(Folder folder){
        loadToFolder(folder, folder.header);
    }
}
