package com.ycl.file_manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ycl.file_manager.business.filter.INodeFilter;
import com.ycl.file_manager.business.filter.NodeFilter;
import com.ycl.file_manager.business.sort.ISortStrategy;
import com.ycl.file_manager.business.sort.SortStrategy;
import com.ycl.file_manager.business.tree.DirectoryNode;
import com.ycl.file_manager.business.tree.FileSystemNode;

import java.util.Arrays;

public class FileListActivity extends AppCompatActivity {

    private static final String TAG = "FileListActivity";

    private FileViewModel mFileViewModel;

    private FileListAdapter mFileListAdapter;

    private FileSystemNode mCurDirNode;

    private MenuItem mEditModeMenuItem;

    /**
     * 排序策略
     */
    private final ISortStrategy mSortStrategy = SortStrategy.NONE;

    private final INodeFilter mNodeFilter = NodeFilter.NONE;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click close buttonl");
                if (mCurDirNode != null) {
                    FileSystemNode parent = mCurDirNode.getParent();
                    if (parent instanceof DirectoryNode) {
                        mFileListAdapter.submitList(((DirectoryNode) parent).getSubNodes(mSortStrategy, mNodeFilter));
                        mCurDirNode = parent;
                    }
                }
            }
        });
        mFileViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(FileViewModel.class);
        mFileViewModel.getEditModeLivedata().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean candidate) {
                if (mEditModeMenuItem != null) {
                    if (candidate) {
                        mEditModeMenuItem.setTitle("取消");
                    } else {
                        mEditModeMenuItem.setTitle("编辑");
                    }
                }
                if (mFileListAdapter != null) {
                    mFileListAdapter.applyEditModeOp(candidate);
                }
            }
        });
        initView();
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_files, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_menu_delete:
                if (mFileListAdapter.getSelectedCount() > 0) {
                    if (mFileListAdapter != null) mFileListAdapter.applyDelOp(mNodeFilter);
                } else {
                    Toast.makeText(this, "请选择文件", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.id_menu_edit:
                this.mEditModeMenuItem = item;
                boolean candidate = !mFileListAdapter.isEditMode();
                mFileViewModel.applyEditMode(candidate);

                break;
            case R.id.id_menu_rename:
                if (mFileListAdapter.getSelectedCount() == 1) {
                    if (mFileListAdapter != null) mFileListAdapter.applyDelOp(mNodeFilter);
                } else {
                    Toast.makeText(this, "只能选择一个文件", Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult() called with: requestCode = [" + requestCode + "], permissions = [" + Arrays.toString(permissions) + "], grantResults = [" + Arrays.toString(grantResults) + "]");
        mFileViewModel.loadFileNodes(mNodeFilter);
    }

    private void initView() {
        RecyclerView rvList = findViewById(R.id.rv_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        rvList.setLayoutManager(layoutManager);
        mFileListAdapter = new FileListAdapter(this);
        rvList.setAdapter(mFileListAdapter);
        mFileViewModel.getObservableRootNode().observe(this, new Observer<FileSystemNode>() {
            @Override
            public void onChanged(FileSystemNode node) {
                Log.d(TAG, "onChanged() called with: node = [" + node + "]");
                if (node instanceof DirectoryNode) {
                    mFileListAdapter.submitList(((DirectoryNode) node).getSubNodes(mSortStrategy, mNodeFilter));
                }
            }
        });
        mFileListAdapter.setOnItemClickListener(new FileListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FileSystemNode node) {
                Log.d(TAG, "onItemClick() called with: node = [" + node + "]");
                if (node instanceof DirectoryNode) {
                    mFileListAdapter.submitList(((DirectoryNode) node).getSubNodes(mSortStrategy, mNodeFilter));
                    mCurDirNode = node;
                } else {
                    Toast.makeText(FileListActivity.this, "点击了文件" + node.getFileName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemSelectedChanged(FileSystemNode node) {

            }
        });
    }
}