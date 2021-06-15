package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;
import cn.geralt.projectFS.MyFile;
import cn.geralt.util.ByteIO;

import java.io.IOException;

public class mv extends Executable{
    public mv(FileSystem fileSystem) {
        super(fileSystem);
        permission0 = 4;
        permission1 = 2;
        type1 = 0;
    }

    @Override
    public int process(String[] args) throws IOException {
        DEntry cur = getFSHandler().getCurrent();
        DEntry child = cur.getChild(args[0]);
        cur.getChildren().remove(child);
        int childAmount = cur.getChildren().size();
        byte[] data = new byte[childAmount*4+4];
        System.arraycopy(ByteIO.intToByteArray(childAmount),0,data,0,4);
        int index = 4;
        for (DEntry curChild : cur.getChildren()) {
            System.arraycopy(ByteIO.intToByteArray(curChild.getiNodeNum()),0,data,index,4);
            index+=4;
        }
        try {
            int fd = FSHandler.open("../"+cur.getFileName());
            FSHandler.write(fd,data,0);
            FSHandler.close(fd);
            fd = FSHandler.open(args[1]);
            MyFile file = FSHandler.getFiles().get(fd);
            data = new byte[file.getFileLen()+4];
            FSHandler.read(fd,data,0,file.getFileLen());
            System.arraycopy(ByteIO.intToByteArray(file.getFileLen()/4),0,data,0,4);
            System.arraycopy(ByteIO.intToByteArray(child.getiNodeNum()),0,data,data.length-4,4);
            FSHandler.write(fd,data,0);
            FSHandler.dir2DEntry(args[1]).getChildren().add(child);
            FSHandler.close(fd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int preProcess(String[] args) {
        DEntry des0 = getFSHandler().dir2DEntry(args[0]);
        int fd0 = 0;
        try {
            fd0 = getFSHandler().open(des0.getAbsPath());
        }catch (NullPointerException e){
            return -1;
        }
        MyFile file0 = getFSHandler().getFiles().get(fd0);
        boolean ans0 = getFSHandler().getCurrentUser().access(file0, permission0);
        getFSHandler().close(fd0);

        DEntry des1 = getFSHandler().dir2DEntry(args[1]);
        int fd1 = 0;
        try {
            fd1 = getFSHandler().open(des1.getAbsPath());
        }catch (NullPointerException e){
            return -4;
        }
        if(des1.getiNode().getType()!=type1){
            return -3;
        }
        MyFile file1 = getFSHandler().getFiles().get(fd1);
        boolean ans1 = getFSHandler().getCurrentUser().access(file1, permission1);
        getFSHandler().close(fd1);

        return ans0&&ans1?1:0;
    }
}
