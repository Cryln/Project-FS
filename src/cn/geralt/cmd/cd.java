package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;
import cn.geralt.projectFS.MyFile;

import java.io.IOException;

public class cd extends Executable{
    public cd(FileSystem fileSystem) {
        super(fileSystem);
        this.permission = 1;
    }

    @Override
    public int process(String[] args) throws IOException {
        DEntry des = getFSHandler().dir2DEntry(args[0]);
        if(des==null){
            return -1;
        }else {
            des.openDir();
            getFSHandler().setCurrent(des);
        }
        return 0;
    }

    @Override
    public int preProcess(String[] args) {
        DEntry des = getFSHandler().dir2DEntry(args[0]);
        int fd = 0;
        try {
            fd = getFSHandler().open(des.getAbsPath());
        }catch (NullPointerException e){
            return -1;
        }
        MyFile file = getFSHandler().getFiles().get(fd);
        boolean ans = getFSHandler().getCurrentUser().access(file,permission);
        getFSHandler().close(fd);
        return ans?1:0;
    }
}
