package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;

import java.io.IOException;

public class ls extends Executable{

    public ls(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public int run(String[] args) throws IOException {
        if(args.length==0){
            args = new String[]{"."};
        }
        DEntry des =  getFSHandler().dir2DEntry(args[0]);
        if(des==null){
            return -1;
        }
        else{
            des.openDir();
            for (DEntry child : des.getChildren()) {
                System.out.println(child.getFileName());
            }
        }
        return 0;
    }
}
