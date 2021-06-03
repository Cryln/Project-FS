package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;
import cn.geralt.projectFS.MyFile;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class ls extends Executable{

    public ls(FileSystem fileSystem) {
        super(fileSystem);
        permission = 4;
    }

    @Override
    public int process(String[] args) throws IOException {
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
                String user = FSHandler.getUid2user().get(child.getiNode().getUid());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(child.getiNode().getLastModifyTime());
                int m = child.getiNode().getMode();
                StringBuilder sb = new StringBuilder();
                int mask = 0b100000000;
                String[] tags = new String[]{"r","w","x"};
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if((mask&m)==0){
                            sb.append("-");
                        }
                        else sb.append(tags[j]);
                        mask = (mask>>1);
                    }
                    sb.append("|");
                }
                System.out.println(String.format("%s\t\t%s\t\t%s\t\t%s",child.getFileName(),user,sb,date));
            }
        }
        return 0;
    }
    @Override
    public int preProcess(String[] args) {
        if(args.length==0){
            args = new String[]{"."};
        }
        DEntry des =  getFSHandler().dir2DEntry(args[0]);
        int fd = 0;
        try {
            fd = getFSHandler().open(des.getAbsPath());
        }catch (NullPointerException e){
            return -1;
        }
        MyFile file = getFSHandler().getFiles().get(fd);
        boolean ans = getFSHandler().getCurrentUser().access(file,permission);
        getFSHandler().close(fd);
        return ans?1:0;
    }
}
