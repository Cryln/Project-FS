package cn.geralt.projectFS;

//import cn.geralt.util.ByteIO;

import cn.geralt.util.ByteIO;

import java.io.IOException;
import java.util.Arrays;

public class INode {
    private SuperBlock SBHandler;
    private int iNodeNum;
    private int firstBlock;
    private byte type;
    private byte status;
    private int rawFileLen;

    private int fileNameLen;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    //    static int iNodeAreaOffset = 4096;
//    static int iNodeLen = 256;
    public INode(SuperBlock superBlock ,int iNodeNum) throws IOException {
        this.iNodeNum = iNodeNum;
        this.SBHandler = superBlock;
        initialize();
    }


//    public static INode getInstance(int iNodeNum){
//
//    }

    private void initialize() throws IOException {
        byte[] bytes = FileSystem.getbytes(SBHandler.getiNodeSegOffset()+iNodeNum* SBHandler.getiNodeSize()
                ,SBHandler.getiNodeSize());

        this.type = bytes[0]; //first byte for type
        this.status = bytes[1]; //2nd byte for status
        //next 4 bytes for number of first block
        this.firstBlock = ByteIO.byteArrayToInt(bytes,2);
        //next 4 bytes for length of raw file // x byte
        this.rawFileLen = ByteIO.byteArrayToInt(bytes,6);
        //9th byte for length of filename
        this.fileNameLen = (int)bytes[10];
        byte[] buffer = new byte[this.fileNameLen];
        System.arraycopy(bytes,11,buffer,0,this.fileNameLen);
        this.fileName = new String(buffer);
    }
    public byte[] getFile() throws IOException {
        byte[] buffer = new byte[rawFileLen];
        int restBytes = rawFileLen;
        int nextBlockNum = this.firstBlock;
        while (restBytes > SBHandler.getBlockSize()){
            byte[] temp = FileSystem.getbytes(SBHandler.getDataSegOffset()+nextBlockNum* SBHandler.getBlockSize()
            , SBHandler.getBlockSize());
            System.arraycopy(temp,0,buffer,rawFileLen-restBytes,temp.length-4);
            restBytes -= (temp.length-4);
            nextBlockNum = ByteIO.byteArrayToInt(Arrays.copyOfRange(temp,temp.length-4,temp.length));
        }
        if(restBytes<= SBHandler.getBlockSize() && restBytes!=0){
            byte[] temp = FileSystem.getbytes(SBHandler.getDataSegOffset()+nextBlockNum* SBHandler.getBlockSize()
                    , SBHandler.getBlockSize());
            System.arraycopy(temp,0,buffer,rawFileLen-restBytes,restBytes);
            restBytes -= restBytes;
        }
        return buffer;
    }
}
