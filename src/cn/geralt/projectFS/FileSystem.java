package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FileSystem {
//    private String VHDDir;
    private SuperBlock superBlock;
    private boolean isInitialized;
    private DEntry root;
    private DEntry current;
    private Map<Integer,MyFile> files = new HashMap<>();
    private byte[] iNodeMap;
    private byte[] blockMap;
    private User currentUser;
    private Map<String,Integer> user2uid;
    private Map<Integer,String> uid2user=null;

//    public String getVHDDir() {
//        return VHDDir;
//    }


    public Map<String, Integer> getUser2uid() {
        return user2uid;
    }

    public Map<Integer, String> getUid2user() {
        return uid2user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public DEntry getCurrent() {
        return current;
    }

    public void setCurrent(DEntry current) {
        this.current = current;
    }

    public String getAbsPath() {
        if(current==null) return "";
        return current.getAbsPath();
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

    public FileSystem() throws IOException {
//        this.VHDDir = VHDDir;
        current = null;
        superBlock = new SuperBlock(this);
        boolean checked = diskCheck();
        if(checked){
            this.isInitialized = initialize();
        }
        else{
            format();
            this.isInitialized = initialize();
        }
    }
    private boolean diskCheck() throws IOException {
        //TODO: disk check
        ByteIO byteIO = ByteIO.getInstance();
        byteIO.setPos(0);
        if(byteIO.nextInt()!=31415926){
            return false;
        }
        else
            return true;
    }
    public boolean initialize() throws IOException {

        user2uid = superBlock.getUsers();
        uid2user = new HashMap<>();
        for (String s : user2uid.keySet()) {
            int uid = user2uid.get(s);
            uid2user.put(uid,s);
        }
        currentUser = new User(this);
        currentUser.login();
        if(!currentUser.isLoggedIn()){
            currentUser.signUp();
            return initialize();
        }
        root = new DEntry(this,null,superBlock.getRootINode(),getINode(superBlock.getRootINode()));
        current =root;
        iNodeMap = superBlock.getINodeMap();
        blockMap = superBlock.getBlockMap();

//        System.out.println("root:"+root.getFileName());
        return true;
    }

    public void saveUsers() throws IOException {
        superBlock.saveUsers();
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

    public void setMapBit(byte[] bytes,int pos,boolean bit) throws IOException {
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
        byte[] data = new byte[iNodeMap.length+ blockMap.length];
        System.arraycopy(iNodeMap,0,data,0,iNodeMap.length);
        System.arraycopy(blockMap,0,data,iNodeMap.length,blockMap.length);
        ByteIO byteIO = ByteIO.getInstance();
        byteIO.input(data,superBlock.getiNodeMapOffset());

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
            MyFile myFile = new MyFile(temp,fd);
            files.put(fd,myFile);
            return fd;
        }
    }

    public int close(int fd){
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
                case -1:
                    System.out.println("error occurred during writing!");
                    return;
            }
        }
    }

    private void format() throws IOException {
        int[] ints = {31415926,4096,16*1024*1024,256,4096,4096,4096,1052672,3837,0,52,564,1076};
        superBlock.format(ints);
        System.out.println("formatted!");
    }

    public INode getINode(int iNodeNum) throws IOException {
        return new INode(superBlock,iNodeNum);
    }

    public static byte[] getbytes(int offset, int len) throws IOException {
        ByteIO byteIO = ByteIO.getInstance();
        return byteIO.output(offset,len);
    }

    public DEntry dir2DEntry(String dir){
        String[] path = dir.strip().split("/");
        DEntry start = null;
        int index = 0;
        if(path.length==0){
            return root;
        }
        else{
            switch (path[0]) {
                case "": start=root; index=1;break;
                case ".": start=current; index=1;break;
                case "..": start=current.getParent(); index=1;break;
                default: start = current;break;
            }

        }

        for (;index<path.length;index++) {
            DEntry child = start.getChild(path[index]);
            if(path[index]==null){
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
        for (DEntry child : parent.getChildren()) {
            if (dirName.equals(child.getFileName())){
                System.out.println(dirName+" already exists!");
                return null;
            }
        }

        int[] inodeNum =  getUnusedNum(iNodeMap,1);
        int[] blockNum = getUnusedNum(blockMap,1);
        INode.initInode(dirName,0,superBlock.getiNodeSegOffset()+inodeNum[0]*superBlock.getiNodeSize(),blockNum[0],currentUser);
        //initialize the block
        ByteIO byteIO = ByteIO.getInstance();
        byteIO.setPos(blockNum[0]*superBlock.getBlockSize()+superBlock.getDataSegOffset());
        byteIO.writeInt(0);

        setMapBit(iNodeMap,inodeNum[0],true);
        setMapBit(blockMap,blockNum[0],true);
        INode iNode = new INode(superBlock,inodeNum[0]);
        DEntry dEntry = new DEntry(this,parent,inodeNum[0],iNode);
        parent.getChildren().add(dEntry);
        //new child need to be write in dir block
        int fd = open("../"+ parent.getFileName());
//        byte[] data = ByteIO.intToByteArray(parent.getChildren().size());
//        write(fd,data,0);
//        data = ByteIO.intToByteArray(inodeNum[0]);
//        write(fd,data,parent.getiNode().getRawFileLen());
        byte[] temp = parent.getiNode().read();
        byte[] data = new byte[temp.length+4];
        System.arraycopy(temp,0,data,0,temp.length);
        temp = ByteIO.intToByteArray(parent.getChildren().size());
        System.arraycopy(temp,0,data,0,temp.length);
        temp = ByteIO.intToByteArray(inodeNum[0]);
        System.arraycopy(temp,0,data,data.length-4,4);
        write(fd,data,0);

        close(fd);

        return dEntry;
    }

    public DEntry newFile(String fileName,DEntry parent) throws IOException {
        for (DEntry child : parent.getChildren()) {
            if (fileName.equals(child.getFileName())){
                System.out.println(fileName+" already exists!");
                return null;
            }
        }
        int[] inodeNum =  getUnusedNum(iNodeMap,1);
        int[] blockNum = getUnusedNum(blockMap,1);
        INode.initInode(fileName,1,superBlock.getiNodeSegOffset()+inodeNum[0]*superBlock.getiNodeSize(),blockNum[0],currentUser);
        //initialize the block
        ByteIO byteIO = ByteIO.getInstance();
        byteIO.setPos(blockNum[0]*superBlock.getBlockSize()+superBlock.getDataSegOffset());
        byteIO.writeInt(0);

        setMapBit(iNodeMap,inodeNum[0],true);
        setMapBit(blockMap,blockNum[0],true);
        INode iNode = new INode(superBlock,inodeNum[0]);
        DEntry dEntry = new DEntry(this,parent,inodeNum[0],iNode);
        parent.getChildren().add(dEntry);
        //new child need to be write in dir block
        int fd = open("../"+ parent.getFileName());
//        byte[] data = ByteIO.intToByteArray(parent.getChildren().size());
//        write(fd,data,0);
//        data = ByteIO.intToByteArray(inodeNum[0]);
//        write(fd,data,parent.getiNode().getRawFileLen());
        byte[] temp =parent.getiNode().read();
        byte[] data = new byte[temp.length+4];
        System.arraycopy(temp,0,data,0,temp.length);
        temp = ByteIO.intToByteArray(parent.getChildren().size());
        System.arraycopy(temp,0,data,0,temp.length);
        temp = ByteIO.intToByteArray(inodeNum[0]);
        System.arraycopy(temp,0,data,data.length-4,4);
        write(fd,data,0);

        close(fd);

        return dEntry;
    }

    public void delete(DEntry dEntry) throws IOException {
        List<ArrayList<Integer>> ans = dEntry.delete();
        for (Integer integer : ans.get(0)) {
            setMapBit(iNodeMap,integer,false);
        }
        for (Integer integer : ans.get(1)) {
            setMapBit(blockMap,integer,false);
        }
        DEntry parent = dEntry.getParent();
        parent.getChildren().remove(dEntry);

        int fd = open("../"+ dEntry.getParent().getFileName());
        int index = 0;
        byte[] data = new byte[parent.getChildren().size()*4+4];
        byte[] temp = ByteIO.intToByteArray(parent.getChildren().size());
        System.arraycopy(temp,0,data,index,temp.length);
        index += 4;
        for (DEntry child : parent.getChildren()) {
            temp = ByteIO.intToByteArray(child.getiNodeNum());
            System.arraycopy(temp,0,data,index,temp.length);
            index += 4;
        }
        write(fd,data,0);

        close(fd);
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        FileSystem fileSystem = new FileSystem();
        Shell shell = new Shell(fileSystem);
        shell.run();
        System.out.println();
    }
}
