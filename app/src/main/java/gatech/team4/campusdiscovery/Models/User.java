package gatech.team4.campusdiscovery.Models;

public class User {
    protected String name;
    protected String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void changeName(String newName) {
        this.name = newName;
    }

    public void changeEmail(String newEmail) {
        this.email = newEmail;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return String.format("%s", email);
    }
}
