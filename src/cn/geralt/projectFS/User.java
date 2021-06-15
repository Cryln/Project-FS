package cn.geralt.projectFS;

import java.io.IOException;
import java.util.Scanner;

public class User {
    private int uid;
    private String userName;
    private boolean isLoggedIn = false;
    private FileSystem FS;

    public User(FileSystem fileSystem){
        FS = fileSystem;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    private int identify(MyFile file){
        int fileOwner = file.getOwner();
        int ans = fileOwner^uid;
        if(ans==0){
            return 6;
        }
        else if(ans<0x10000){
            return 3;
        }
        else return 0;
    }

    public int getUid() {
        return uid;
    }

    public String getUserName() {
        return userName;
    }

    public boolean canRead(MyFile file){ //r2 w1 x0
        int ans = (file.getMode()>>(identify(file)+2));
        return (ans&1)==1;
    }
    public boolean canWrite(MyFile file){
        int ans = (file.getMode()>>(identify(file)+1));
        return (ans&1)==1;
    }
    public boolean canExecute(MyFile file){
        int ans = (file.getMode()>>(identify(file)));
        return (ans&1)==1;
    }
    public boolean access(MyFile file,int permission){
        int ans = (file.getMode()>>(identify(file)));
        return (ans&permission)==permission;
    }

    public void login(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("username: ");
        String s = scanner.nextLine();
        if(!FS.getUser2uid().containsKey(s)){
            System.out.println("not such a user!");
            isLoggedIn = false;
        }else{
            System.out.println("welcome!");
            this.uid = FS.getUser2uid().get(s);
            this.userName = s;
            isLoggedIn = true;
        }
    }
    public void signUp() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("group(int, 0 for root): ");
        int gid = Integer.parseInt(scanner.nextLine());
        System.out.print("\nusername: ");
        String s = "";
        while(s.equals(""))s = scanner.nextLine();
        int uid=0;
        for (int i = 0; i < FS.getUser2uid().size()+1; i++) {
            if(!FS.getUser2uid().containsValue(((gid << 16) | i))){
                uid = ((gid << 16) | i);
                break;
            }
        }
        FS.getUser2uid().put(s,uid);
        FS.saveUsers();
    }
}
