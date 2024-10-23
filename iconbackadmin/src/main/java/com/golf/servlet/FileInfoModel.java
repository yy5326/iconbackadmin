package com.golf.servlet;

public class FileInfoModel {

	private String name;
    private String fileName;
    private String uploadedFileName;
    private long fileSize;
    private String contentType;
    
    
    public FileInfoModel(String name, String fileName, String uploadedFileName,
            long fileSize, String contentType) {
        super();
        this.name = name;
        this.fileName = fileName;
        this.uploadedFileName = uploadedFileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getUploadedFileName() {
        return uploadedFileName;
    }
    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }
    public long getFileSize() {
        return fileSize;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    @Override
    public String toString() {
        return "FileInfoModel [name=" + name + ", fileName=" + fileName
                + ", uploadedFileName=" + uploadedFileName + ", fileSize="
                + fileSize + ", contentType=" + contentType + "]";
    }


	
}
