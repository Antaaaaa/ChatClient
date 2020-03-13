import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String command = null;
        Auth auth = null;
        Thread th = null;
        MessageThread messageThread= null;

        while(command != "@exit"){
            System.out.print("To see all commands print out: !help\n>>> ");
            command = scanner.nextLine();
            switch(command.trim()){
                case "!help":
                    System.out.println("@auth - register/login\n" +
                            "@users - show all online users\n" +
                            "@status - check user status\n" +
                            "@change_status - change status\n" +
                            "@logout - logout from chat\n" +
                            "@send - send to user letter");
                    break;
            case "@auth":
                {
                    if (auth != null){
                        System.out.println("Please log out");
                    }
                    else{
                    scanner.reset();
                    System.out.print("Login:");
                    String login = scanner.nextLine().trim();
                    scanner.reset();
                    System.out.print("Password: ");
                    String password = scanner.nextLine().trim();
                    auth = new Auth(login,password);
                    int result = auth.send(Utils.getURL() + "/auth");
                        if (result != 200){
                            if (result == 404) {
                                System.out.println("Wrong Password");
                                auth = null;
                            }
                            else System.out.println("Error" + result);
                        }
                        else {
                            System.out.println("Successfully logged in like: " + auth.toString());
                            messageThread = new MessageThread(auth);
                            th = new Thread(messageThread);
                            th.setDaemon(true);
                            th.start();
                        }
                    }
            }
            break;
                case "@users": {
            if (auth != null){
                getUsers();
            }
            else {
                    System.out.println("Please log in");
                }
        }
        break;
                case "@status": {
            if (auth != null) {
                System.out.print("Enter user login >>> ");
                Scanner in = new Scanner(System.in);
                String login = in.nextLine().trim();
                Users list = getUsers();
                for (Auth user: list.getList()){
                    if (user.getLogin().equals(login) && user.getStatus().isStatus()) System.out.print("User " + login + " is +\n");
                    else System.out.println("This user is offline or not registered");
                }
            }
            else{
                System.out.println("Please log in");
                }
            }
        break;
                case "@changestatus":{
                    if (auth != null){
                    int result = auth.send(Utils.getURL() + "/change_status");
                        if (result != 200){
                            System.out.println("Error:" + result);
                            return;
                        }
                        else{ System.out.println("Status changed"); }
                    }
                    else{
                        System.out.println("Please log in");
                    }
                }
        break;
                case "@logout":{
                    if (auth == null) {
                        System.out.println("Please log in");
                    }
                    else {
                        int result = auth.send(Utils.getURL() + "/change_status");
                        auth = null;
                        System.out.println("You are successfully logged out");
                    }
                }
                break;
                case "@send":{
                    Scanner in = new Scanner(System.in);
                    String login = auth.getLogin();
                    System.out.print("Enter a dest login:");
                    in.reset();
                    String to = in.nextLine().trim();
                    System.out.println("Enter a message:");
                    in.reset();
                    String text = in.nextLine();
                    Message message = new Message(text,login,to);
                    message.send(Utils.getURL() + "/message");
                }
                break;
                default:
                    System.out.println("Wrong command\n");
            }
        }
        scanner.close();
    }

    public static Users getUsers() throws IOException {
        URL urlFrom = new URL(Utils.getURL() + "/get");
        HttpURLConnection http = (HttpURLConnection) urlFrom.openConnection();
        Users list = null;
        InputStream is = http.getInputStream();
        try {
            Gson gson = new GsonBuilder().create();
            byte[] buf = Utils.responseBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);
            list = gson.fromJson(strBuf, Users.class);
            if (list != null) {
                System.out.print("Online users in chat: ");
                for (Auth user: list.getList()) {
                    if (user.getStatus().isStatus())
                        System.out.print("[" + user.getLogin() + "]");
                }
                System.out.println();
            }
        } finally {
            is.close();
        }
        return list;
    }
}
