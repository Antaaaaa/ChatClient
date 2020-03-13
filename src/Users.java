import java.util.List;

public class Users {
    private final List<Auth> list;

    public Users(List<Auth> lst){
        this.list = lst;
    }
    public List<Auth> getList(){return list;}
}
