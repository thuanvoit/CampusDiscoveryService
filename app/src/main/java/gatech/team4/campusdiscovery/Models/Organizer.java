package gatech.team4.campusdiscovery.Models;

import gatech.team4.campusdiscovery.Database.DBHelper;

public class Organizer extends User {
    private String eventName;

    public Organizer(String name, String email) {
        this(name, email, null);
    }

    public Organizer(String name, String email, String eventName) {
        super(name, email);
        this.eventName = eventName;
    }

    public CampusActivity getEvent(DBHelper db) {
        return db.getCampusActivityFromSubject(eventName);
    }
}
