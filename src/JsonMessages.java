import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class JsonMessages {
    private List<Message> list = new ArrayList<>();

    public List<Message> getList() {
        return list;
    }
    public boolean containsMessage(Message someMessage){
        for (Message message : list){
            if (message.getTo().equals(someMessage.getTo()) && message.getFrom().equals(someMessage.getFrom())
                && message.getMessage().equals(someMessage.getMessage())) return true;
        }
        return false;
    }
    public JsonMessages() { }
    public static JsonMessages fromJSON(String s) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(s, JsonMessages.class);
    }
    public boolean isToUser(String login){
        for (Message log : getList()){
            if (login.equals(log.getTo())) return true;
        }
        return false;
    }
}
