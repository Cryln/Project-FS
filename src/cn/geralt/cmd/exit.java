package cn.geralt.cmd;

import cn.geralt.projectFS.FileSystem;

import java.io.IOException;

public class exit extends Executable{

    public exit(FileSystem fileSystem) {
        super(fileSystem);
        this.permission0 = 0;
    }

    @Override
    public int process(String[] args) throws IOException {
        getFSHandler().initialize();
        return 0;
    }
}
