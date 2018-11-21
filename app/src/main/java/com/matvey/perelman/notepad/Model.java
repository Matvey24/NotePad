package com.matvey.perelman.notepad;

import com.matvey.perelman.notepad.data.DataManager;
import com.matvey.perelman.notepad.data.Folder;
import com.matvey.perelman.notepad.data.Visual;

import java.util.ArrayList;

public class Model {
    private MainActivity activity;
    private Folder rootFolder;
    private Folder visibleFile;
    private Folder visibleFolder;
    private ArrayList<Visual> buffer;
    int mode;
    static final int FILE_MAKER_MODE = 0;
    static final int CUT_MODE = 1;
    static final int COPY_MODE = 2;
    Model(MainActivity activity){
        this.activity = activity;
        rootFolder = new Folder();
        visibleFolder = rootFolder;
        mode = FILE_MAKER_MODE;
    }

    ArrayList<Visual> getBuffer() {
        return buffer;
    }

    void setBuffer(ArrayList<Visual> buffer) {
        if(this.buffer == null){
            this.buffer = new ArrayList<>();
            activity.onCreateBuffer();
        }
        this.buffer.clear();
        this.buffer.addAll(buffer);
    }
    boolean isVisualFolder(Visual v){
        return v instanceof Folder;
    }
    boolean isVisibleFolderRoot(){
        return isFolderRoot(visibleFolder);
    }
    boolean isVisibleFolderFile(){
        return isFolderRoot(visibleFolder.parent);
    }
    boolean isFolderRoot(Visual v){
        return v.parent == null;
    }
    boolean isFolderFile(Visual v){
        return isFolderRoot(v.parent);
    }
    public Folder getRootFolder() {
        return rootFolder;
    }
    Folder getVisibleFolder() {
        return visibleFolder;
    }
    int getFolderLevel(Folder folder){
        if(isFolderRoot(folder))
            return 0;
        else
            return 1 + getFolderLevel(folder.parent);
    }
    Visual getVisibleElement(int i){
        return visibleFolder.visuals.get(i);
    }
    public MainActivity getActivity() {
        return activity;
    }
    void setVisibleFolder(Folder visibleFolder) {
        this.visibleFolder = visibleFolder;
    }
    DataManager getDataManager(){
        return activity.getDataManager();
    }

    public Folder getVisibleFile() {
        return visibleFile;
    }

    public void setVisibleFile(Folder visibleFile) {
        this.visibleFile = visibleFile;
    }
}

