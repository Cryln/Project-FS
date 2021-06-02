package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;

public class cd extends Executable{
    public cd(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public int run(String[] args) {
        DEntry des = getFSHandler().dir2DEntry(args[0]);
        if(des==null){
            return -1;
        }else {
            getFSHandler().setCurrent(des);
        }
        return 0;
    }
}
