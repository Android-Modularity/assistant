package com.march.assistant.funcs.file;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 2018/6/13
 * Describe :
 *
 * @author chendong
 */
public class FileModel {

    private boolean isTop;
    private String name;
    private File file;
    private FileModel parent;
    private List<FileModel> children;
    private int index;

    public FileModel(File file) {
        this.file = file;
    }

    public FileModel(File file, FileModel parent) {
        this.file = file;
        this.parent = parent;
    }

    public FileModel(String name, File file, FileModel parent) {
        this.name = name;
        this.file = file;
        this.parent = parent;
    }

    public String getName() {
        if (TextUtils.isEmpty(name)) {
            if (file != null) {
                name = file.getName();
            }
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public FileModel getParent() {
        return parent;
    }

    public void setParent(FileModel parent) {
        this.parent = parent;
    }

    public List<FileModel> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    children.add(new FileModel(child, this));
                }
            }
        }
        return children;
    }

    public void setChildren(List<FileModel> children) {
        this.children = children;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
