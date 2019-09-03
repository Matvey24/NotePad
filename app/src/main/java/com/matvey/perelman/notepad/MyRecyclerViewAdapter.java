package com.matvey.perelman.notepad;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.matvey.perelman.notepad.data.Folder;
import com.matvey.perelman.notepad.data.Visual;

import java.util.ArrayList;

import static com.matvey.perelman.notepad.Model.COPY_MODE;
import static com.matvey.perelman.notepad.Model.CUT_MODE;
import static com.matvey.perelman.notepad.Model.FILE_MAKER_MODE;
import static com.matvey.perelman.notepad.Model.RENAME_MODE;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
    private Model model;
    private ArrayList<Visual> selectedData;

    MyRecyclerViewAdapter(Model model) {
        this.model = model;
        selectedData = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder,int j) {
        if (model.getVisibleFolder().visuals.size() == 0) {
            myViewHolder.setFolder(false);
            myViewHolder.setHeader("Empty!");
            myViewHolder.setContent("Empty! Empty!");
            myViewHolder.itemView.setOnClickListener(null);
            myViewHolder.itemView.setOnLongClickListener(null);
            return;
        }
        final int i = j;
        myViewHolder.setFolder(model.isVisualFolder(model.getVisibleElement(i)));
        myViewHolder.setHeader(model.getVisibleElement(i).header);
        myViewHolder.setContent(model.getVisibleElement(i).content);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (model.mode) {
                    case COPY_MODE:
                    case CUT_MODE:
                        selectedData.clear();
                        Visual vis = model.getVisibleElement(i);
                        if (model.isVisibleFolderRoot()) {
                            Toast.makeText(model.getActivity(), "Loading", Toast.LENGTH_SHORT).show();
                            Folder folder = model.getDataManager().loadFile(vis.header);
                            folder.header = folder.header.substring(0, folder.header.lastIndexOf('.'));
                            selectedData.add(folder);
                            if(model.mode == CUT_MODE) {
                                model.getDataManager().deleteFile(vis.header);
                            }
                        } else {
                            selectedData.add(vis);
                            if(model.mode == CUT_MODE){
                                model.getVisibleFolder().visuals.remove(vis);
                                model.onUpdate();
                            }
                        }
                        model.setBuffer(selectedData);
                        if(model.mode == CUT_MODE){
                            update(model.getVisibleFolder());
                        }
                        model.mode = FILE_MAKER_MODE;
                        break;
                    case RENAME_MODE:
                        model.getActivity().showRenameDialog(myViewHolder, model.getVisibleElement(i));
                        break;
                    case FILE_MAKER_MODE:
                        if (model.isVisibleFolderRoot()) {
                            Toast.makeText(model.getActivity(), "Loading", Toast.LENGTH_SHORT).show();
                            Folder f = (Folder)model.getVisibleElement(i);
                            model.getDataManager().upload(f);
                            model.setVisibleFile(f);
                            update(f);
                        } else {
                            if (model.isVisualFolder(model.getVisibleElement(i))) {
                                update((Folder) model.getVisibleElement(i));
                            } else {
                                model.getActivity().showEditorDialog(myViewHolder,model.getVisibleElement(i));
                            }
                        }
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return Math.max(1, model.getVisibleFolder().visuals.size());
    }

    void update(Folder folder) {
        model.setVisibleFolder(folder);
        if (model.isVisibleFolderRoot())
            model.getActivity().setTitle(R.string.app_name);
        else {
            model.getActivity().setTitle(shortName(folder.header) + "    l: " + model.getFolderLevel(folder));
        }
        notifyDataSetChanged();
    }

    private String shortName(String name) {
        if (name == null)
            return null;
        if (name.length() > 26) {
            return ".." + name.substring(name.length() - 25);
        }
        return name;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView header;
        private TextView content;
        boolean isFolder;

        MyViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
            content = itemView.findViewById(R.id.content);
        }

        void setHeader(String header) {
            this.header.setText(header);
        }

        void setContent(String content) {
            if (content == null) {
                this.content.setText(null);
                return;
            }
            String[] lines = content.split("\\n");
            if (lines.length > 1) {
                this.content.setText(lines[0].substring(0, lines[0].length() > 26 ? 25 : lines[0].length()));
                this.content.append("..");
                return;
            }
            if (content.length() > 26) {
                this.content.setText(content.substring(0, 25));
                this.content.append("..");
                return;
            }
            this.content.setText(content);
        }
        String getHeader(){
            return header.getText().toString();
        }
        private void setFolder(boolean isFolder) {
            this.isFolder = isFolder;
            ImageView imageView = itemView.findViewById(R.id.imageView);
            if (isFolder) {
                imageView.setImageResource(R.drawable.folder);
            } else {
                imageView.setImageResource(R.drawable.file);
            }
        }
    }
}
