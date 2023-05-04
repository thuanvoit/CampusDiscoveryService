package gatech.team4.campusdiscovery.Models;

import java.io.Serializable;

import gatech.team4.campusdiscovery.R;

public class CampusActivity implements Serializable {
    private String subject;
    private String info;
    private String location;
    private String date;
    private String time;
    private String host;
    private int image;
    private int inviteOnly;

    public CampusActivity(String[] args, int image, int inviteOnly) {
        this.subject = args[0];
        this.info = args[1];
        this.location = args[2];
        this.date = args[3];
        this.time = args[4];
        this.host = args[5];
        this.image = image;
        this.inviteOnly = inviteOnly;
    }

    public CampusActivity(String[] args, int inviteOnly) {
        this(args, R.drawable.activity_default_image, inviteOnly);
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public void setHost(String organizer) {
        this.host = organizer;
    }

    public String getHost() {
        return host;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getInviteOnly() {
        return inviteOnly;
    }

    public void setInviteOnly(int inviteOnly) {
        this.inviteOnly = inviteOnly;
    }
}