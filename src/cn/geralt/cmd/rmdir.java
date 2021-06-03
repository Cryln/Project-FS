package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;

import java.io.IOException;

public class rmdir extends Executable{
    public rmdir(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public int run(String[] args) throws IOException {
        DEntry des = getFSHandler().dir2DEntry(args[0]);
        if(des==null){
            return -1;
        }else{
            des.openDir();
            getFSHandler().delete(des);
        }

        return 0;
    }
}
