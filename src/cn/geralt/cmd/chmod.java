package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;
import cn.geralt.projectFS.MyFile;

import java.io.IOException;

public class chmod extends Executable{
    public chmod(FileSystem fileSystem) {
        super(fileSystem);
        if((fileSystem.getCurrentUser().getUid()&0xffff0000)==0){
            this.permission0 = 0;
        }else
            this.permission0 = 7;
    }

    @Override
    public int process(String[] args) throws IOException {
        DEntry des = getFSHandler().dir2DEntry(args[1]);
        if(des==null){
            return -1;
        }else {
            des.getiNode().setMode(Integer.parseInt("0"+args[0],8));
            des.getiNode().update();
        }
        return 0;
    }
    @Override
    public int preProcess(String[] args) {
        DEntry des = getFSHandler().dir2DEntry(args[1]);
        int fd = 0;
        try {
            fd = getFSHandler().open(des.getAbsPath());
        }catch (NullPointerException e){
            return -4;
        }
        MyFile file = getFSHandler().getFiles().get(fd);
        boolean ans = getFSHandler().getCurrentUser().access(file, permission0);
        getFSHandler().close(fd);
        return ans?1:0;
    }
}
