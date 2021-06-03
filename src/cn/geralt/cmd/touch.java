package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;

import java.io.IOException;

public class touch extends Executable{
    public touch(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public int run(String[] args){
        DEntry cur = getFSHandler().getCurrent();
        try {
            getFSHandler().newFile(args[0],cur);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
