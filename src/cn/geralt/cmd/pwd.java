package cn.geralt.cmd;

import cn.geralt.projectFS.FileSystem;

import java.io.IOException;

public class pwd extends Executable{
    public pwd(FileSystem fileSystem) {
        super(fileSystem);
        permission0 = 0;
    }

    @Override
    public int process(String[] args) throws IOException {

        System.out.print(getFSHandler().getAbsPath()+"\n");

        return 0;
    }
}
