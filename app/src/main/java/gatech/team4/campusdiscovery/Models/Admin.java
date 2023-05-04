package gatech.team4.campusdiscovery.Models;

public class Admin extends User {
    private CampusActivity event;

    public Admin(String name, String email) {
        this(name, email, null);
    }

    public Admin(String name, String email, CampusActivity event) {
        super(name, email);
        this.event = event;
    }

    public void setEvent(CampusActivity event) {
        this.event = event;
    }
}
