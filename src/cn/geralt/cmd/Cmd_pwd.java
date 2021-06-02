package cn.geralt.cmd;

import cn.geralt.projectFS.FileSystem;

public class Cmd_pwd extends Executable{
    public Cmd_pwd(FileSystem FSHandler) {
        super(FSHandler);
    }

    @Override
    public int run(String[] args) {

        System.out.println();
        return 1;
    }
}
