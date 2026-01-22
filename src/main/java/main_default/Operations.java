package main_default;

import ObserverPattern.Observer;
import productions.Actor;
import productions.Production;
import productions.Rating;
import requests.Request;
import requests.RequestsHolder;
import users.*;

import java.util.List;

public class Operations {

    // method that deletes from everywhere a production, actor etc
    public static List<?> delete(Object object, List<?> list) {
        // delete from system
        if (object instanceof Production) {
            System.out.println("the production with the name " + ((Production) object).title + "has been deleted");
            return delete_production((Production) object, list);
        } else if (object instanceof Actor) {
            System.out.println("the actor with the name " + ((Actor) object).name + "has been deleted");
            return delete_actor((Actor)object, list);
        } else if (object instanceof Request) {
            System.out.println("the request with the type " + ((Request) object).getType() + "has been deleted");
            return delete_request((Request)object, list);
        } else if (object instanceof User) {
            System.out.println("the user with the username " + ((User) object).username + "has been deleted");
            return delete_user((User)object, list);
        } else if (object instanceof Rating) {
            System.out.println("the user with the username " + ((Rating) object).getUsername() + "has been deleted");
            delete_rating((Rating)object, list);
            return null;
        }
        System.out.println("error in operations");
        return null;
    }

    public static List<?> delete_production(Production production, List<?> list) {
        // delete from the system
        IMDB.production_list.remove(production);
        list.remove(production);

        // delete all the requests about this production
        for (Request request : IMDB.request_list) {
            if ((request.getType().equals("MOVIE_ISSUE") || request.getType().equals("ACTOR_ISSUE")) &&
                    request.name.equals(production.title) == true) {
                list = delete_request(request, list);
            }
        }

        for (Rating rating : production.getRating()) {
            // delete the ratings
            delete_rating(rating, list);
        }
        // notify the observers
        for (Observer object : production.production_observers) {
            object.update("The production with the name '" + production.title + "' has been deleted!");
        }

        for (User user : IMDB.user_list) {
            // delete from each favorite list
            user.favorites.remove(production);

            if (user instanceof Staff) {
                // delete from the contributors or admins
                ((Staff) user).removeProductionSystem(production.title);
            }
        }

        return list;
    }
    public static List<?> delete_actor(Actor actor, List<?> list) {
        list.remove(actor);
        IMDB.actor_list.remove(actor);

        // delete all the requests about this actor
        for (Request request : IMDB.request_list) {
            if ((request.getType().equals("MOVIE_ISSUE") || request.getType().equals("ACTOR_ISSUE")) &&
                    request.name.equals(actor.name) == true) {
                list = delete_request(request, list);
            }
        }

        for (Rating rating : actor.getRating()) {
            // delete the ratings
            delete_rating(rating, list);
        }
        // notify the observers
        for (Observer object : actor.production_observers) {
            object.update("The actor with the name '" + actor.name + "' has been deleted!");
        }

        for (User user : IMDB.user_list) {
            // delete from each favorite list
            user.favorites.remove(actor);

            if (user instanceof Staff) {
                // delete from the contributors or admins
                ((Staff) user).removeActorSystem(actor.name);
            }
        }

        return list;
    }
    public static List<?> delete_request(Request request, List<?> list) {
        IMDB.request_list.remove(request);
        list.remove(request);
        RequestsHolder.removeRequest(request);

        return list;
    }
    public static List<?> delete_user(User user, List<?> list) {
        // check to not delete the same account as the one that is logged in
        if (user == MyGUI.user)
            return list;

        list.remove(user);
        IMDB.user_list.remove(user);
        // if a staff user has been deleted move the contributions to the admins
        if (user instanceof Staff) {
            // for each admin in the system
            for (User u : IMDB.user_list) {
                if (u instanceof Admin) {
                    for (Object object : ((Staff) user).getUserCollection()) {
                        // if an admin doesn't contain the productions/actors from the staff deleted user add them
                        if (((Admin) u).getUserCollection().contains(object) == false) {
                            ((Admin) u).getUserCollection().add(object);
                        }
                    }
                }
            }

            // check for requests that must be resolved by this user that can only be from staff
            for (Request request : IMDB.request_list) {
                if (request.to.equals(user.username)) {
                    // change the request for the admins
                    RequestsHolder.addRequest(request);
                }
            }
        }


        // TODO continue

        return list;
    }

    public static void solve_request(Request request) {
        IMDB.request_list.remove(request);
        RequestsHolder.removeRequest(request);

        User user = IMDB.find_user_by_username(request.username);

        if (user == null) {
            System.out.println("Error in function solve_request");
            return;
        }

        // add experience to the user that created the request
        ExperienceStrategy strategy = new SolvedRequestStrategy();
        user.addExperience(strategy.calculateExperience());

        // add a notification to the user that created the request
        request.notifyObservers("The request with the type '" + request.getType() + "' has been solved!");
    }

    // deletes a rating of an actor or production
    public static void delete_rating(Rating rating, List<?> list) {
        // find the production or actor with that rating
        for (Production production : IMDB.production_list) {
            if (production.getRating().contains(rating) == true) {
                production.removeRating(rating);
                return;
            }
        }

        for (Actor actor : IMDB.actor_list) {
            if (actor.getRating().contains(rating) == true) {
                actor.removeRating(rating);
                return;
            }
        }
    }
}
