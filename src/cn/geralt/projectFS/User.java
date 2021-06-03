package cn.geralt.projectFS;

public class User {
    private int uid;
    private String userName;
    private boolean isLoggedIn = false;

    public boolean canRead(MyFile file){
        return true;
    }
    public boolean canWrite(MyFile file){
        return true;
    }
    public boolean canExecute(MyFile file){
        return true;
    }
}
