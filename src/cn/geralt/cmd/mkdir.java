package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;
import cn.geralt.projectFS.MyFile;

import java.io.IOException;

public class mkdir extends Executable{
    public mkdir(FileSystem fileSystem){
        super(fileSystem);
        this.permission0 = 2;
    }

    @Override
    public int process(String[] args){
        DEntry cur = getFSHandler().getCurrent();
        try {
            getFSHandler().newDir(args[0],cur);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }
    @Override
    public int preProcess(String[] args) {
        DEntry des = getFSHandler().getCurrent();
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
