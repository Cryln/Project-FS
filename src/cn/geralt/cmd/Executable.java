package cn.geralt.cmd;

import cn.geralt.projectFS.FileSystem;

import java.io.IOException;

public abstract class Executable {
    protected FileSystem FSHandler;

    protected FileSystem getFSHandler(){
        return this.FSHandler;
    }
    public Executable(FileSystem fileSystem){
        this.FSHandler = fileSystem;
    }

    public abstract int run(String[] args) throws IOException;
}
