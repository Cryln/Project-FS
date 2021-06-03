package cn.geralt.projectFS;

import cn.geralt.cmd.Executable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Scanner;

public class Shell {
    private FileSystem fileSystem;
    private Scanner scanner = new Scanner(System.in);

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    private void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    public Shell(FileSystem fileSystem){
        setFileSystem(fileSystem);
//        fileSystem.setCurrent(fileSystem.);
    }

    public void run() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException, IOException {
        while (true){
            System.out.print(getFileSystem().getAbsPath()+">");
            String line = scanner.nextLine().strip();
            String[] cmd = line.split("\\s+");
            if(cmd.length==0)
                break;
            Class clazz = null;
            try {
                clazz = Class.forName("cn.geralt.cmd." + cmd[0]);
            }catch (ClassNotFoundException e){
                System.out.println("no such a cmd!");
                break;
            }
            Executable exec = (Executable)clazz.getConstructor(FileSystem.class).newInstance(this.fileSystem);
            exec.run(Arrays.copyOfRange(cmd,1,cmd.length));
            System.out.println();
        }
    }
}
