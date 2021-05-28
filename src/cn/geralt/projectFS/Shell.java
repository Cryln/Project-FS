package cn.geralt.projectFS;

public class Shell {
    private FileSystem fileSystem;

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    private void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    public Shell(FileSystem fileSystem){
        setFileSystem(fileSystem);
    }


}
