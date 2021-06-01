package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MyFile {
    private int fd;
    private DEntry dEntry;
//    private byte[] buffer;

//    public byte[] read() throws IOException {
//        //用/temp 目录来模拟内存，直接将文件从VHD加载到/temp 返回路径
////        buffer =  dEntry.openFile();
//        String path = "temp"+dEntry.getPath();
//        File pathFile = new File(path);
//        if(!pathFile.exists()){
//            pathFile.mkdirs();
//        }
//        String fileDir = path+dEntry.getFileName();
//        File file = new File(fileDir);
//        if(!file.exists()){
//            file.createNewFile();
//        }
//        ByteIO byteIO = new ByteIO(fileDir);
//        byteIO.input(dEntry.getiNode().getFile(),0);
//        return fileDir;
//    }
    private MyFile(DEntry dEntry,int fd){
        this.dEntry = dEntry;
        this.fd = fd;
    }
    public static MyFile getInstance(DEntry dEntry,int fd){
        return new MyFile(dEntry,fd);
    }

    public byte[] read() throws IOException {
        return dEntry.read();
    }

    public void write(byte[] bytes,int off){
        dEntry.write(bytes,off);
    }
}
