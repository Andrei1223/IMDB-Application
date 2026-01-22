package users;

import productions.Actor;
import productions.Production;
import productions.Rating;
import requests.*;
import main_default.*;

public class Regular extends User implements RequestsManager{

    public Regular(){
        super("");
    }

    public Regular(String username){
        super(username);
    }


    // create a request by the regular user
    public void createRequest(String name, String description, String request_type) {
        Object obj = null;
        Request request = null;
        // find the object with that title
        if (request_type.equals("MOVIE_ISSUE") || request_type.equals("ACTOR_ISSUE")) {
            // for each contributor or admin account
            for (User user : IMDB.user_list) {
                if (user.getTip() == AccountType.CONTRIBUTOR ||
                        user.getTip() == AccountType.ADMIN) {
                    Staff staff = (Staff) user;
                    // get the production/actor with that name if it exists
                    if ((obj =  staff.contains(name)) != null) {
                        request = new Request(description,
                                            this.username,
                                            user.username,
                                            request_type);
                        request.name = name;
                        request.setType(request_type);
                        request.SetActualTime();
                        // add the request to the staff user
                        staff.addRequest(request);
                        // exit the loop
                        break;
                    }
                }
            }
            //  check if the user hasn't been found
            if (obj == null) {
                System.out.println("No user has been found!");
            }
        } else if (request_type.equals("OTHERS") || request_type.equals("DELETE_ACCOUNT")){
            request = new Request(description,
                                    this.username,
                    "ADMIN",
                                    request_type);
            // add it to the list for the admins
            RequestsHolder.addRequest(request);
        } else {
            System.out.println("Error: Not a valid type of request - " + request_type);
        }
    }

    // delete request
    public void removeRequest(Request r){
        IMDB.request_list.remove(r);
    }

    // can add a rating for a production or actor
    public void addRating(Object object, int grade, String comment) {
        Rating rating = new Rating(this.username, grade, comment);

        // add the rating into the list
        if (object instanceof Actor) {
            ((Actor) object).addRating(rating);
        }
        else if (object instanceof Production) {
            ((Production) object).addRating(rating);
        }
        else
            System.out.println("Unknown type");
    }
}

