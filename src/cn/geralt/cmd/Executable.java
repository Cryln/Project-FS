package cn.geralt.cmd;

import cn.geralt.projectFS.DEntry;
import cn.geralt.projectFS.FileSystem;

import java.io.IOException;

public abstract class Executable {
    protected FileSystem FSHandler;
    protected int permission=0;

    protected FileSystem getFSHandler(){
        return this.FSHandler;
    }
    public Executable(FileSystem fileSystem){
        this.FSHandler = fileSystem;
    }

    public abstract int process(String[] args) throws IOException;

    public int preProcess(String[] args){
        //TODO
        return 1;
    }

    public int postProcess(int a){
        switch (a) {
            case -1:
                System.out.println("no such a file!");break;
        }
        return a;
    }
    public int run(String[] args) throws IOException {
//        if(preProcess(args)==1){
//            postProcess(process(args));
//            return 0;
//        }
//        else if(){
//
//        }
//        else{
//            System.out.println("Permission denied!");
//            return -1;
//        }
        switch (preProcess(args)) {
            case 1:postProcess(process(args));
                    return 0;
            case 0:System.out.println("Permission denied!");
                return -1;
            case -1:System.out.println("no such a file!");
                return -1;
        }
        return 0;
    }

//    public int run(int permissin, String[] args) throws IOException {
//        int preAns = preProcess(permissin);
//
//    }
//    public int preProcess(int permission){
//
//    }
//    public int process(){
//
//    }
//    public int postProcess(){
//
//    }
}
