package main_default;

import productions.Actor;
import productions.Production;
import requests.Request;
import users.*;

import java.util.*;

public class MyCLI {

    Scanner scanner;
    public static List<String> template_list;

    public MyCLI() {
        scanner = new Scanner(System.in);
    }

    public String read_from_terminal(String str) {
        System.out.print(str);
        String line = "";

        line = scanner.nextLine();
        return line;
    }

    public void cleanup() {
        scanner.close();
    }

    public int print_template(User user) {
        // create the string to be printed
        String output = "User experience: " + user.getExperience() + "\n" + "Choose action:\n";
        int index = 1;

        for (String str : template_list) {
            output += "\t" + (index++) + ") " + str + "\n";
        }

        System.out.print(output);

        String input = read_from_terminal("Select an option: ");

        try {
            int inputAsInt = Integer.parseInt(input);
            if (inputAsInt >= 1 && inputAsInt <= 11 && template_list.size() >= inputAsInt) {
                return inputAsInt;
            } else {
                System.out.println("-->Error: The input is not a valid integer between 1 and " + template_list.size() + "!");
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("-->Error: Invalid integer format!");
            return -1;
        }
    }

    // create template array
    public static void create_template_array(User user) {
        template_list = new ArrayList<>();

        // add basic operations that are for all the users
        template_list.add("View productions details");
        template_list.add("View actors details");
        template_list.add("View notifications");
        template_list.add("Search for actor/movie/series");
        template_list.add("Add/Delete actor/movie/series to/from favorites");

        if (user instanceof Regular) {
            template_list.add("Create/Delete a request");
            template_list.add("Add/Delete review for production");
        } else if (user instanceof Contributor) {
            template_list.add("Create/Delete a request");
            template_list.add("Add/Delete actor/movie/series from system");
            template_list.add("Update Movie details");
            template_list.add("Update Actor details");
            template_list.add("Solve a request");
        } else if (user instanceof Admin) {
            template_list.add("Add/Delete actor/movie/series from system");
            template_list.add("Update Movie details");
            template_list.add("Update Actor details");
            template_list.add("Solve a request");
            template_list.add("Add/Delete user");
        }

        // add logout option
        template_list.add("Logout");
        template_list.add("Exit");
    }

    // function that returns the user with that username
    public static Optional<User> search_by_email(List<User> user_list, String input_email) {
        return user_list.stream()
                .filter(user -> user.getEmail().equals(input_email))
                .findFirst();
    }

    // function that makes the login and returns the object based on the type
    public User login(List<User> user_list) {
        // create user object
        User user = null;
        boolean success = false;

        System.out.println("Welcome back! Enter your credentials!\n");
        while (!success) {
            // read the username
            String email = read_from_terminal("\t\temail: ");
            Optional<User> found_user = search_by_email(user_list, email);

            // check if a user exists
            if (found_user.isPresent() == false) {
                System.out.println("The email : \"" + email + "\" does not exist");
                continue;
            }

            // get the user
            user = found_user.get();

            // read the password
            String password = read_from_terminal("\t\tpassword: ");

            if (user.getPassword().equals(password) == true) {
                // create the list with operations based on the user
                create_template_array(user);
                System.out.println("\n--Welcome back user " + user.username + "!--\n");
                success = true;
            }
        }

        return user;
    }

    // method that returns a string with all the actors details from the system
    public String view_actors(List<Actor> actor_list) {
        String str = "These are all the actors in the system: \n";

        for (Object obj : actor_list) {
            Actor actor = (Actor) obj;
            str += actor.toString();
        }

        return str;
    }

    public void clear() {
        System.out.println("\n".repeat(50));
    }

}
