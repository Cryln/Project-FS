package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;
import cn.geralt.projectFS.MyFile;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class ls extends Executable{

    public ls(FileSystem fileSystem) {
        super(fileSystem);
        permission0 = 4;
        type0 = 0;
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
                int filesize = child.getiNode().getRawFileLen();
                if(child.getiNode().getType()==0){
                    System.out.print(String.format("\u001b[34m"));
                    System.out.println(String.format("%s\t\t%s\t\t%s\t\t%d\t\t%s",child.getFileName(),user,sb,filesize/4-1,date));
                }
                else {
                    StringBuilder fileLen = new StringBuilder();
                    String[] tag = new String[]{"B","KB","MB","GB"};
                    for (int i = 0; i < tag.length; i++) {
                        if(filesize>1024){
                            filesize /= 1024;
                        }
                        else {
                            fileLen.append(filesize).append(tag[i]);
                            break;
                        }
                    }
                    System.out.println(String.format("%s\t\t%s\t\t%s\t\t%s\t\t%s", child.getFileName(), user, sb, fileLen, date));
                }
                System.out.print(String.format("\u001b[0m"));
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
            return -4;
        }
        if(des.getiNode().getType()!=type0){
            return -3;
        }
        MyFile file = getFSHandler().getFiles().get(fd);
        boolean ans = getFSHandler().getCurrentUser().access(file, permission0);
        getFSHandler().close(fd);
        return ans?1:0;
    }
}
