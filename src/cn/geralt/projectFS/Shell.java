package cn.geralt.projectFS;

import cn.geralt.cmd.Executable;

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
    }

    public void run() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        while (true){
            String line = scanner.nextLine().strip();
            String[] cmd = line.split("\\s+");
            Class clazz = Class.forName("cn.geralt.cmd."+cmd[0]);
            Executable exec = (Executable)clazz.getConstructor(FileSystem.class).newInstance(this.fileSystem);
            exec.run(Arrays.copyOfRange(cmd,1,cmd.length));
            System.out.println();
        }
    }

}
