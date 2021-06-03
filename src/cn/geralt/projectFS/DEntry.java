package cn.geralt.projectFS;

import cn.geralt.util.ByteIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DEntry {
    private DEntry parent;
    private ArrayList<DEntry> children = new ArrayList<>();
    private int iNodeNum;
    private INode iNode;
//    private byte[] rawFile;
    private String fileName;
    private FileSystem FSHandler;
    private boolean isLoaded=false;
    private String absPath;

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

    public String getAbsPath(){
        List<String> l = new ArrayList<>();
        DEntry temp = this;
        while (!temp.getFileName().equals("")){
            l.add(temp.getFileName());
            temp = temp.getParent();
        }
        l.add(temp.getFileName());
        StringBuilder sb = new StringBuilder();
        for (int i = l.size() - 1; i >= 0; i--) {
            sb.append(l.get(i));
            sb.append("/");
        }
        return sb.toString();
    }

    public DEntry(FileSystem fileSystem, DEntry parent, int iNodeNum, INode iNode) throws IOException {
        this.FSHandler  = fileSystem;
        if(parent==null)
            this.parent = this;
        else this.parent = parent;
        this.iNodeNum = iNodeNum;
        this.iNode = iNode;
        this.fileName = this.iNode.getFileName();
        //TODO: can be better
        if(iNode.getType()==1)
            isLoaded=true;
        openDir();
//        children.add()
    }
    public byte[] read() throws IOException {

//        rawFile = iNode.getFile();
        return iNode.read();
    }

    public int[] write(byte[] bytes,int off,int[] additions) throws IOException {
        int[] ans = iNode.write(bytes,off,additions);
        iNode.update();
        return ans;
    }
    public void openDir() throws IOException {
        //将当前目录的子项目加载未Dentry，扩展文件树
        if(!isLoaded){
            byte[] rawFile = iNode.read();
            int numOfItems = ByteIO.byteArrayToInt(Arrays.copyOfRange(rawFile, 0, 4));

            if(numOfItems*4+4!=rawFile.length){
                System.out.println(getFileName()+":something wrong! in the dir file");
                return;
            }

            for (int i = 0; i < numOfItems; i++) {
                int iNodeNum = ByteIO.byteArrayToInt(Arrays.copyOfRange(rawFile, 4 * (i + 1), 4 * (i + 2)));
                DEntry dEntry = new DEntry(FSHandler, this, iNodeNum, FSHandler.getINode(iNodeNum));
                this.children.add(dEntry);
            }
            isLoaded = true;
        }
    }

    public List<ArrayList<Integer>> delete() throws IOException {
        List<ArrayList<Integer>> ans = new ArrayList<ArrayList<Integer>>();
        ArrayList ans0 = new ArrayList<Integer>();
        ArrayList ans1 = new ArrayList<Integer>();
        ans.add(ans0);
        ans.add(ans1);

        for (DEntry child : getChildren()) {
            List<ArrayList<Integer>> temp = child.delete();
            for (Integer integer : temp.get(0)) {
                ans0.add(integer);
            }
            for (Integer integer : temp.get(1)) {
                ans1.add(integer);
            }
        }
        ans0.add(iNodeNum);
        for (int i : iNode.getAllBlockNum()) {
            ans1.add(i);
        }

        return ans;
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
        if(childName.equals("..")){
            return parent;
        }
        else if(childName.equals(".")){
            return this;
        }
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
