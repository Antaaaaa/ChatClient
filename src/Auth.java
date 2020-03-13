import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Auth {
    private String login;
    private String password;
    UserStatus status;

    public Auth(String login, String password){
        this.login = login;
        this.password = password;
        this.status = new UserStatus(true);
    }
    public String toJSON() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return new StringBuilder().append("login=").append(login)
                .append("||password=").append(password).toString();
    }

    public int send(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setDoOutput(true);

        OutputStream os = con.getOutputStream();
        try {
            String json = toJSON();
            os.write(json.getBytes(StandardCharsets.UTF_8));
            return con.getResponseCode();
        } finally{
            os.close();
        }
    }
    public String getLogin() { return login; }

    public UserStatus getStatus() { return status; }

}
