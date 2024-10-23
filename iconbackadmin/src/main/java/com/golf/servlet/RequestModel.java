package com.golf.servlet;

import java.util.List;

public class RequestModel {
	private String title;
    private String description;
    private FileInfoModel file;
    private List<FileInfoModel> photo;
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public FileInfoModel getFile() {
        return file;
    }
    public void setFile(FileInfoModel file) {
        this.file = file;
    }
    public List<FileInfoModel> getPhoto() {
        return photo;
    }
    public void setPhoto(List<FileInfoModel> photo) {
        this.photo = photo;
    }
    
    @Override
    public String toString() {
        return "RequestModel [title=" + title + ", description=" + description
                + ", file=" + file + ", photo=" + photo + "]";
    }


}
