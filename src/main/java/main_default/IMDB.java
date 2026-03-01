package main_default;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

// import json simple
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

//import my packages
import users.*;
import productions.*;
import requests.*;

// class for the entire program
public class IMDB {
    // parameters for running the app
    static volatile boolean running = true;
    public static volatile boolean login = false; // it is set to volatile to be changed from another thread ie the window
    static boolean first_iteration = true;

    // false for CLI and true for GUI
    RunMode running_mode = RunMode.NONE;
    public static volatile List<User> user_list;

    // liste pentru contentul bazei de date
    public static volatile List<Actor> actor_list;
    public static volatile List<Request> request_list;

    // lista pentru productii
    public static volatile List<Production> production_list;

    // implement singleton design pattern
    private static IMDB obj = null; // create static object

    // create getInstance method
    public static IMDB getInstance() {
        if (obj == null)
            return new IMDB();
        return obj;
    }

    // constructor pentru liste
    private IMDB() {
        // allocate memory for the lists
        user_list = new ArrayList<>();
        actor_list = new ArrayList<>();
        request_list = new ArrayList<>();
        production_list = new ArrayList<>();
    }

    // the main method
    public void run() {
        // variable for the running program
        running = true;
        first_iteration = true;
        login = false;
        running_mode = RunMode.NONE;
        String extension = "src/main/resources/input/";

        // read info from the input files
        read_productions(extension + "production.json");
        read_actors(extension + "actors.json");
        read_users(extension + "accounts.json");
        read_requests(extension + "requests.json");

        // after reading the users from the JSON add observers
        for (Production production : production_list) {
            production.addObserversFromRatings();
        }
        for (Actor actor : actor_list) {
            actor.addObserversFromRatings();
        }

        // after reading the users from the JSON sort the ratings and add experience
        for (Production production : production_list) {
            production.sortRatings();
            production.addExperienceFromRatings();
        }
        for (Actor actor : actor_list) {
            actor.sortRatings();
            actor.addExperienceFromRatings();
        }

        // add as observers for each production and actor for all the users that have them at favorites
        for (User user : user_list) {
            for (Object obj : user.favorites) {
                if (obj instanceof Actor && !((Actor) obj).production_observers.contains(user)) {
                    ((Actor) obj).production_observers.add(user);
                } else if (obj instanceof Production && !((Production) obj).production_observers.contains(user)) {
                    ((Production) obj).production_observers.add(user);
                }
            }
        }

        // initialize main objects
        MyCLI CLI = null;
        MyGUI GUI = null;
        User user = null;

        while (running) {
            // for the first step check if the user wants CLI or GUI
            if (first_iteration) {
                // call the function that checks for the running mode
                running_mode = MyGUI.check_running_mode();
                first_iteration = false; // set the first iteration to false
            }

            // if the user isn't logged in
            if (login == false && user == null) {
                // create the object based on the selected interface
                if (running_mode == RunMode.CLI) {
                    if (CLI == null)
                        CLI = new MyCLI();
                    // login
                    user = CLI.login(user_list);
                } else if (running_mode == RunMode.GUI) {
                    if (GUI == null) {
                        GUI = new MyGUI();

                        //user = find_user_by_username("steven_jackson_1819");
                        //login = true;
                        //MyGUI.user = user;
                        //GUI.create_UI();
                    }
                    // login
                    if (login == false) {
                        user = GUI.open_imdb_window();
                        login = true;
                    }
                } else {
                    // TODO raise exception
                }
            }

            int operation_index = -1;
            // read from the terminal
            if (running_mode == RunMode.CLI) {
                // get the operation
                operation_index = CLI.print_template(user);

                if (operation_index == -1)
                    continue;

                // if there isn't a valid operation
                operation_index--;
                String input = "";
                Object answear = null;
                CLI.clear(); // clear the terminal screen

                switch (CLI.template_list.get(operation_index)) {
                    case "View notifications":
                        System.out.println(user.view_notifications());
                        break;
                    case "View productions details":
                        // check the mode
                        input = CLI.read_from_terminal("You want the productions in alphabetical order?: (yes/no) (y/n)");
                        if (input.equals("yes") || input.equals("y")) {
                            // copy the list
                            List<Production> aux_list = new ArrayList<>(production_list);
                            // sort the list
                            Collections.sort(aux_list, Comparator.comparing(Production::getTitle));
                            System.out.println(CLI.view_productions(aux_list));
                        } else {
                            System.out.println(CLI.view_productions(production_list));
                        }
                        break;
                    case "View actors details":
                        // check the mode
                        input = CLI.read_from_terminal("You want the actors in alphabetical order?: (yes/no) (y/n)");
                        if (input.equals("yes") || input.equals("y")) {
                            // copy the list
                            List<Actor> aux_list = new ArrayList<>(actor_list);
                            // sort the list
                            Collections.sort(aux_list, Comparator.comparing(Actor::getNume));
                            System.out.println(CLI.view_actors(aux_list));
                        } else {
                            System.out.println(CLI.view_actors(actor_list));
                        }
                        break;

                    case "Search for actor/movie/series":
                        input = CLI.read_from_terminal("What do you want to search? (actor/production) (a/p): ");
                        if (input.equals("actor") || input.equals("a")) {
                            input = CLI.read_from_terminal("What is the actor's name: ");

                            answear = find_actor_by_name(input);
                            // print the actor information
                            if (answear == null) {
                                System.out.println("The actor with the name " + input + " doesn't exist!");
                            } else {
                                System.out.println(answear);
                            }
                        } else if (input.equals("production") || input.equals("p")) {
                            // search in the productions list
                            input = CLI.read_from_terminal("What is the production's name: ");

                            answear = find_production_by_name(input);
                            // print the production information
                            if (answear == null) {
                                System.out.println("The production with the name " + input + " doesn't exist!");
                            } else {
                                System.out.println(answear);
                            }
                        } else {
                            System.out.println("Incorrect input!\n");
                        }
                        break;

                    case "Add/Delete actor/movie/series to/from favorites":
                        input = CLI.read_from_terminal("Select the action (add/delete) (a/d): ");

                        if (input.equals("add") || input.equals("a")) {
                            input = CLI.read_from_terminal("What do you want to add? (actor/production) (a/p): ");

                            if (input.equals("actor") || input.equals("a")) {
                                // get the name of the actor
                                input = CLI.read_from_terminal("What is the actor's name: ");

                                // get the actor object from the system
                                answear = find_actor_by_name(input);

                                // check if the actor exists in the favorite list
                                if (answear == null) {
                                    System.out.println("The actor with the name " + input + " doesn't exist!");
                                } else if (user.favorites.contains(answear) == false) {
                                    System.out.println("The actor with the name " + input + " doesn't exist in the favorites!");
                                } else {
                                    user.favorites.add(answear);
                                }

                            } else if (input.equals("production") || input.equals("p")) {
                                // get the name of the production
                                input = CLI.read_from_terminal("What is the production's name: ");

                                // get the production object from the system
                                answear = find_production_by_name(input);

                                if (answear == null) {
                                    System.out.println("The production with the name " + input + " doesn't exist!");
                                } else if (user.favorites.contains(answear)) {
                                    System.out.println("The production with the name " + input + " already exists in the favorites!");
                                } else {
                                    user.favorites.add(answear);
                                }
                            } else {
                                System.out.println("Incorrect input!\n");
                            }

                        } else if (input.equals("delete") || input.equals("d")) {
                            input = CLI.read_from_terminal("What do you want to delete? (actor/production) (a/p): ");

                            if (input.equals("actor") || input.equals("a")) {
                                // get the name of the actor
                                input = CLI.read_from_terminal("What is the actor's name: ");

                                // search the actor
                                answear = find_actor_by_name(input);

                                if (answear == null) {
                                    System.out.println("The actor with the name " + input + " doesn't exist!");
                                } else if (user.favorites.contains(answear) == false) {
                                    System.out.println("The actor with the name " + input + " doesn't exist in the favorites!");
                                } else {
                                    user.favorites.remove(answear);
                                }
                            } else if (input.equals("production") || input.equals("p")) {
                                // get the name of the production
                                input = CLI.read_from_terminal("What is the production's name: ");

                                // search the production
                                answear = find_production_by_name(input);

                                if (answear == null) {
                                    System.out.println("The production with the name " + input + " doesn't exist!");
                                } else if (user.favorites.contains(answear) == false) {
                                    System.out.println("The production with the name " + input + " doesn't exist in the favorites!");
                                } else {
                                    user.favorites.remove(answear);
                                }
                            } else {
                                System.out.println("Incorrect input!\n");
                            }
                        }
                        break;
                    case "Create/Delete a request":
                        break;
                    case "Logout":
                        user = null;
                        break;
                    case "Exit":
                        running = false;
                        user = null;
                        break;
                }
            }
        }

        // if CLI is used cleanup
        if (running_mode == RunMode.CLI) {
            CLI.cleanup();
        } else {
            GUI.cleanup();
        }

        // update the JSON files with the new modifications of the db
        update_json_files("src/main/resources/input/");
    }

