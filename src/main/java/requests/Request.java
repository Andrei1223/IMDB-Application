package requests;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// import the observer and subject interfaces
import ObserverPattern.*;
import main_default.IMDB;
import users.Admin;
import users.User;

public class Request implements Subject {
    // the list of observers
    List<Observer> observers;

    // define the enum
    private enum RequestType{
        DELETE_ACCOUNT, ACTOR_ISSUE,
        MOVIE_ISSUE, OTHERS
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdDate;
    private RequestType type;
    public String name; // this name is present if it is about an actor or a production
    public String description, username, to;

    // constructor
    public Request(String descriere,
                   String username,
                   String usernameRezolvator,
                   String tip){
        // completare campuri
        this.description = descriere;
        this.username = username;
        this.to = usernameRezolvator;
        this.setType(tip);
        name = null;

        observers = new ArrayList<>();
        addUsersAsObservers(to);
        addUsersAsObservers(username);
        // send a notification to the user that has received a request
        notifyObservers("You have a new request " + "of type " + getType() + " from '" + username +"'!");
        // remove the observer only the user who created the request must be notified
        removeObserver(IMDB.find_user_by_username(to));
        createdDate = LocalDateTime.now();// se the current time
    }

    public void addUsersAsObservers(String username) {
        User user = IMDB.find_user_by_username(username);
        // check if it is for the admin team
        if (username.equals("ADMIN")) {
            // add all the admins as observers
            for (User admin : IMDB.user_list) {
                if (admin instanceof Admin) {
                    observers.add(admin);
                }
            }
            return;
        }

        // add the 2 usernames as observers
        if (user != null)
            observers.add(user);
        else
            System.out.println("Error: The user with the username " + username + " cannot be added as an observer");
    }

    public void setCreatedDate(String DataCreareCerere){
        // define the formatter for the given date-time pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        // cast the string to LocalDateTime
        this.createdDate = LocalDateTime.parse(DataCreareCerere, formatter);
    }

    public void SetActualTime() {
        LocalDateTime time = LocalDateTime.now();
        createdDate = time;
    }
    // getter pentru data
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    // setter for the type of request
    public void setType(String str){

        switch (str){
            case "DELETE_ACCOUNT":
                type = RequestType.DELETE_ACCOUNT;
                break;
            case "ACTOR_ISSUE":
                type = RequestType.ACTOR_ISSUE;
                break;
            case "MOVIE_ISSUE":
                type = RequestType.MOVIE_ISSUE;
                break;
            case "OTHERS":
                type = RequestType.OTHERS;
                break;
        }
    }

    // getter for the type request
    public String getType(){

        switch (type){
            case DELETE_ACCOUNT:
                return "DELETE_ACCOUNT";
            case ACTOR_ISSUE:
                return "ACTOR_ISSUE";
            case MOVIE_ISSUE:
                return "MOVIE_ISSUE";
            case OTHERS:
                return "OTHERS";
        }
        return null;
    }

    public String toString() {
        return "\tRequest with the name '" + name +"' and was created on " + createdDate +
                " with the type " + getType() +
                ".\n\tThe request was created by "+ username + " and was intended for " + to +
                "'.\n\t The description is: '" + description + '\'';
    }

    // define functions from the interface
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String notification) {
        for (Observer observer : observers) {
            observer.update(notification);
        }
    }

    // class to format the dateTime variable into a string
    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        @Override
        public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(localDateTime.format(formatter));
        }
    }
}