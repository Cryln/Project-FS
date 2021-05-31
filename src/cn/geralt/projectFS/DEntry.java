package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DEntry {
    private DEntry parent;
    private ArrayList<DEntry> children;
    private int iNodeNum;
    private INode iNode;
//    private byte[] rawFile;
    private String fileName;
    private FileSystem FSHandler;

    public DEntry getParent() {
        return parent;
    }

    public ArrayList<DEntry> getChildren() {
        return children;
    }

    public int getiNodeNum() {
        return iNodeNum;
    }

    public INode getiNode() {
        return iNode;
    }

//    public byte[] getRawFile() {
//        return rawFile;
//    }

    public String getFileName() {
        return fileName;
    }

    public DEntry(FileSystem fileSystem, DEntry parent, int iNodeNum, INode iNode) throws IOException {
        this.FSHandler  = fileSystem;
        this.parent = parent;
        this.iNodeNum = iNodeNum;
        this.iNode = iNode;
        this.fileName = this.iNode.getFileName();
    }
//    public byte[] openFile() throws IOException {
//        //TODO: open file
//        rawFile = iNode.getFile();
//        return rawFile;
//    }
    public void openDir() throws IOException {
        //将当前目录的子项目加载未Dentry，扩展文件树
        byte[] rawFile = iNode.getFile();
        int numOfItems =  ByteIO.byteArrayToInt(Arrays.copyOfRange(rawFile,0,4));
        for (int i = 0; i < numOfItems; i++) {
            int iNodeNum = ByteIO.byteArrayToInt(Arrays.copyOfRange(rawFile,4*(i+1),4*(i+2)));
            DEntry dEntry = new DEntry(FSHandler,this,iNodeNum,FSHandler.getINode(iNodeNum));
            this.children.add(dEntry);
        }
    }
    public String getPath(){
        DEntry temp = parent;
        StringBuilder path = new StringBuilder("/");
        while(temp!=null){
            StringBuilder sb = new StringBuilder();
            sb.append(temp.getFileName()+"/");
            sb.append(path);
            path = sb;
            temp = temp.getParent();
        }
        return temp.toString();
    }

    public DEntry getChild(String childName){
        for (DEntry child : children) {
            if(child.getFileName().equals(childName)){
                return child;
            }
        }
        return null;
    }

    public boolean isChildExist(String childName){
        for (DEntry child : getChildren()) {
            if(child.getFileName().equals(childName))
                return true;
        }
        return false;
    }
}
