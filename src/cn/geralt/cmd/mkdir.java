package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;

import java.io.IOException;

public class mkdir extends Executable{
    public mkdir(FileSystem fileSystem){
        super(fileSystem);
    }

    @Override
    public int run(String[] args) {
        DEntry cur = getFSHandler().getCurrent();
        try {
            getFSHandler().newDir(args[0],cur);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
