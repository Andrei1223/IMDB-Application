package requests;

import main_default.IMDB;
import users.User;

import java.util.*;

public class RequestsHolder{

    public static List<Request> requestList = new ArrayList<>();

    // add into the request list another list
    public static void addAllList(List<Request> list){
        requestList.addAll(list);
    }
    public static void addRequest(Request r){
        requestList.add(r);
    }
    public static void removeRequest(Request r){
        requestList.remove(r);
    }
}