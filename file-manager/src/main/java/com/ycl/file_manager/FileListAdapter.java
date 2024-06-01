package com.ycl.file_manager;/*
 * null.java
 * Created by Yclong on 2024/6/1
 **/

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ycl.file_manager.business.tree.DirectoryNode;
import com.ycl.file_manager.business.tree.FileSystemNode;

import java.util.Objects;

public class FileListAdapter extends ListAdapter<FileSystemNode, FileListAdapter.VH> {

    private final Context mContext;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    protected FileListAdapter(Context context) {
        super(ITEM_CALLBACK);
        this.mContext = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_node, parent, false);
        return new VH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bindTo(getItem(position));
    }

    public class VH extends RecyclerView.ViewHolder {

        private final ImageView ivFileView;
        private final TextView tvFileNameView;

        public VH(@NonNull View itemView) {
            super(itemView);
            ivFileView = itemView.findViewById(R.id.iv_file);
            tvFileNameView = itemView.findViewById(R.id.tv_file_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(getItem(getAdapterPosition()));
                    }
                }
            });
        }

        void bindTo(FileSystemNode node) {
            if (node == null) return;
            if (node instanceof DirectoryNode) {
                ivFileView.setImageResource(R.mipmap.ic_folder);
            } else {
                Glide.with(mContext)
                        .load(node.getPath())
                        .error(R.mipmap.defualt_img)
                        .into(ivFileView);
            }
            tvFileNameView.setText(node.getFileName());
        }

    }

    private static final DiffUtil.ItemCallback<FileSystemNode> ITEM_CALLBACK = new DiffUtil.ItemCallback<FileSystemNode>() {
        @Override
        public boolean areItemsTheSame(@NonNull FileSystemNode oldItem, @NonNull FileSystemNode newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull FileSystemNode oldItem, @NonNull FileSystemNode newItem) {
            return Objects.equals(oldItem.getPath(), newItem.getPath());
        }
    };

    public interface OnItemClickListener {

        void onItemClick(FileSystemNode node);

    }

}
