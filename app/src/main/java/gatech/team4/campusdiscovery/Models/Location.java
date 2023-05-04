package gatech.team4.campusdiscovery.Models;

public class Location {

    private String building;
    private String room;
    private double lat;
    private double lon;

    public Location(String building, String room, double lat, double lon) {
        this.building = building;
        this.room = room;
        this.lat = lat;
        this.lon = lon;
    }

    public Location(String building, double lat, double lon) {
        this(building, "", lat, lon);
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return String.format("%s %s", building, room);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
