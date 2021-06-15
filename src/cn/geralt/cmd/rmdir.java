package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;
import cn.geralt.projectFS.MyFile;

import java.io.IOException;

public class rmdir extends Executable{
    public rmdir(FileSystem fileSystem) {
        super(fileSystem);
        permission0 = 2;
        permission1 = 2;
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

        DEntry des = getFSHandler().dir2DEntry(args[0]);
        int fd = 0;
        try {
            fd = getFSHandler().open(des.getAbsPath());
        }catch (NullPointerException e){
            return -4;
        }
        MyFile file = getFSHandler().getFiles().get(fd);
        boolean ans = getFSHandler().getCurrentUser().access(file, permission0);
        getFSHandler().close(fd);


        return ans?1:0;

    }
}
