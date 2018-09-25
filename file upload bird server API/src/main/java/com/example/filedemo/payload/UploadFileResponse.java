package com.example.filedemo.payload;


public class UploadFileResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
 
    
    private String score;
    private String BirdPicurl;
    private String BirdDetails;

    public UploadFileResponse(String fileName, String fileDownloadUri, String fileType, long size,String score,String BirdPicurl,String BirdDetails ) {
        
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
        this.score=score;
        this.BirdPicurl=BirdPicurl;
        this.BirdDetails=BirdDetails;
        
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    

    public String getBirdPicurl() {
        return BirdPicurl;
    }

    public void setBirdPicurl(String BirdPicurl) {
        this.BirdPicurl = BirdPicurl;
    }

    public String getBirdDetails() {
        return BirdDetails;
    }

    public void setBirdDetails(String BirdDetails) {
        this.BirdDetails = BirdDetails;
    }

    
  

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
