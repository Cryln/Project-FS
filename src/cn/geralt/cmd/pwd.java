package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;

import java.io.IOException;
import java.util.Stack;

public class pwd extends Executable{
    public pwd(FileSystem fileSystem) {
        super(fileSystem);
        permission = 0;
    }

    @Override
    public int process(String[] args) throws IOException {

        System.out.print(getFSHandler().getAbsPath()+"\n");

        return 0;
    }
}
