package users;


public class Credentials {

    private String password;
    private String email;

    // constructor
    public Credentials(String email, String parola){
        this.email = email;
        this.password = parola;
    }

    public Credentials(){
        this("","");
    }

    // getteri

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // setteri


    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String parola) {
        this.password = parola;
    }

    public String toString() {
        return "email: " + email +
                "\tpassword: " + password + "\n";
    }

}