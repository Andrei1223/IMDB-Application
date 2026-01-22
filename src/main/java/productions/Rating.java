package productions;

import ObserverPattern.Observer;
import ObserverPattern.Subject;
import main_default.IMDB;
import users.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rating implements Subject {
    List<Observer> observers;
    private String username;
    private int nota;
    private String comment;

    public Rating(String username, int nota, String comment) {
        this.username = username;
        this.nota = nota;
        this.comment = comment;

        observers = new ArrayList<>();
        User user = IMDB.find_user_by_username(username);

        // add only valid users
        if (user != null)
            addObserver(user);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        if (nota <= 10 && nota >= 1)
            this.nota = nota;
        else
            System.out.println("Error : Invalid grade");
    }

    public String getComents() {
        return comment;
    }

    // method that returns the string of formatted comment

    public String toString() {
        return "Rating was posted by '" +
                username + "'" +
                "and it has an average grade of " + nota +
                "\nThe comment is: " + comment + "\n";
    }

    // methods for the observer design pattern
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

    public boolean containsObserver(Observer observer) {
        return observers.contains(observer);
    }

    public void addObserver(List<Observer> observers) {
        observers.addAll(observers);
    }
}