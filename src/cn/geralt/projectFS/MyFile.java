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
    private int status; //version
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

    public int getMode() {
        return mode;
    }

    public int getOwner() {
        return owner;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
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
        byte cur = dEntry.getiNode().getRealStatus();
        if((cur&(byte)0b10000000)!=0){
            System.out.println("file busy!");
            return new int[]{-1};
        }else if((cur&(byte)0b01111111)!=(status&(byte)0b01111111)){
            System.out.println("file has been modified!");
            return new int[]{-1};
        }else{
            dEntry.getiNode().setRealStatus((byte)((cur)|(byte)0b10000000)); //status[7] set 1
            int[] ans = dEntry.write(bytes,off,additons);
            dEntry.getiNode().setRealStatus((byte)((cur+1)&(byte)0b01111111));//status[7] set 0, status[6-0]++
            initialize();
            return ans;
        }
    }
}
