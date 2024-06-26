package com.ycl.file_manager.business.tree;

import androidx.annotation.NonNull;

import com.ycl.file_manager.business.filter.INodeFilter;
import com.ycl.file_manager.business.filter.NodeFilter;
import com.ycl.file_manager.business.sort.ISortStrategy;
import com.ycl.file_manager.business.sort.SortStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Directory
 * <p>
 * 文件夹节点
 * <p>
 * Created by Yclong on 2024/5/29.
 **/
public class DirectoryNode extends FileSystemNode {

    /**
     * 子节点集
     */
    private final List<FileSystemNode> subNodes = new ArrayList<>();

    public DirectoryNode(String path) {
        super(path);
    }

    @Override
    public int numOfFiles() {
        int numOfFiles = 0;
        for (FileSystemNode subNode : subNodes) {
            numOfFiles += subNode.numOfFiles();
        }
        return numOfFiles;
    }

    @Override
    public int numOfFiles(@NonNull INodeFilter filter) {
        int numOfFiles = 0;
        for (FileSystemNode subNode : subNodes) {
            numOfFiles += subNode.numOfFiles(filter);
        }
        return numOfFiles;
    }

    @Override
    public long sizeOfFiles() {
        long sizeOfFiles = 0;
        for (FileSystemNode subNode : subNodes) {
            sizeOfFiles += subNode.sizeOfFiles();
        }
        return sizeOfFiles;
    }

    @Override
    public long sizeOfFiles(@NonNull INodeFilter filter) {
        long sizeOfFiles = 0;
        for (FileSystemNode subNode : subNodes) {
            sizeOfFiles += subNode.sizeOfFiles(filter);
        }
        return sizeOfFiles;
    }

    @Override
    public String getFileName() {
        File file = new File(path);
        if (file.exists()) {
            return file.getName();
        }
        return null;
    }

    @Override
    public boolean rename(String name) {
        //1.重命名（没有后缀）
        File oldFile = new File(path);
        File newFile = new File(oldFile.getParent() + File.separator + name);
        if (oldFile.renameTo(newFile)) {
            //同步修改全部子路径
            for (FileSystemNode subNode : subNodes) {
                replacePrefixPath(subNode, path, newFile.getPath());
            }
            this.path = newFile.getPath();
            setLastModified(System.currentTimeMillis(), false);
        }
        return true;
    }

    @Override
    public boolean delete() {
        //删除文件
        deleteFile(this, NodeFilter.NONE);
        return true;
    }

    /**
     * 删除文件
     *
     * @param filter 过滤器
     */
    public boolean delete(INodeFilter filter) {
        //删除文件
        deleteFile(this, filter);
        return true;
    }

    @Override
    public boolean copy(String dstPath) {
        doCopyFile(this, dstPath, NodeFilter.NONE);
        return false;
    }

    /**
     * 拷贝文件夹
     *
     * @param dstPath 目标路径
     * @param filter  过滤器
     */
    public boolean copy(String dstPath, INodeFilter filter) {
        doCopyFile(this, dstPath, filter);
        return false;
    }

    private void doCopyFile(FileSystemNode root, String dstPath, INodeFilter filter) {
        if (root instanceof FileNode) {
            //拷贝
            root.copy(dstPath);
            return;
        }
        if (root instanceof DirectoryNode) {
            //如果文件夹不存在，则先创建
            File dirFile = new File(root.path);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            for (FileSystemNode node : ((DirectoryNode) root).getSubNodes(filter)) {
                doCopyFile(node, root.path, filter);
            }
        }
    }


    /**
     * 替换子节点路径
     *
     * @param root    根节点
     * @param oldPath 历史路径
     * @param newPath 新路径
     */
    private void replacePrefixPath(FileSystemNode root, String oldPath, String newPath) {
        if (root != null) {
            root.path = root.path.replace(oldPath, newPath);
            root.setLastModified(System.currentTimeMillis(), true);
        }
        if (root instanceof DirectoryNode) {
            for (FileSystemNode subNode : ((DirectoryNode) root).getSubNodes()) {
                replacePrefixPath(subNode, oldPath, newPath);
            }

        }
    }

    /**
     * 删除文件
     *
     * @param root 节点
     */
    private void deleteFile(FileSystemNode root, INodeFilter filter) {

        if (root instanceof FileNode) {
            //删除文件节点
            root.delete();
            return;
        }
        if (root instanceof DirectoryNode) {
            //删除子节点文件
            for (FileSystemNode node : ((DirectoryNode) root).getSubNodes(filter)) {
                deleteFile(node, filter);
            }
            //删除文件夹节点(需确认文件夹中是否有其他类型的文件)
            File file = new File(root.getPath());
            File[] files;
            //文件夹存在且文件夹中没有任何文件才能删除
            if (file.exists() && file.isDirectory() && (files = file.listFiles()) != null && files.length == 0) {
                file.deleteOnExit();
            }
            //移除本节点
            if (parent instanceof DirectoryNode) {
                ((DirectoryNode) parent).removeSubNode(this);
            }

        }

    }

    /**
     * 新增子节点
     *
     * @param fileOrDir 子节点
     */
    public void addSubNode(FileSystemNode fileOrDir) {
        fileOrDir.parent = this;
        this.subNodes.add(fileOrDir);
    }

    /**
     * 移除子节点
     *
     * @param fileOrDir 子节点
     */
    public void removeSubNode(FileSystemNode fileOrDir) {
        int size = subNodes.size();
        int i = 0;
        for (; i < size; ++i) {
            if (subNodes.get(i).getPath().equalsIgnoreCase(fileOrDir.getPath())) {
                break;
            }
        }
        if (i < size) {
            subNodes.remove(i);
        }
    }

    /**
     * 获取子节点集
     */
    public final List<FileSystemNode> getSubNodes() {
        return getSubNodes(SortStrategy.NONE, NodeFilter.NONE);
    }

    /**
     * 获取子节点集
     *
     * @param strategy 排序策略
     */
    public final List<FileSystemNode> getSubNodes(@NonNull ISortStrategy strategy) {
        return getSubNodes(strategy, NodeFilter.NONE);
    }

    /**
     * 获取子节点集
     *
     * @param filter 过滤器
     */
    public final List<FileSystemNode> getSubNodes(@NonNull INodeFilter filter) {
        return getSubNodes(SortStrategy.NONE, filter);
    }

    /**
     * 获取子节点集
     *
     * @param strategy 排序策略
     * @param filter   过滤器
     */
    public final List<FileSystemNode> getSubNodes(@NonNull ISortStrategy strategy, @NonNull INodeFilter filter) {
        return strategy.getSortNodes(applyFilter(subNodes, filter));
    }

    /**
     * 应用过滤器
     *
     * @param nodes  原始节点集
     * @param filter 过滤器
     * @return 新节点集快照
     */
    private List<FileSystemNode> applyFilter(List<FileSystemNode> nodes, @NonNull INodeFilter filter) {

        if (nodes == null) {
            return Collections.emptyList();
        }
        if (NodeFilter.NONE == filter) {
            return new ArrayList<>(nodes);
        }
        List<FileSystemNode> list = new ArrayList<>();
        for (FileSystemNode node : nodes) {
            if (node instanceof DirectoryNode) {
                if (node.numOfFiles(filter) > 0) {
                    list.add(node);
                }
            } else {
                if (filter.doFilter(node)) {
                    list.add(node);
                }
            }
        }
        return list;
    }
}
