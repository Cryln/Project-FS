package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileSystem {
    private String VHDDir;
    private SuperBlock superBlock;
    private boolean isInitialized;
    private DEntry root;
    private DEntry current;
    private Map<Integer,MyFile> files = new HashMap<>();

    public String getVHDDir() {
        return VHDDir;
    }

    public SuperBlock getSuperBlock() {
        return superBlock;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public Map<Integer, MyFile> getFiles() {
        return files;
    }

    public FileSystem(String VHDDir) throws IOException {
        this.VHDDir = VHDDir;
        current = null;
        superBlock = new SuperBlock(this);
        boolean checked = diskCheck(this.VHDDir);
        if(checked){
            this.isInitialized = initialize();
        }
        else{
            format();
            this.isInitialized = initialize();
        }
    }
    private boolean diskCheck(String dir){
        //TODO: disk check

        return false;
    }
    private boolean initialize() throws IOException {
        //TODO: 1.initialize the super block

        root = new DEntry(this,null,superBlock.getRootINode(),getINode(superBlock.getRootINode()));
        System.out.println("root:"+root.getFileName());
        return true;
    }



    public boolean isDirExist(String relDir, DEntry cur){
        String[] path = relDir.split("/");
        for (String s : path) {
            if(cur.isChildExist(s)){
                cur = cur.getChild(s);
            }
            else{
                return false;
            }
        }

        return true;
    }

    public void createFile(String absDir){
        //TODO

    }

    public int open(String dir){
//        if(!isDirExist(dir)){
//            createFile(dir);
//        }
        return 0;
    }

    private void format() throws IOException {
        int[] ints = {31415926,4096,16*1024*1024,256,4096,4096,4096,1052672,3839,0};
        superBlock.format(ints);
        System.out.println("formatted!");
    }

    public INode getINode(int iNodeNum) throws IOException {
        return new INode(superBlock,iNodeNum);
    }

    public static byte[] getbytes(int offset, int len) throws IOException {
        ByteIO byteIO = new ByteIO("src/cn/geralt/util/mydisk.vhd");
        return byteIO.output(offset,len);
    }

    public static void main(String[] args) throws IOException {
        FileSystem fileSystem = new FileSystem("src/cn/geralt/util/mydisk.vhd");
        System.out.println();
    }
}
