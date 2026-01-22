package main_default;

import users.*;

public class UserFactory {

    public static User factory(AccountType accountType) {

        // check the type of account
        switch (accountType) {
            case ADMIN :
                return new Admin();
            case REGULAR:
                return new Regular();
            case CONTRIBUTOR:
                return new Contributor();
        }

        // return if it is not a valid account type
        System.out.println("Error not a valid type of account!");
        return null;
    }
}
