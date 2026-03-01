package users;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.LocalDateTime;

import ObserverPattern.*;
import ObserverPattern.Observer;
import main_default.IMDB;
import main_default.MyGUI;
import productions.Production;

// define abstract class
public abstract class User implements Observer {

    // clasa interna
    public static class Information{
        private Credentials credentials;
        private LocalDateTime birthDate;
        public String name, country;
        public int age;
        public String gender;

        private Information(InformationBuilder builder) {
            this.country = builder.country;
            this.name = builder.name;
            this.age = builder.age;
            this.gender = builder.gender; // make it as a char from string
            this.credentials = builder.information;
            this.birthDate = builder.date_of_birth;
        }

        public void setDate_of_birth(LocalDateTime data_nasterii){
            this.birthDate = data_nasterii;
        }

        public LocalDateTime getDate_of_birth() {
            return birthDate;
        }

        public void setInformation(Credentials information) {
            this.credentials = information;
        }

        public Credentials getInformation() {
            return credentials;
        }

        public String toString() {
            String str = "";
            str += "Name: " + name + "\n";
            str += "Country: " + country + "\n";
            str += "Age: " + age + "\n";
            str += "Gender: " + gender + "\n\n";

            str += "Credentials: " + "\n";
            str += "Email: " + credentials.getEmail() + "\n";
            str += "Password: " + credentials.getPassword() + "\n";

            return str;
        }

        // define builder class
        public static class InformationBuilder {
            private Credentials information;
            private String name, country;
            private int age;
            private String gender;
            private LocalDateTime date_of_birth;

            public InformationBuilder name(String name) {
                this.name = name;
                return this;
            }

            public InformationBuilder country(String country) {
                this.country = country;
                return this;
            }

            public InformationBuilder date_of_birth(LocalDateTime date_of_birth) {
                this.date_of_birth = date_of_birth;
                return this;
            }

            public InformationBuilder age(int age) {
                this.age = age;
                return this;
            }

            public InformationBuilder gender(String gender) {
                this.gender = gender;
                return this;
            }

            public InformationBuilder information(Credentials information) {
                this.information = information;
                return this;
            }

            public Information build() {
                return new Information(this);
            }
        }
    }

    // se defineste enumeratia

    private Information information;
    private AccountType userType;
    public String username;
    private int experience;
    private ExperienceStrategy experienceStrategy; // for the design pattern
    public List<String> notifications;
    public SortedSet<Object> favorites;

    // getteri is setteri
    public void setExperience(Object experienta){
        if (experienta == null)
            this.experience = 0;
        else
            this.experience = (int) Long.parseLong(experienta.toString());
    }
    public int getExperience(){
        return experience;
    }

    public void setTip(String str){
        switch (str) {
            case "Regular":
                userType = AccountType.REGULAR;
                break;
            case "Admin":
                userType = AccountType.ADMIN;
                break;
            case "Contributor":
                userType = AccountType.CONTRIBUTOR;
                break;
        }
    }

    public void setInformation(Credentials credentials,
                               String name,
                               String country,
                               int age,
                               String gender,
                               String date_of_birth) {

        // create new information object using builder design pattern
        Information.InformationBuilder builder = new Information.InformationBuilder();

        String aux = MyGUI.isDOB(date_of_birth, "yyyy-MM-dd");

        if (aux != null) {
            date_of_birth = aux;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateTimeOfBirth = LocalDateTime.parse(date_of_birth, formatter);


        builder.information(credentials)
                .name(name)
                .country(country)
                .age(age)
                .date_of_birth(dateTimeOfBirth)
                .gender(gender);


        this.information = builder.build();
    }

    public String getExperientaAsString() {
        if (this instanceof Admin) {
            return "0";
        }
        return experience + "";
    }
    public String getEmail() {
        return information.getInformation().getEmail();
    }

    public String getPassword() {
        return information.getInformation().getPassword();
    }

    public Information getInfo(){
        return information;
    }

    public void setTip(AccountType tip){
        this.userType = tip;
    }

    public AccountType getTip(){
        return userType;
    }

    public String getTypeAsString() {
        switch (userType) {
            case CONTRIBUTOR :
                return "Contributor";
            case ADMIN:
                return "Admin";
            case REGULAR:
                return "Regular";
        }
        return "";
    }

    // constructor
    public User(){
        this("", 0);
    }

    public User(String username){
        this(username, 0);
    }

    // 2nd constructor
    public User(String username, int experienta) {
        // assign value
        this.username = username;
        this.experience = experienta;

        // allocate memory
        notifications = new ArrayList<>();
        favorites = new TreeSet<>();
        information = new Information.InformationBuilder().build();
    }

    public void setCredentials(Credentials informatii){
        information.setInformation(informatii);
    }

    // metode pentru modificarea experientei
    public void addExperience(int inc){
        // inc poate sa fie negativ
        experience += inc;
    }

    // static method to return the enum based on the string
    public static AccountType returnEnum(String accountType) {
        switch (accountType) {
            case "Admin" :
                return AccountType.ADMIN;
            case "Contributor":
                return AccountType.CONTRIBUTOR;
            default:
                return AccountType.REGULAR;
        }
    }

    // method to add into the favorites list
    public boolean addFavorite(Object obj){
        // check if the object is null
        if (obj == null)
            return false;

        // add this user as an observer for all the ratings
        if (obj instanceof Production) {
            ((Production) obj).production_observers.add(this);
        }

        // add it into the list
        favorites.add(obj);
        return true;
    }

    // method to remove from the favorites list
    public boolean deleteFavorite(Object obj){
        // check if it is a valid object
        if (obj == null)
            return false;

        favorites.remove(obj);

        return true;
    }

    public String toString() {
        String str = "Username: " + username + "\n";

        str += "Type: " + this.getTypeAsString() + "\n";
        str += "Experience: " + experience + "\n";
        str += "Informations: " + "\n";
        str += information + "\n";

        return str;
    }

    public boolean logoutUser(){
        if (IMDB.login == false)
            return false;

        IMDB.login = false;
        return true;
    }

    // method to generate a username based on the name
    public static String generateUsername(String name) {
        String username = name;

        // generate the username based on the name
        username = username.replace(' ', '_').toLowerCase();
        username += '_';

        // generate a random number of 4 digits
        Random random = new Random();
        int randomNumber = random.nextInt(10000);

        // add random number
        username += randomNumber;

        // check if the username exists
        User user = IMDB.find_user_by_username(username);
        if (user != null) {
            generateUsername(name);
            System.out.println("Warning generated username: " + username + " already exists!");
        }

        return username;
    }

    // method to generate a password based on the name
    public static String generatePassword(String name) {
        // generate random string
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-=_+[]{}';:|/?.,<>`~";
        String password = "";

        // generate random number
        Random random = new Random();
        int length = (name.length() > 8) ? name.length() : 10 ;

        // create the password with random chars from the string
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            password += chars.charAt(randomIndex);
        }

        return password;
    }

    // method that prints the notifications of the current user
    public String view_notifications() {
        String str = "";
        if (this.notifications.size() != 0) {
            str += "Your notifications are : \n";
            int integer = 1;
            // traverse all the strings from the list
            for (String notification : this.notifications) {
                str +="\t\t" + (integer++) + ". " + notification + "\n";
            }
        } else {
            str += "You don't have any notifications!";
        }
        return str;
    }

    // for the observer design pattern
    public void update(String notification) {
        notifications.add(notification);
    }
}