package productions;

import ObserverPattern.Observer;
import main_default.IMDB;
import users.AddRatingStrategy;
import users.ExperienceStrategy;
import users.User;

import java.util.*;

public class Actor implements Comparable {
    public String name;

    public List<Map.Entry<String, String>> performances;
    // list of rating for the actor
    private List<Rating> ratings;
    public double averageRating;
    public List<Observer> production_observers;

    public String biography;

    // constructor
    public Actor(){
        name = "";
        biography = "";
        performances = new ArrayList<>();
        averageRating = 0;
        ratings = new ArrayList<>();
        production_observers = new ArrayList<>();
    }

    public void addRating(Rating rating) {
        User user = IMDB.find_user_by_username(rating.getUsername());

        ratings.add(rating); // add the rating into the list
        this.calculateAverageRating(); // recalculate the average rating

        if (user == null || checkRating(user, 1) == false)
            return;

        // add experience to the user for adding a rating
        ExperienceStrategy strategy = new AddRatingStrategy();
        user.addExperience(strategy.calculateExperience());

        System.out.println(ratings.size());
        // notify all the other users that added a rating
        for (Rating obj : ratings) {
            obj.notifyObservers("The user '" + user.username
                    + "' has added a rating to the actor '" + this.name +  "'!");
        }
        rating.addObserver(production_observers); // add the list of observers
        production_observers.add(user); // add the user who added the rating

        // sort the ratings based on the experience of each user
        sortRatings();
    }

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

    private void calculateAverageRating() {
        averageRating = 0;
        for (Rating aux : ratings) {
            averageRating += aux.getNota();
        }
        averageRating /= ratings.size();
    }

    public int getNumberOfAppearances() {
        return performances.size();
    }

    // method to add into the list
    public void add(String name_of_movie, String type) {
        performances.add(new AbstractMap.SimpleEntry<>(name_of_movie, type));
    }

    // method to remove a certain element from the list
    public void remove(String name_of_movie, String type) {
        for (Map.Entry<String, String> role : performances) {

            String movie_name = role.getKey();
            String movie_type = role.getValue();

            if (movie_type.equals(type) && movie_name.equals(name_of_movie)) {
                performances.remove(role);
            }
        }
    }

    public String toString() {
        String str = "";
        str +=  this.name + "\nBiography: " + this.biography + "\n\n" +
                 "The actor played in: \n";
        int index = 1;
        for (Map.Entry<String, String> role : this.performances) {
            str += (index++) + ") \"" +role.getKey() + "\"\n";
        }

        str += "\nRatings: \n";
        // get the ratings
        for (Rating rating : ratings) {
            str += rating + "\n";
        }

        str += "\n";

        return str;
    }

    @Override
    public int compareTo(Object obj) {
        if (obj instanceof Actor)
            return this.name.compareTo(((Actor)obj).name);
        else if (obj instanceof Production)
            return this.name.compareTo(((Production)obj).title);
        else
            System.out.println("Error no valid object type");
        return 0;
    }

    public String getNume() {
        return name;
    }

    public void addObserversFromRatings() {
        for (Rating rating : ratings) {
            User user = IMDB.find_user_by_username(rating.getUsername());
            if (user != null && production_observers.contains(user) == false) {
                production_observers.add(user);
                for (Observer observer : production_observers) {
                    observer.update("The user '" + user.username
                            + "' has added a rating to the actor '" + this.name +  "'!");
                }
            }
        }
    }
}