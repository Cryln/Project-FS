package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;
import cn.geralt.projectFS.MyFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class input extends Executable{
    public input(FileSystem fileSystem) {
        super(fileSystem);
        permission0 = 2;
    }

    public int process(String[] args){
        DEntry cur = getFSHandler().getCurrent();
        try {
            String[] path = args[0].split("\\\\");
            getFSHandler().newFile(path[path.length-1],cur);
            File file  = new File(args[0]);
            if(!file.exists()){
                System.out.println("file not exists!");
                return -1;
            }
            FileInputStream fis = new FileInputStream(file);
            byte[] data = fis.readAllBytes();
            fis.close();
            int fd = getFSHandler().open(path[path.length-1]);
            FSHandler.write(fd,data,0);
            FSHandler.close(fd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    @Override
    public int preProcess(String[] args) {
        DEntry des = getFSHandler().getCurrent();
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
