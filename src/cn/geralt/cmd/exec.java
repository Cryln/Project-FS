package cn.geralt.cmd;

import cn.geralt.projectFS.FileSystem;

public class exec extends Executable{
    public exec(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public int run(String[] args) { //exec notepad test.java
        int fd = getFSHandler().open(args[2]);


        return 0;
    }
}
