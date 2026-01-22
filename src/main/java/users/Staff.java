package users;

// pentru lista
import java.util.*;
import productions.*;
import requests.*;

public abstract class Staff extends User implements StaffInterface {

    private List<Request> lista_cereri;
    private SortedSet<Object> userCollection;

    // constructor
    public Staff(String username){
        super(username, 0);
        lista_cereri = new ArrayList<>();
        userCollection = new TreeSet<>();
    }

    // mthod to add into the list of requests
    public void addRequest(Request r){
        lista_cereri.add(r);
    }

    public void deleteRequest(Request r) {
        // add notification that the request has been solves or ignored
        r.notifyObservers("The request that you have submitted on the date " + r.getCreatedDate() +
                " and with the description: '" + r.description + "' HAS BEEN SOLVED or DELETED by " + r.to);
        lista_cereri.remove(r);
    }

    public List<Request> getLista_cereri(){
        return lista_cereri;
    }
    public SortedSet<Object> getUserCollection() {
        return userCollection;
    }

    // define methods from the interface
    public void addProductionSystem(Production p){
        // add the production into the system
        userCollection.add(p);

        // add the user as an observer
        p.production_observers.add(this);

        // add experience to the user if it isn't an admin
        if (this instanceof Contributor) {
            ExperienceStrategy strategy = new AddInSystemStrategy();
            super.addExperience(strategy.calculateExperience());
        }
    }
    public void addActorSystem(Actor a){
        // se adauga in lista
        userCollection.add((Object)a);

        // add the user as an observer
        a.production_observers.add(this);

        // add experience to the user if it isn't an admin
        if (this instanceof Contributor) {
            ExperienceStrategy strategy = new AddInSystemStrategy();
            super.addExperience(strategy.calculateExperience());
        }
    }

    public void removeProductionSystem(String name) {
        userCollection.removeIf(obj -> {
            if (obj instanceof Production) {
                return ((Production) obj).title.equals(name);
            }
            return false;
        });
    }

    public void removeActorSystem(String name) {
        userCollection.removeIf(obj -> {
            if (obj instanceof Actor) {
                return ((Actor) obj).name.equals(name);
            }
            return false;
        });
    }

    public void updateProduction(Production p){
        // check if it already exists
        if (userCollection.contains((Object)p) == true){
            // remove and add it
            userCollection.remove(p);
            userCollection.add(p);
        } else {
            System.out.println("Nu exista productia in sistem!");
        }
    }
    public void updateActor(Actor a){
        if (userCollection.contains(a) == true){
            userCollection.remove(a);
            userCollection.add(a);
        } else {
            System.out.println("Nu exista actorul in sistem!");
        }
    }

    public String convert_requests() {
        String str = "The list of requests is: ";
        int index = 1;

        if (lista_cereri.size() == 0) {
            str += "None\n";
            return str;
        }
        str += "\n";
        for (Request request : lista_cereri) {
            str += (index++) + ") " + request.toString() + "\n";
        }

        return str + "\n";
    }

    public String convert_collection() {
        String str = "The list of contribution: ";
        int index = 1;

        if (userCollection.size() == 0) {
            str += "None\n";
            return str;
        }

        str += "\n";
        for (Object object : userCollection) {
            str += (index++) + ") " + object.toString() + "\n";
        }
        return str;
    }

    // method that verifies that a name is in the list of requests
    public Object contains(String name) {
        for (Object obj : userCollection) {
            if (obj instanceof Actor) {
                Actor actor = (Actor) obj;

                if (actor.name.equals(name)) {
                    return actor;
                }
            } else if (obj instanceof Production) {
                Production production = (Production) obj;

                if (production.title.equals(name))
                    return production;
            }
        }
        return null;
    }
    public String toString() {
        return super.toString() + convert_requests() + convert_collection();
    }
}