    // method that returns the actor or production object based on the type
    public static Object return_object(String name, String type) {
        Object object = null;

        switch (type) {
            case "Production":
                for (Production production : production_list) {
                    if (production.title.equals(name)) {
                        object = production;
                        break;
                    }
                }

                if (object == null) {
                    System.out.println("error no production with this name: " + name);
                }

                break;

            case "Actor":

                for (Actor actor : actor_list) {
                    if (actor.name.equals(name)) {
                        object = actor;
                        break;
                    }
                }

                if (object == null) {
                    System.out.println("error no actor with this name: " + name);
                }

                break;
        }

        return object;
    }

    // method that reads the json file for the users
    private static void read_users(String path) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(path)) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (Object key : jsonArray) {
                JSONObject jsonData = (JSONObject) key;

                String userType = (String) jsonData.get("userType");
                User user;

                JSONObject information = (JSONObject) jsonData.get("information");
                JSONObject credentials = (JSONObject) information.get("credentials");

                // create a new user object using factory design pattern
                user = UserFactory.factory(User.returnEnum(userType));

                // read the contributions ONLY for Admin and Contributor
                if (!(user instanceof Regular)) {
                    // read the productions
                    JSONArray productions_contribution = (JSONArray) jsonData.get("productionsContribution");

                    // check if it is no null
                    if (productions_contribution != null) {
                        // loop for reading all the productions
                        for (Object name : productions_contribution) {
                            // search for each name and add it
                            ((Staff) user).addProductionSystem((Production) (return_object(((String)name), "Production")));
                        }
                    }

                    JSONArray productions_actors = (JSONArray) jsonData.get("actorsContribution");
                    if (productions_actors != null) {
                        // loop for reading all the actors
                        for (Object name : productions_actors) {
                            ((Staff) user).addActorSystem((Actor)(return_object(((String)name), "Actor")));
                        }
                    }
                }

                // read the favorite productions
                JSONArray favorite_productions = (JSONArray) jsonData.get("favoriteProductions");

                // check if it is no null
                if (favorite_productions != null) {
                    // loop for reading all the productions
                    for (Object name : favorite_productions) {
                        // search for each name and add it
                        user.addFavorite(return_object(((String)name), "Production"));
                    }
                }

                // read the favorite actors
                JSONArray favorite_actors = (JSONArray) jsonData.get("favoriteActors");
                if (favorite_actors != null) {
                    // loop for reading all the actors
                    for (Object name : favorite_actors) {
                        user.addFavorite(return_object(((String)name), "Actor"));
                    }
                }

                // read the notifications
                JSONArray notifications = (JSONArray) jsonData.get("notifications");
                if (notifications != null) {
                    for (Object name : notifications) {
                        user.notifications.add((String) name);
                    }
                }

                user.username = (String) jsonData.get("username");
                Object experienceValue = jsonData.get("experience");
                user.setExperience(experienceValue);

                // set information
                user.setInformation(new Credentials(
                        (String) credentials.get("email"),
                        (String) credentials.get("password")),
                        (String) information.get("name"),
                        (String) information.get("country"),
                        (int) Long.parseLong(information.get("age").toString()),
                        (String) information.get("gender"),
                        (String) information.get("birthDate")
                        );

                // set credentials
                user.setCredentials(new Credentials(
                        (String) credentials.get("email"),
                        (String) credentials.get("password")
                ));

                //user.setInformation(info);

                user.setTip((String) jsonData.get("userType"));

                // add the user info the list
                user_list.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        //getUserCollectionAsString();
    }

    // method that reads the json file for the requests
    private static void read_requests(String path) {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(path)) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (Object key : jsonArray) {
                JSONObject jsonData = (JSONObject) key;

                // create a new object
                Request request = new Request((String) jsonData.get("description"),
                        (String) jsonData.get("username"),
                        (String) jsonData.get("to"),
                        (String) jsonData.get("type"));

                // add the creation date
                request.setCreatedDate((String) jsonData.get("createdDate"));

                String type = (String) jsonData.get("type");

                // check if it has the name of the actor or movie
                if (type.compareTo("ACTOR_ISSUE") == 0) {
                    request.name = (String) jsonData.get("actorName");
                }

                if (type.compareTo("MOVIE_ISSUE") == 0) {
                    request.name = (String) jsonData.get("movieTitle");
                }

                if (request.to.equals("ADMIN")) {
                    // add the requests into the admin list
                    RequestsHolder.addRequest(request);
                    // add also the new request into the list
                    request_list.add(request);
                } else {
                    // add the new request into the list
                    request_list.add(request);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // method that reads the json file for the productions
    private static void read_productions(String path) {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(path)) {
            JSONArray productionsArray = (JSONArray) parser.parse(reader);

            for (Object obj : productionsArray) {
                JSONObject jsonData = (JSONObject) obj;

                Production production = null;

                // get the type
                String type = (String) jsonData.get("type");

                // check the type and read the specific fields
                switch (type) {
                    case "Movie":
                        production = new Movie();

                        // checks if the field exists
                        if (jsonData.get("releaseYear") == null) {
                            ((Movie) production).releaseYear = 0;
                        }
                        else {
                            ((Movie) production).releaseYear = ((Number) jsonData.get("releaseYear")).intValue();
                        }

                        // read the duration
                        ((Movie) production).duration = (String) jsonData.get("duration");
                        production.type = "Movie";
                        break;
                    case "Series":
                        production = new Series();
                        ((Series) production).releaseYear = ((Number) jsonData.get("releaseYear")).intValue();
                        // read the number of seasons
                        ((Series) production).numberSeasons = ((Number) jsonData.get("numSeasons")).intValue();

                        JSONObject seasonsObject = (JSONObject) jsonData.get("seasons");

                        for (int i = 1; i <= ((Series) production).numberSeasons; i++) {
                            String seasonName = "Season " + i;
                            JSONArray episodesArray = (JSONArray) seasonsObject.get(seasonName);

                            List<Episode> episodeList = new ArrayList<>();

                            for (Object episodeObj : episodesArray) {
                                JSONObject episodeData = (JSONObject) episodeObj;
                                String episodeName = (String) episodeData.get("episodeName");
                                String duration = (String) episodeData.get("duration");

                                Episode episode = new Episode(episodeName, duration);
                                episodeList.add(episode);
                            }

                            ((Series) production).addEpisodes(seasonName, episodeList);
                        }

                        production.type = "Series";
                        break;
                }
                production.title = (String) jsonData.get("title");

                // get the lists from the file
                JSONArray directorsArray = (JSONArray) jsonData.get("directors");
                JSONArray actorsArray = (JSONArray) jsonData.get("actors");
                JSONArray genresArray = (JSONArray) jsonData.get("genres");
                JSONArray ratingsArray = (JSONArray) jsonData.get("ratings");

                // handle the array of directors
                List<String> directorsList = new ArrayList<>();
                for (Object director : directorsArray) {
                    directorsList.add((String) director);
                }
                production.directors = directorsList;

                // Handle actorsArray
                List<String> actorsList = new ArrayList<>();
                for (Object actor : actorsArray) {
                    actorsList.add((String) actor);
                }
                production.actors = actorsList;

                // handle genresArray
                List<Production.Genre> genresList = new ArrayList<>();
                for (Object genre : genresArray) {
                    genresList.add(Production.Genre.valueOf((String) genre));
                }
                production.genre = genresList;

                for (Object ratingObj : ratingsArray) {
                    JSONObject ratingData = (JSONObject) ratingObj;
                    String username = (String) ratingData.get("username");
                    int rating = ((Number) ratingData.get("rating")).intValue();
                    String comment = (String) ratingData.get("comment");

                    Rating ratingObject = new Rating(username, rating, comment);
                    production.addRating(ratingObject);
                }

                production.plot = (String) jsonData.get("plot");

                // add the production into the list
                production_list.add(production);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    // method that reads the json file for the actors
    private static void read_actors(String path)  {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(path)) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (Object key : jsonArray) {
                JSONObject jsonData = (JSONObject) key;


                // create a new object
                Actor actor = new Actor();

                // set the name of the actor
                actor.name = (String) jsonData.get("name");

                // set the biography
                actor.biography = (String) jsonData.get("biography");

                // read the list of movies
                JSONArray performance = (JSONArray) jsonData.get("performances");
                JSONArray ratings = null;

                // check if there is a field for the average rating
                if (jsonData.get("averageRating") != null) {
                    actor.averageRating = (double)jsonData.get("averageRating");
                }

                // read the ratings for the actor if they exists
                if (jsonData.get("ratings") != null) {
                    ratings = (JSONArray) jsonData.get("ratings");
                    for (Object obj : ratings) {
                        JSONObject fields = (JSONObject) obj;
                        String username = (String) fields.get("username");
                        int rating = ((Number) fields.get("rating")).intValue();
                        String comment = (String) fields.get("comment");

                        Rating ratingObject = new Rating(username, rating, comment);
                        actor.addRating(ratingObject);
                    }
                }

                for (Object obj : performance) {
                    JSONObject fields = (JSONObject) obj;

                    String movie_name = (String) fields.get("title");
                    String type = (String) fields.get("type");
                    // set the name of the movie and the type
                    actor.add(movie_name, type);
                }

                // add the new request into the list
                actor_list.add(actor);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // search in user list
    public static User find_user_by_username(String username){
        // traverse the user list
        for (User user : user_list) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return null;
    }

    public Actor find_actor_by_name(String name){
        // search in the actors list
        for (Actor actor : actor_list) {
            if (actor.name.equalsIgnoreCase(name)) {
                return actor;
            }
        }

        return null;
    }

    public Production find_production_by_name(String name) {
        for (Production production : production_list) {
            if (production.title.equalsIgnoreCase(name)) {
                return production;
            }
        }
        return null;
    }

    // method to update the json files
    public static void update_json_files(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new Request.LocalDateTimeSerializer());
        objectMapper.registerModule(module);

        try {
            // serialize list to JSON and write to file
            objectMapper.writeValue(new File(filePath + "requests.json"), createJsonListForRequests());

            objectMapper.writeValue(new File(filePath + "actors.json"), createJsonListForActor());

            objectMapper.writeValue(new File(filePath + "production.json"), createJsonListForProduction());

            // print the users into the JSON file
            objectMapper.writeValue(new File(filePath + "accounts.json"), createJsonListForUser());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Map<String, Object>> createJsonListForRequests() {
        // create a list of maps representing the desired JSON structure
        List<Map<String, Object>> jsonList = new ArrayList<>();

        for (Request request : request_list) {
            Map<String, Object> jsonMap = new HashMap<>();

            jsonMap.put("type", request.getType());
            jsonMap.put("createdDate", request.getCreatedDate());
            jsonMap.put("username", request.username);
            jsonMap.put("to", request.to);
            jsonMap.put("description", request.description);

            if (request.getType().equals("ACTOR_ISSUE")) {
                jsonMap.put("actorName", request.name);
            } else if (request.getType().equals("MOVIE_ISSUE")) {
                jsonMap.put("movieTitle", request.name);
            }

            jsonList.add(jsonMap);
        }

        return jsonList;
    }

    // method to print the users into the JSON file
    private static List<Map<String, Object>> createJsonListForUser() {
        // create a list of maps representing the desired JSON structure
        List<Map<String, Object>> jsonList = new ArrayList<>();

        for (User user : user_list) {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("username", user.username);
            jsonMap.put("experience", user.getExperience() + ""); // convert to string the experience

            Map<String, Object> information = new HashMap<>();
            Map<String, Object> credentials = new HashMap<>();
            // write the credentials
            credentials.put("email", user.getEmail());
            credentials.put("password", user.getPassword());
            information.put("credentials", credentials);

            // write the information
            information.put("name", user.getInfo().name);
            information.put("country", user.getInfo().country);
            information.put("age", user.getInfo().age);
            information.put("gender", user.getInfo().gender);
            information.put("birthDate", user.getInfo().getDate_of_birth());
            jsonMap.put("information", information);
            jsonMap.put("userType", user.getTypeAsString());

            // add favorites productions/actors
            if (user.favorites.size() > 0) {
                List<String> favoriteActors = new ArrayList<>();
                List<String> favoriteProductions = new ArrayList<>();

                for (Object obj : user.favorites) {
                    if (obj instanceof Production) {
                        favoriteProductions.add(((Production) obj).title);
                    } else {
                        favoriteActors.add(((Actor) obj).name);
                    }
                }

                if (favoriteActors.size() > 0)
                    jsonMap.put("favoriteActors", favoriteActors);
                if (favoriteProductions.size() > 0)
                    jsonMap.put("favoriteProductions", favoriteProductions);
            }

            // add additional information based on the account type
            if (user instanceof Staff && ((Staff) user).getUserCollection().size() > 0) {
                // add contributions
                List<String> actorsContribution = new ArrayList<>();
                List<String> productionsContribution = new ArrayList<>();

                for (Object obj : ((Staff) user).getUserCollection()) {
                    if (obj instanceof Production) {
                        productionsContribution.add(((Production) obj).title);
                    } else {
                        actorsContribution.add(((Actor) obj).name);
                    }
                }

                if (actorsContribution.size() > 0)
                    jsonMap.put("actorsContribution", actorsContribution);
                if (productionsContribution.size() > 0)
                    jsonMap.put("productionsContribution", productionsContribution);
            }

            jsonList.add(jsonMap);
        }
        return jsonList;
    }

    // method to print the actors into the JSON file
    private static List<Map<String, Object>> createJsonListForActor() {
        // create a list of maps representing the desired JSON structure
        List<Map<String, Object>> jsonList = new ArrayList<>();

        for (Actor actor : actor_list) {
            Map<String, Object> jsonMap = new HashMap<>();

            jsonMap.put("name", actor.name);
            jsonMap.put("biography", actor.biography);

            List performances = new ArrayList();

            // add the name of the productions and the type
            for (Map.Entry<String, String> entry : actor.performances) {
                Map<String, Object> aux = new HashMap<>();

                aux.put("title", entry.getKey());
                aux.put("type", entry.getValue());

                performances.add(aux);
            }
            jsonMap.put("performances", performances);

            // add the ratings into the JSON file
            List<Map<String, Object>> ratings = new ArrayList<>();
            for (int i = 0; i < actor.getNumberOfRatings(); i++) {
                Map<String, Object> aux = new HashMap<>();

                aux.put("username", actor.getRating(i).getUsername());
                aux.put("rating", actor.getRating(i).getNota());
                aux.put("comment", actor.getRating(i).getComents());

                ratings.add(aux);
            }
            jsonMap.put("ratings", ratings);

            // delete the ratings from the list to delete experience
            List<Rating> aux_list = new ArrayList<>(actor.getRating());
            for (Rating r : aux_list) {
                actor.removeRating(r);
            }

            jsonList.add(jsonMap);
        }

        return  jsonList;
    }

    private static List<Map<String, Object>> createJsonListForProduction() {
        // create a list of maps representing the desired JSON structure
        List<Map<String, Object>> jsonList = new ArrayList<>();

        for (Production production : production_list) {
            Map<String, Object> jsonMap = new HashMap<>();

            jsonMap.put("title", production.title);
            jsonMap.put("plot", production.plot);
            jsonMap.put("type", production.type);
            jsonMap.put("averageRating", production.averageRating);
            jsonMap.put("directors", production.directors);
            jsonMap.put("actors", production.actors);
            jsonMap.put("genres", production.getGenreAsString());

            if (production.title.equals("The Godfather")) {
                System.out.println(production.getNumberOfRatings());
            }
            // add the ratings into the JSON file
            List<Map<String, Object>> ratings = new ArrayList<>();
            for (int i = 0; i < production.getNumberOfRatings(); i++) {
                Map<String, Object> aux = new HashMap<>();

                aux.put("username", production.getRating(i).getUsername());
                aux.put("rating", production.getRating(i).getNota());
                aux.put("comment", production.getRating(i).getComents());

                ratings.add(aux);
            }
            jsonMap.put("ratings", ratings);

            // delete the ratings from the list to delete experience
            List<Rating> aux_list = new ArrayList<>(production.getRating());
            for (Rating r : aux_list) {
                production.removeRating(r);
            }

            if (production instanceof Movie) {
                jsonMap.put("duration", ((Movie) production).duration);
                jsonMap.put("releaseYear", ((Movie) production).releaseYear);
            } else if (production instanceof Series) {
                jsonMap.put("releaseYear", ((Series) production).releaseYear);
                jsonMap.put("numSeasons", ((Series) production).numberSeasons);

                // print the list of episodes
                Map<String, Object> seasons = new HashMap<>();

                Map<String, List<Episode>> seasonsMap = ((Series) production).returnList();

                for (Map.Entry<String, List<Episode>> entry : seasonsMap.entrySet()) {
                    String season = entry.getKey();
                    List<Episode> episodeList = entry.getValue();

                    List<Object> listOfEpisodes = new ArrayList<>();

                    for (Episode episode : episodeList) {
                        // add each episode
                        Map<String, Object> aux_episode = new HashMap<>();
                        aux_episode.put("episodeName", episode.getEpisodeName());
                        aux_episode.put("duration", episode.getDuration());

                        listOfEpisodes.add(aux_episode);
                    }

                    seasons.put(season, episodeList);
                }

                jsonMap.put("seasons", seasons);
            }

            jsonList.add(jsonMap);
        }

        return  jsonList;
    }

    // method for debugging the writing methods to the JSON
    public static boolean verify(List<Production> aux) {

        if (aux.size() != production_list.size())
            return false;

        for (int i = 0; i < aux.size(); i++) {
            Production original, copy;
            original = production_list.get(i);
            copy = aux.get(i);

            if (original instanceof Movie != copy instanceof Movie)
                return false;
            if (original.title.equals(copy.title) == false)
                return false;
            if (original.type.equals(copy.type) == false)
                return false;
            if (original.plot.equals(copy.plot) == false)
                return false;
            if (original.directors.containsAll(copy.directors) == false || copy.directors.containsAll(original.directors) == false)
                return false;
            if (original.actors.containsAll(copy.actors) == false || copy.actors.containsAll(original.actors) == false)
                return false;
            if (original.genre.containsAll(copy.genre) == false || copy.genre.containsAll(original.genre) == false)
                return false;
            List<Rating> aaa = original.getRating();
            List<Rating> bbb = original.getRating();
            if (aaa.containsAll(bbb) == false || bbb.containsAll(aaa) == false) {
                return false;
            }

        }
        return true;
    }
}