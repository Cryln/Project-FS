package cn.geralt.cmd;

import cn.geralt.projectFS.FileSystem;

public class Cmd_pwd implements Executable{
    private FileSystem FSHandler;

    public Cmd_pwd(FileSystem FSHandler) {
        this.FSHandler = FSHandler;
    }

    @Override
    public int run() {

        System.out.println();
        return 1;
    }
}
