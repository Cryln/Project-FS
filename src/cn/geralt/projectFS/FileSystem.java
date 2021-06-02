package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class FileSystem {
    private String VHDDir;
    private SuperBlock superBlock;
    private boolean isInitialized;
    private DEntry root;
    private DEntry current;
    private Map<Integer,MyFile> files = new HashMap<>();
    private byte[] iNodeMap;
    private byte[] blockMap;
    private Stack<DEntry> path = new Stack<>();

    public String getVHDDir() {
        return VHDDir;
    }

    public DEntry getCurrent() {
        return current;
    }

    public void setCurrent(DEntry current) {
        this.current = current;
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

        iNodeMap = superBlock.getINodeMap();
        blockMap = superBlock.getBlockMap();

        current = root;
        System.out.println("root:"+root.getFileName());
        return true;
    }

    public int[] getUnusedNum(byte[] bytes,int amount){
        int[] nums = new int[amount];
        int num = 0;
        int index = 0;
        for (byte aByte : bytes) {
            for (int i = 0; i < 8 ; i++) {
                if((aByte&(byte)0b10000000) == 0){
                    nums[index] = num;
                    index++;
                    if(index==amount) return nums;
                }
                num++;
                aByte = (byte)(aByte<<1);
            }
        }
        return null;
    }

    public void setMapBit(byte[] bytes,int pos,boolean bit){
        int a = pos/8;
        int b = pos%8;
        byte temp = bytes[a];
        byte mask = (byte)(0b10000000 >> b);
        if((bit&&((byte)(temp&mask)!=0))||(!bit&&((byte)(temp&mask)==0))){

        }
        else {
            temp = (byte) (temp ^ mask);
            bytes[a] = temp;
        }
    }
//
//    public boolean isDirExist(String relDir, DEntry cur){
//        String[] path = relDir.split("/");
//        for (String s : path) {
//            if(cur.isChildExist(s)){
//                cur = cur.getChild(s);
//            }
//            else{
//                return false;
//            }
//        }
//
//        return true;
//    }

    private int getFD(){
        for (int i = 0; i < files.size()+1; i++) {
            if(!files.containsKey(i)){
                return i;
            }
        }
        return -1;
    }

    public int open(String dir){
        DEntry temp = dir2DEntry(dir);
        if(temp==null){
            return -1;
        }
        else{
            int fd = getFD();
            MyFile myFile = MyFile.getInstance(temp,fd);
            files.put(fd,myFile);
            return fd;
        }
    }

    private int close(int fd){
        files.remove(fd);
        return 1;
    }

    public int read(int fd,byte[] des,int off,int len) throws IOException {
        MyFile file = files.get(fd);
        byte[] data = file.read();
        System.arraycopy(data,off,des,0,len);
        return len;
    }

    public void write(int fd,byte[] data,int off) throws IOException {
        MyFile file = files.get(fd);
        int[] res;
        res = file.write(data,off,null);

        while(true){
            switch (res[0]) {
                case 0://块不足
                    res = file.write(data, off, getUnusedNum(blockMap, res[1]));
                    break;
                case 1://成功
                    return;
                case 2://分配成功，返回实际使用的block;
                    for (int i = 1; i < res.length; i++) {
                        setMapBit(blockMap, res[i], true);
                    }
                    return;
                case 3://有剩余block，返回
                    for (int i = 1; i < res.length; i++) {
                        setMapBit(blockMap, res[i], false);
                    }
                    return;
            }
        }
    }

    private void format() throws IOException {
        int[] ints = {31415926,4096,16*1024*1024,256,4096,4096,4096,1052672,3837,0,48,560};
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

    public DEntry dir2DEntry(String dir){
        String[] path = dir.strip().split("/");
        DEntry start;
        if(path[0].equals("")){
            String[] temp = new String[path.length-1];
            System.arraycopy(path,1,temp,0,path.length-1);
            start = root;
            path = temp;
        }
        else{
            start = current;
        }

        for (String s : path) {
            DEntry child = start.getChild(s);
            if(s==null){
                System.out.printf("not such a directory");
                return null;
            }
            else {
                start = child;
            }
        }
        return start;
    }

    public DEntry newDir(String dirName,DEntry parent) throws IOException {
        int[] inodeNum =  getUnusedNum(iNodeMap,1);
        int[] blockNum = getUnusedNum(blockMap,1);
        INode.initInode(VHDDir,dirName,0,superBlock.getiNodeSegOffset()+inodeNum[0]*superBlock.getiNodeSize(),blockNum[0]);
        setMapBit(iNodeMap,inodeNum[0],true);
        setMapBit(blockMap,blockNum[0],true);
        INode iNode = new INode(superBlock,inodeNum[0]);
        DEntry dEntry = new DEntry(this,parent,inodeNum[0],iNode);
        parent.getChildren().add(dEntry);

        return dEntry;
    }

    public DEntry newFile(String fileName,DEntry parent) throws IOException {
        int[] inodeNum =  getUnusedNum(iNodeMap,1);
        int[] blockNum = getUnusedNum(blockMap,1);
        INode.initInode(VHDDir,fileName,1,superBlock.getiNodeSegOffset()+inodeNum[0]*superBlock.getiNodeSize(),blockNum[0]);
        setMapBit(iNodeMap,inodeNum[0],true);
        setMapBit(blockMap,blockNum[0],true);
        INode iNode = new INode(superBlock,inodeNum[0]);
        DEntry dEntry = new DEntry(this,parent,inodeNum[0],iNode);
        parent.getChildren().add(dEntry);

        return dEntry;
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        FileSystem fileSystem = new FileSystem("src/cn/geralt/util/mydisk.vhd");
        Shell shell = new Shell(fileSystem);
        shell.run();
        System.out.println();
    }
}
