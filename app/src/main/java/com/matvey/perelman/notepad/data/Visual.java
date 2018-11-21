package com.matvey.perelman.notepad.data;

import java.io.Serializable;

public class Visual implements Serializable {
    public String header;
    public String content;
    public Folder parent;

    public Visual getCopy(){
        Visual v = new Visual();
        v.header = header;
        v.content = content;
        return v;
    }
}
