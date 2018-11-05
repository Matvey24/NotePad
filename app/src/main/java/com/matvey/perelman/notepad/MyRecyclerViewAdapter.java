package com.matvey.perelman.notepad;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.matvey.perelman.notepad.data.Folder;
import com.matvey.perelman.notepad.data.Visual;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
    private Folder folder;
    private MainActivity mainActivity;

    MyRecyclerViewAdapter(MainActivity mainActivity, Folder folder) {
        this.folder = folder;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        if (folder.visuals.size() == 0) {
            myViewHolder.setFolder(false);
            myViewHolder.setHeader("Empty!");
            myViewHolder.setContent("Empty!Empty!Empty!Empty!Empty!Empty!");
            myViewHolder.itemView.setOnClickListener(null);
            return;
        }
        myViewHolder.setFolder(folder.visuals.get(i).folder);
        myViewHolder.setHeader(folder.visuals.get(i).header);
        myViewHolder.setContent(folder.visuals.get(i).content);
        final int j = i;
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mainActivity.mode) {
                    case MainActivity.CUT_MODE:
                        Visual vis = folder.visuals.remove(j);
                        if (folder.parent == null) {
                            mainActivity.setBuffer(mainActivity.getDataManager().loadFile(vis.header));
                            mainActivity.getDataManager().deleteFile(vis.header);
                            mainActivity.getDataManager().saveFileNames();
                        } else {
                            mainActivity.setBuffer(vis);
                        }
                        mainActivity.mode = MainActivity.FILE_MAKER_MODE;
                        update(folder);
                        break;
                    case MainActivity.COPY_MODE:
                        vis = folder.visuals.get(j);
                        if (folder.parent == null)
                            mainActivity.setBuffer(mainActivity.getDataManager().loadFile(vis.header));
                        else
                            mainActivity.setBuffer(vis);
                        mainActivity.mode = MainActivity.FILE_MAKER_MODE;
                        break;
                    case MainActivity.FILE_MAKER_MODE:
                        if (folder.parent == null) {
                            Folder f = mainActivity.getDataManager().loadFile(folder.visuals.get(j).header);
                            folder.add(f);
                            update(f);
                        } else {
                            if (folder.visuals.get(j).folder) {
                                update((Folder) folder.visuals.get(j));
                            } else {
                                final Dialog dialog = new Dialog(mainActivity);
                                dialog.setTitle(myViewHolder.header.getText().toString());
                                dialog.setContentView(R.layout.note_editer_view);
                                final EditText et = dialog.findViewById(R.id.content1);
                                et.setText(folder.visuals.get(j).content);
                                FloatingActionButton btn = dialog.findViewById(R.id.save_btn);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        folder.visuals.get(j).content = et.getText().toString();
                                        myViewHolder.setContent(et.getText().toString());
                                        dialog.hide();
                                    }
                                });
                                dialog.show();
                            }
                        }
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return folder.visuals.size() < 1 ? 1 : folder.visuals.size();
    }

    void update(Folder folder) {
        this.folder = folder;
        if (folder.header == null || folder.header.length() == 0)
            mainActivity.setTitle("NotePad");
        else
            mainActivity.setTitle(shortName(folder.header));
        notifyDataSetChanged();
    }

    private String shortName(String name) {
        if (name == null)
            return null;
        if (name.length() > 20) {
            return ".." + name.substring(name.length() - 25);
        }
        return name;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView header;
        private TextView content;
        boolean isFolder;

        MyViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
            content = itemView.findViewById(R.id.content);
        }

        public void setHeader(String header) {
            this.header.setText(header);
        }

        public void setContent(String content) {
            if (content == null) {
                this.content.setText(null);
                return;
            }
            content = content.split("\\n")[0];
            if (content.length() > 25) {
                this.content.setText(content.substring(0, 25));
                this.content.append("..");
                return;
            }
            this.content.setText(content);
        }

        public void setFolder(boolean isFolder) {
            this.isFolder = isFolder;
            ImageView imageView = itemView.findViewById(R.id.imageView);
            if (isFolder) {
                imageView.setImageResource(R.drawable.folder);
            } else {
                imageView.setImageResource(R.drawable.file);
            }
        }
    }

    public Folder getFolder() {
        return folder;
    }
}
