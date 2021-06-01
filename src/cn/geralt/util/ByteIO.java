package cn.geralt.util;

import java.io.*;
import java.util.Arrays;

public class ByteIO {
    private String filedir;
    private int pos;
//    private File file;
//    private FileOutputStream fileOutputStream;
//    private FileInputStream fileInputStream;
    private RandomAccessFile randomAccessFile;
    public ByteIO(String filedir) throws FileNotFoundException {
        this.filedir = filedir;
//        this.file = new File(this.filedir);
//        this.fileInputStream = new FileInputStream(this.file);
//        this.fileOutputStream = new FileOutputStream(this.file);
        this.randomAccessFile = new RandomAccessFile(this.filedir,"rw");
        pos = 0;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void input(String content, int off) throws IOException {
        byte[] bytes =  content.getBytes();
//        this.fileOutputStream.write(bytes,off,len);
        this.randomAccessFile.seek(off);
        this.randomAccessFile.write(bytes,0, bytes.length);
    }
    public void input(byte[] bytes,int off) throws IOException {
        this.randomAccessFile.seek(off);
        this.randomAccessFile.write(bytes,0,bytes.length);
    }
    public byte[] output(int off, int bytenum) throws IOException {
        byte[] bytes = new byte[bytenum];
//        for (int i = 0; i < bytenum; i++) {
//            this.fileInputStream.read(bytes,off,bytenum);
//            this.randomAccessFile.read(bytes,off,bytenum);
//        }
        this.randomAccessFile.seek(off);
        this.randomAccessFile.read(bytes,0,bytenum);
        this.randomAccessFile.readByte();
        return bytes;
    }
    public int nextInt() throws IOException {
        byte[] data = output(pos,4);
        pos += 4;
        return byteArrayToInt(data);
    }

    public byte nextByte() throws IOException {
        byte[] data = output(pos,1);
        pos ++;
        return data[0];
    }

    public void writeInt(int a) throws IOException {
        byte[] data = intToByteArray(a);
        input(data,pos);
        pos += 4;
    }
    public void writeBytes(byte[] a) throws IOException {
        input(a,pos);
        pos += a.length;
    }

    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static int byteArrayToInt(byte[] b,int off){
        return byteArrayToInt(Arrays.copyOfRange(b,off,off+4));
    }

    public static void main(String[] args) throws IOException {

    }
}
