package com.matvey.perelman.notepad.data;

import java.io.Serializable;

public class Visual implements Serializable {
    public String header;
    public String content;
    public Folder parent;
    public boolean folder;

    public Visual getCopy(){
        Visual v;
        if(!folder){
            v = new Note();
            v.header = header;
            v.content = content;
        }else {
            v = new Folder();
            v.header = header;
            v.content = content;
            for(Visual vis: ((Folder)this).visuals){
                ((Folder) v).add(vis.getCopy());
            }
        }
        return v;
    }
}
