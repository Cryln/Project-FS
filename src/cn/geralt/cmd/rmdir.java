package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;
import cn.geralt.projectFS.MyFile;

import java.io.IOException;

public class rmdir extends Executable{
    public rmdir(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    public int process(String[] args) throws IOException {
        DEntry des = getFSHandler().dir2DEntry(args[0]);
        if(des==null){
            return -1;
        }else{
            des.openDir();
            getFSHandler().delete(des);
        }

        return 0;
    }
    @Override
    public int preProcess(String[] args) {
        try{
            DEntry des = getFSHandler().dir2DEntry(args[0]);
            int fd = getFSHandler().open(des.getAbsPath());
            MyFile file = getFSHandler().getFiles().get(fd);
            boolean ans = getFSHandler().getCurrentUser().access(file,permission);
            getFSHandler().close(fd);
            return ans?1:0;
        }catch (NullPointerException e){
            return 1;
        }
    }
}
