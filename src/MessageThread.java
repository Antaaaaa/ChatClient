import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MessageThread implements Runnable{
    private final Gson gson;
    private JsonMessages jsonMessages;
    private Auth auth;
    private boolean exit;

    public MessageThread(Auth auth){
     gson = new GsonBuilder().create();
     jsonMessages = new JsonMessages();
     this.auth = auth;
     exit = false;
    }
    @Override
    public void run() {

        while (!exit) {
            try {
               URL url = new URL(Utils.getURL() + "/getmessage");
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                InputStream is = http.getInputStream();
                try {
                    byte[] buf = Utils.responseBodyToArray(is);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);
                    JsonMessages json = JsonMessages.fromJSON(strBuf);
                    for(Message message : json.getList()){
                        if (message != null && !jsonMessages.containsMessage(message) && message.getTo().equals(auth.getLogin()) && auth.getStatus().isStatus())
                        {
                        System.out.print("getting new message\nNew message(" + message.toString() + ")\n>>>");
                        jsonMessages.getList().add(message);
                        }
                    }
                }
                finally{
                    is.close();
                }
                Thread.sleep(3000);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void stop(){
        exit = true;
    }
    public void start(){
        exit = false;
    }
    public boolean isExit(){
        return exit;
    }
}
