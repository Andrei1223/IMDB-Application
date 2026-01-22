package productions;

import ObserverPattern.Observer;
import main_default.IMDB;
import users.AddRatingStrategy;
import users.ExperienceStrategy;
import users.User;

import java.util.*;

public abstract class Production implements Comparable{
    // se defineste enumeratia
    public enum Genre{
        Action, Adventure, Comedy, Drama, Horror, SF,
        Fantasy, Romance, Mystery, Thriller, Crime, Biography, War,
        Cooking
    }

    public String title, type, plot;
    public List<String> directors;
    public List<String> actors;
    public List<Genre> genre;

    private List<Rating> ratings;
    public List<Observer> production_observers;

    public double averageRating;

    // constructor
    public Production(){
        directors = new ArrayList<>();
        actors = new ArrayList<>();
        genre = new ArrayList<>();
        ratings = new ArrayList<>();
        production_observers = new ArrayList<>();
        plot = null;
        averageRating = 0;
        title = null;
    }

    public void addRating(Rating rating) {
        User user = IMDB.find_user_by_username(rating.getUsername());

        ratings.add(rating); // add the rating into the list
        this.calculateAverageRating(); // recalculate the average rating

        // this verifies that a user cannot add 2 ratings and that the user exists
        if (user == null)
            return;

        if (checkRating(user, 1) == false)
            return;

        // add experience to the user for adding a rating
        ExperienceStrategy strategy = new AddRatingStrategy();
        user.addExperience(strategy.calculateExperience());

        // notify all the other users that added a rating
        for (Rating obj : ratings) {
            obj.notifyObservers("The user '" + user.username
             + "' has added a rating to the production '" + this.title +  "'!");
        }
        rating.addObserver(production_observers); // add the list of observers
        production_observers.add(user); // add the user who added the rating
        sortRatings();
    }

    public void removeRating(Rating rating) {
        ratings.remove(rating);
        calculateAverageRating();

        User user = IMDB.find_user_by_username(rating.getUsername());

        if (user == null) {
            System.out.println("Error in method removeRating");
        }

        // remove the experience for the rating
        ExperienceStrategy strategy = new AddRatingStrategy();
        user.addExperience(-1 * strategy.calculateExperience());
    }

    public void addExperienceFromRatings() {
        for (Rating rating : ratings) {
            User user = IMDB.find_user_by_username(rating.getUsername());
            if (user != null) {
                // add experience to the user for adding a rating
                ExperienceStrategy strategy = new AddRatingStrategy();
                user.addExperience(strategy.calculateExperience());
            }
        }
    }

    public String getTitle(){
        return title;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public Rating getRating(int index) {
        return ratings.get(index);
    }

    public List<Rating> getRating() {
        return ratings;
    }

    // method to check if a rating by a certain user has been added
    public boolean checkRating(User user, int number) {
        int numberOfRatings = 0;
        for (Rating rating : ratings) {
            if (rating.getUsername().equals(user.username)) {
                numberOfRatings++;
            }
        }
        if (numberOfRatings == number)
            return true;
        return false;
    }

    public int getNumberOfRatings() {
        return ratings.size();
    }

    public void calculateAverageRating(){
        this.averageRating = 0;
        double number = 0;

        // traverse the list of ratings
        for (Rating obj : ratings){
            number++;
            averageRating += obj.getNota();
        }

        this.averageRating /= number;
    }

    // method to sort the ratings based on the experience of the user that added it
    public void sortRatings() {
        Collections.sort(this.ratings, Comparator.comparingInt(o -> {
            if (o instanceof Rating) {
                Rating rating = (Rating) o;
                User user = IMDB.find_user_by_username(rating.getUsername());
                return (user != null) ? user.getExperience() : 0;
            }
            return 0;
        }).reversed());
    }

    public abstract void displayInfo(boolean running_mode);

    public int compareTo(Production otherProduction) {
        return this.title.compareTo(otherProduction.title);
    }

    public List<String> getGenreAsString() {
        List<String> list = new ArrayList<>();

        for (Genre aux : genre) {
            list.add(aux.name());
        }

        return list;
    }


    public String toString() {
        String str = "";
        str += this.title + " has the grade " +
                this.averageRating + " and has the description :" +
                this.plot + "\n";

        str += "\nRatings: \n";
        // get the ratings
        for (Rating rating : ratings) {
            str += rating + "\n";
        }
        return str;
    }

    public void addObserversFromRatings() {
        for (Rating rating : ratings) {
            User user = IMDB.find_user_by_username(rating.getUsername());
            if (user != null && production_observers.contains(user) == false) {
                production_observers.add(user);
                for (Observer observer : production_observers) {
                    observer.update("The user '" + user.username
                            + "' has added a rating to the production '" + this.title +  "'!");
                }
            }
        }
    }
}