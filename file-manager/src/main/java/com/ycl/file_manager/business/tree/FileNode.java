package com.ycl.file_manager.business.tree;

import androidx.annotation.NonNull;

import com.ycl.file_manager.business.filter.INodeFilter;

import java.io.File;
import java.io.IOException;

/**
 * FileNode
 * <p>
 * 文件节点
 * <p>
 * Created by Yclong on 2024/5/29.
 **/
public class FileNode extends FileSystemNode {

    public FileNode(String path) {
        super(path);
    }

    @Override
    public int numOfFiles() {
        return 1;
    }

    @Override
    public int numOfFiles(@NonNull INodeFilter filter) {
        return filter.doFilter(this) ? numOfFiles() : 0;
    }

    @Override
    public long sizeOfFiles() {
        File file = new File(path);
        if (!file.exists()) {
            return 0;
        }
        return file.length();
    }

    @Override
    public long sizeOfFiles(@NonNull INodeFilter filter) {
        return filter.doFilter(this) ? sizeOfFiles() : 0;
    }

    @Override
    public String getFileName() {
        return new File(path).getName();
    }

    @Override
    public boolean rename(String name) {
        //1.修改文件名
        File oldFile = new File(path);
        String newName = name + "." + getFileExtension(path);
        File newFile = new File(oldFile.getParent() + File.separator + newName);
        boolean b = oldFile.renameTo(newFile);
        if (b) {
            this.path = newFile.getPath();
            setLastModified(System.currentTimeMillis(), false);
        }
        return b;
    }

    @Override
    public boolean delete() {
        //直接删除文件即可
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        boolean b = file.delete();
        if (b) {
            //更新时间
            if (parent instanceof DirectoryNode) {
                parent.setLastModified(System.currentTimeMillis(), true);
                ((DirectoryNode) parent).removeSubNode(this);
            }
        }

        return b;
    }

    @Override
    public boolean copy(String dstPath) {
        try {
            File srcFile = new File(path);
            if (!srcFile.exists()) {
                return false;
            }
            File dstFile = new File(dstPath);
            if (!dstFile.exists()) {
                dstFile.createNewFile();
            }
            //拷贝文件
            copyFile(srcFile, dstFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


}
