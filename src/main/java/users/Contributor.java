package users;

import requests.*;

public class Contributor extends Staff implements RequestsManager{

    public Contributor(String username){
        super(username);
    }

    public Contributor(){
        super("");
    }

    // implemenatrea metode din interfata
    public void createRequest(String name, String description, String request_type) {

    }

    public void removeRequest(Request r){
        super.deleteRequest(r);
    }
}