package bin.framework.controller;

import bin.framework.service.IndexService;

import java.util.List;

public class IndexController {

    private IndexService indexService;

    public IndexController() {
        indexService=new IndexService();
    }

    public List<String> getUserList(){
        List<String> userList = indexService.getUserList();
        return userList;
    }
}
