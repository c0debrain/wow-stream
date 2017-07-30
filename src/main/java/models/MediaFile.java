package models;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import java.util.Date;

@Entity
@Cache
@Index
public class MediaFile {

    @Id
    @Index
    private Long id;
    
    @Index
    private String name;
    
    private String thumbPath;
    
    private String fileName;
    private String filePath;
    private String contentType;
    private long size;
    
    @Index
    private Date createDate;

    public MediaFile(){
        this.createDate = new Date();
    }
    
    public MediaFile(String fileName, String contentType) {
        this();
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public MediaFile(String fileName, String filePath, String contentType) {
        this(fileName, contentType);
        this.filePath = filePath;
    }
    
    public MediaFile(String fileName, String filePath, String contentType,
            long size) {
        this(fileName, contentType);
        this.filePath = filePath;
        this.size=size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }
    
    

}
