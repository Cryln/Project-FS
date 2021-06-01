package cn.geralt.projectFS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Login {
    static final int MAX = 8;
    public static void main(String[] args) {
        while (true) {
            System.out.println("***************正在登录。。。。。。******************");
            Scanner scanner = new Scanner(System.in);
            System.out.println("输入用户名：");
            String user = scanner.nextLine();
            System.out.println("输入密码：");
            String key = scanner.nextLine();
            if (check(user, key,0)==true) {
                System.out.println("登录成功！");
                break;
            }
            else System.out.println("登录失败！");
        }
        MainWindow mainWindow = new MainWindow();
        mainWindow.myWindow();
    }

    public static boolean check(String user,String key,int sign){
        //sign,标记，用于识别“登录”和“注册”操作，0表示“登录”，1表示“注册”
        File file = new File("E:\\Code\\Java\\Project-FS\\src\\cn\\geralt\\util\\user");
        int line = 1;//读取行数，即已存在的用户数量
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                //System.out.println("line " + line + ": " + tempString);
                String[] ls=tempString.split(" ");
                if(user.equals(ls[0]) ){
                    if(sign==0 && key.equals(ls[1])){
                    System.out.println(String.format("登录成功，当前用户为：%s",user));
                    return true;
                    }
                    else{
                        System.out.println(String.format("用户%s已存在",user));
                        return false;
                    }
                }
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(line<8 && sign==1){
            return true;//少于八个用户
        }
        return false;
    }
}

class MainWindow{
    public static void main(String[] args) {

    }
    public static void myWindow() {
        System.out.println("********************************************************");
        System.out.println("\t\t1.help\t\t");
        System.out.println("\t\t2.wtf\t\t");
        System.out.println("********************************************************");

        while (true){
            System.out.println("输入操作！");
            Scanner scanner = new Scanner(System.in);
            String option = scanner.nextLine();
            switch (option)
            {
                case "1":;
                case "2":;
            }
        }

    }
}

class Create_User {
    private String key;
    private String name;

    public Create_User(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}