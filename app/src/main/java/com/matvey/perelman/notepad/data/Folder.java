package com.matvey.perelman.notepad.data;

import java.util.ArrayList;

public class Folder extends Visual{
    public ArrayList<Visual> visuals;
    public Folder(){
        visuals = new ArrayList<>();
        super.folder = true;
    }
    public void add(Visual v){
        visuals.add(v);
        v.parent = this;
    }
}
