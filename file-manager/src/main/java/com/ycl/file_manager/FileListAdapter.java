package com.ycl.file_manager;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ycl.file_manager.business.filter.INodeFilter;
import com.ycl.file_manager.business.sort.ISortStrategy;
import com.ycl.file_manager.business.tree.DirectoryNode;
import com.ycl.file_manager.business.tree.FileSystemNode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class FileListAdapter extends ListAdapter<FileSystemNode, FileListAdapter.VH> {

    private final Context mContext;

    private OnItemClickListener mOnItemClickListener;


    private final SimpleDateFormat sdf = new SimpleDateFormat("dd HH:mm:ss", Locale.getDefault());

    /**
     * 编辑模式
     */
    private boolean editMode;

    private final SparseBooleanArray itemSelectedArray = new SparseBooleanArray();

    /**
     * 编辑
     */
    public void applyEditModeOp(boolean editMode) {
        this.editMode = editMode;
        notifyItemRangeChanged(0, getCurrentList().size());
    }

    /**
     * 是否是编辑模式
     */
    public boolean isEditMode() {
        return editMode;
    }

    /**
     * 重命名
     *
     * @param node     点中节点
     * @param name     新名称
     * @param strategy 排序策略
     */
    public void applyRenameOp(FileSystemNode node, String name, ISortStrategy strategy) {
        if (node.rename(name)) {
            FileSystemNode parent;
            if ((parent = node.getParent()) != null && parent instanceof DirectoryNode) {
                //重命名后需要重新排序
                this.submitList(((DirectoryNode) parent).getSubNodes(strategy));
            }
        }

    }

    /**
     * 删除
     */
    public void applyDelOp(INodeFilter filter) {
        List<FileSystemNode> snapshot = getSnapshot();
        List<FileSystemNode> selectedNodes = getSelectedNodes();
        for (FileSystemNode node : selectedNodes) {
            if (node instanceof DirectoryNode) {
                ((DirectoryNode) node).delete(filter);
            } else {
                node.delete();
            }
        }
        snapshot.removeAll(selectedNodes);

        this.submitList(snapshot);
    }

    /**
     * 选中的文件节点
     */
    public List<FileSystemNode> getSelectedNodes() {

        List<FileSystemNode> nodes = new ArrayList<>();
        for (int i = 0; i < itemSelectedArray.size(); i++) {
            nodes.add(getCurrentList().get(itemSelectedArray.keyAt(i)));
        }
        return nodes;
    }

    /**
     * 获取选中数量
     */
    public int getSelectedCount() {
        return itemSelectedArray.size();
    }

    /**
     * 快照
     */
    private List<FileSystemNode> getSnapshot() {
        return new ArrayList<>(getCurrentList());
    }

    @Override
    public void submitList(@Nullable List<FileSystemNode> list) {
        super.submitList(list, null);
    }

    @Override
    public void submitList(@Nullable List<FileSystemNode> list, @Nullable Runnable commitCallback) {
        this.itemSelectedArray.clear();
        super.submitList(list, commitCallback);
    }

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
        private final CheckBox mCheckBox;
        private final TextView mLastModifiedView;

        public VH(@NonNull View itemView) {
            super(itemView);
            ivFileView = itemView.findViewById(R.id.iv_file);
            tvFileNameView = itemView.findViewById(R.id.tv_file_name);
            mCheckBox = itemView.findViewById(R.id.cb);
            mLastModifiedView = itemView.findViewById(R.id.tv_file_last_modified);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (editMode) {
                        boolean candidate = !itemSelectedArray.get(position, false);
                        if (candidate) {
                            itemSelectedArray.put(position, true);
                        } else {
                            itemSelectedArray.delete(position);
                        }
                        mCheckBox.setChecked(candidate);
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemSelectedChanged(getItem(position));
                        }
                    } else {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(getItem(position));
                        }
                    }
                }
            });
            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!buttonView.isPressed()) {
                        return;
                    }
                    int position = getAdapterPosition();
                    if (isChecked) {
                        itemSelectedArray.put(position, true);
                    } else {
                        itemSelectedArray.delete(position);
                    }
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemSelectedChanged(getItem(position));
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

            if (editMode) {
                mCheckBox.setVisibility(View.VISIBLE);
                mCheckBox.setChecked(itemSelectedArray.get(getAdapterPosition(), false));
            } else {
                mCheckBox.setVisibility(View.GONE);
            }

            mLastModifiedView.setText(sdf.format(node.lastModified()));
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

        void onItemSelectedChanged(FileSystemNode node);

    }

}
