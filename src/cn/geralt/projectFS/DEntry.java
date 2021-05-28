package cn.geralt.projectFS;

import java.io.IOException;
import java.util.ArrayList;

public class DEntry {
    private DEntry parent;
    private ArrayList<DEntry> children;
    private int iNodeNum;
    private INode iNode;
    private byte[] rawFile;

    public DEntry(DEntry parent, int iNodeNum) {
        if(parent==null){
            this.parent = this;
        }else
            this.parent = parent;
        this.iNodeNum = iNodeNum;
    }
    public void open() throws IOException {
        this.iNode = new INode(this.iNodeNum);
        rawFile = iNode.getFile();
    }
}
