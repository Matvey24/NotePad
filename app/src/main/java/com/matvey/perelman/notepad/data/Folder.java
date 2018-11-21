package com.matvey.perelman.notepad.data;

import java.util.ArrayList;

public class Folder extends Visual{
    public ArrayList<Visual> visuals;
    public Folder(){
        visuals = new ArrayList<>();
    }
    public void add(Visual v){
        visuals.add(v);
        v.parent = this;
    }
    Item toItem(){
        Item main = new Item();
        main.header = header;
        main.items = new Item[visuals.size()];
        for(int i = 0; i < visuals.size(); ++i){
            Item item;
            Visual v = visuals.get(i);
            if(v instanceof Folder){
                item = ((Folder) v).toItem();
            }else {
                item = new Item();
                item.header = v.header;
                item.content = v.content;
            }
            main.items[i] = item;
        }
        return main;
    }
    static Folder fromItem(Item main){
        Folder f = new Folder();
        f.header = main.header;
        for(int i = 0; i < main.items.length; ++i){
            Visual v;
            Item it = main.items[i];
            if(it.items != null){
                v = Folder.fromItem(it);
            }else {
                v = new Visual();
                v.header = it.header;
                v.content = it.content;
            }
            f.add(v);
        }
        return f;
    }
    @Override
    public Folder getCopy(){
        Folder f = new Folder();
        f.header = header;
        for(Visual vis: visuals){
            f.add(vis.getCopy());
        }
        return f;
    }
}
