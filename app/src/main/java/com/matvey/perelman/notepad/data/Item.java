package com.matvey.perelman.notepad.data;
import com.google.gson.annotations.SerializedName;
public class Item {
    @SerializedName("h")
    public String header;
    @SerializedName("c")
    public String content;
    @SerializedName("i")
    Item[] items;
}
