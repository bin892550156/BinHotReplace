package bin.framework.dao;

import java.util.LinkedList;
import java.util.List;

public class IndexDao {

    public List<String> getUserList(){
        List<String> userList=new LinkedList<>();
        userList.add("XIAO_MING");
        userList.add("XIAO_HONG");
        userList.add("XIAO_XI");
        userList.add("Xiao_BIN");
        return userList;
    }
}
