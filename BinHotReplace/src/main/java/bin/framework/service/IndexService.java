package bin.framework.service;

import bin.framework.dao.IndexDao;

import java.util.List;

public class IndexService {

    private IndexDao indexDao;

    public IndexService(){
        indexDao=new IndexDao();
    }

    public List<String> getUserList(){
        List<String> userList = indexDao.getUserList();
        return userList;
    }
}
