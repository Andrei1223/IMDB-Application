package requests;

public interface RequestsManager{
    public void createRequest(String name, String description, String request_type);

    public void removeRequest(Request r);
}