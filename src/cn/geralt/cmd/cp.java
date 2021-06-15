package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;
import cn.geralt.projectFS.MyFile;

import java.io.IOException;

public class cp extends Executable{
    public cp(FileSystem fileSystem) {
        super(fileSystem);
        permission0 = 4;
        permission1 = 2;
        type1 = 0;
    }

    @Override
    public int process(String[] args) throws IOException {
        DEntry src = getFSHandler().dir2DEntry(args[0]);
        DEntry dest = getFSHandler().dir2DEntry(args[1]);
        if(src.getiNode().getType()==0){
            getFSHandler().newDir(src.getFileName(),dest);
        }
        else{
            getFSHandler().newFile(src.getFileName(),dest);
        }
        dest = dest.getChild(src.getFileName());

        fun(src,dest);
        return 0;
    }

    private void fun(DEntry src,DEntry dest) throws IOException {
        for (int i = 0; i < src.getChildren().size(); i++) {
            DEntry child = src.getChildren().get(i);
            if(child.getiNode().getType()==0){
                getFSHandler().newDir(child.getFileName(),dest);
            }
            else{
                getFSHandler().newFile(child.getFileName(),dest);
            }
            fun(child,dest.getChildren().get(i));
        }
        getFSHandler().copy(src,dest);
    }

    @Override
    public int preProcess(String[] args) {
        DEntry des0 = getFSHandler().dir2DEntry(args[0]);
        int fd0 = 0;
        try {
            fd0 = getFSHandler().open(des0.getAbsPath());
        }catch (NullPointerException e){
            return -1;
        }

        MyFile file0 = getFSHandler().getFiles().get(fd0);
        boolean ans0 = getFSHandler().getCurrentUser().access(file0, permission0);
        getFSHandler().close(fd0);

        DEntry des1 = getFSHandler().dir2DEntry(args[1]);
        int fd1 = 0;
        try {
            fd1 = getFSHandler().open(des1.getAbsPath());
        }catch (NullPointerException e){
            return -4;
        }
        if(des1.getiNode().getType()!=type1){
            return -3;
        }
        MyFile file1 = getFSHandler().getFiles().get(fd1);
        boolean ans1 = getFSHandler().getCurrentUser().access(file1, permission1);
        getFSHandler().close(fd1);

        return ans0&&ans1?1:0;
    }
}
