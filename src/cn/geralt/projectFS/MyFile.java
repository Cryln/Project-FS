package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MyFile {
    private int fd;
    private DEntry dEntry;
    private String name;
    private int type;
    private int status;
    private int fileLen;
    private int mode;
    private int owner;
    private long lastModifiedTime;

    public MyFile(DEntry dEntry,int fd){
        this.dEntry = dEntry;
        this.fd = fd;
        initialize();
    }

    private void initialize(){
        name = dEntry.getFileName();
        type = dEntry.getiNode().getType();
        status = dEntry.getiNode().getStatus();
        fileLen = dEntry.getiNode().getRawFileLen();
        mode = dEntry.getiNode().getMode();
        owner = dEntry.getiNode().getUid();
        lastModifiedTime = dEntry.getiNode().getLastModifyTime();
    }

    public String getName(){
        return name;
    }

    public int getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public int getFileLen() {
        return fileLen;
    }
    //    public static MyFile getInstance(DEntry dEntry,int fd){
//        return new MyFile(dEntry,fd);
//    }

    public byte[] read() throws IOException {
        return dEntry.read();
    }

    public int[] write(byte[] bytes,int off,int[] additons) throws IOException {
        int[] ans = dEntry.write(bytes,off,additons);
        initialize();
        return ans;
    }
}
