package gatech.team4.campusdiscovery.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Objects;

import gatech.team4.campusdiscovery.Models.CampusActivity;
import gatech.team4.campusdiscovery.Models.User;

public class DBHelper extends SQLiteOpenHelper {
    private final String delimiter = "__,__";

    public DBHelper(Context context) {
        super(context, "CampusActivities.db",
                null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Users(email TEXT primary key, name TEXT, role INTEGER)");
        db.execSQL("create Table Activities(subject TEXT primary key, info TEXT, location TEXT,"
                + "date TEXT, time TEXT, host TEXT, image INT,"
                + "attending TEXT, potentiallyAttending TEXT,"
                + "inviteOnly INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int ii) {
        db.execSQL("drop Table if exists Users");
        db.execSQL("drop Table if exists Activities");
    }

    public boolean insertUserData(String email, String name, int role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("name", name);
        contentValues.put("role", role);
        return -1 != db.insert("Users", null, contentValues);
    }

    public boolean insertActivityData(String[] args, int image, int inviteOnly) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("subject", args[0]);
        contentValues.put("info", args[1]);
        contentValues.put("location", args[2]);
        contentValues.put("date", args[3]);
        contentValues.put("time", args[4]);
        contentValues.put("host", args[5]);
        contentValues.put("image", image);
        contentValues.put("attending", "");
        contentValues.put("potentiallyAttending", "");
        contentValues.put("inviteOnly", inviteOnly);

        long result = db.insert("Activities", null, contentValues);
        return result != -1;
    }

    public ArrayList<String> getPotentiallyAttending(String subject) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Activities WHERE subject = ?",
                new String[]{subject});
        c.moveToFirst();
        if (c.getCount() > 0) {
            ArrayList<String> lst = convertStringToList(c.getString(8));
            c.close();
            return lst;
        }
        c.close();
        return null;
    }

    public ArrayList<String> getAttending(String subject) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Activities WHERE subject = ?",
                new String[]{subject});
        c.moveToFirst();
        if (c.getCount() > 0) {
            ArrayList<String> lst = convertStringToList(c.getString(7));
            c.close();
            return lst;
        }
        c.close();
        return null;
    }

    public ArrayList<User> getAttendingUser(String subject) {
        ArrayList<String> attendingEmail = getAttending(subject);
        ArrayList<User> attendingUser = new ArrayList<>();
        for (int i = 0; i < attendingEmail.size(); i++) {
            attendingUser.add(getUserFromEmail(attendingEmail.get(i)));
        }
        return attendingUser;
    }

    public ArrayList<User> getPotentiallyAttendingUser(String subject) {
        ArrayList<String> attendingEmail = getPotentiallyAttending(subject);
        ArrayList<User> attendingUser = new ArrayList<>();
        for (int i = 0; i < attendingEmail.size(); i++) {
            attendingUser.add(getUserFromEmail(attendingEmail.get(i)));
        }
        return attendingUser;
    }

    public int getNumberOfAttendingUser(String subject) {
        return getAttending(subject).size();
    }

    public int getNumberOfPotentiallyAttendingUser(String subject) {
        return getPotentiallyAttending(subject).size();
    }

    public boolean removeAttending(String subject, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> attending = getAttending(subject);
        if (attending == null) {
            return false;
        }
        if (attending.contains(email)) {
            attending.remove(email);
        } else {
            return false;
        }
        ContentValues newAttendingArray = new ContentValues();
        newAttendingArray.put("attending", convertListToString(attending));
        db.update("Activities", newAttendingArray, "subject = ?",
                new String[]{subject});
        return true;
    }

    public boolean removePotentiallyAttending(String subject, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> potentiallyAttending = getPotentiallyAttending(subject);
        if (potentiallyAttending == null) {
            return false;
        }
        if (potentiallyAttending.contains(email)) {
            potentiallyAttending.remove(email);
        }
        ContentValues newPotentiallyAttendingArray = new ContentValues();
        newPotentiallyAttendingArray.put("potentiallyAttending",
                convertListToString(potentiallyAttending));
        db.update("Activities", newPotentiallyAttendingArray, "subject = ?",
                new String[]{subject});
        return true;
    }

    public boolean addAttendingUser(String subject, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> attending = getAttending(subject);
        if (attending == null) {
            return false;
        }
        for (String user : attending) {
            if (user.equals(email)) {
                return false;
            }
        }
        if (getPotentiallyAttending(subject).contains(email)) {
            removePotentiallyAttending(subject, email);
        }
        attending.add(email);
        ContentValues newAttendingArray = new ContentValues();
        newAttendingArray.put("attending", convertListToString(attending));
        db.update("Activities", newAttendingArray, "subject = ?",
                new String[]{subject});
        return true;
    }

    public boolean addPotentiallyAttendingUser(String subject, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> potentiallyAttending = getPotentiallyAttending(subject);
        if (potentiallyAttending == null) {
            return false;
        }
        for (String user : potentiallyAttending) {
            if (user.equals(email)) {
                return false;
            }
        }

        potentiallyAttending.add(email);
        ContentValues newPotentiallyAttendingArray = new ContentValues();
        newPotentiallyAttendingArray.put("potentiallyAttending",
                convertListToString(potentiallyAttending));
        db.update("Activities", newPotentiallyAttendingArray, "subject = ?",
                new String[]{subject});
        return true;
    }

    private String convertListToString(ArrayList<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : list) {
            Log.i("DEBUG DBHELPER", s);
            stringBuilder.append(s).append(delimiter);
        }
        Log.i("DEBUG DBHELPER", stringBuilder.toString());

        if (stringBuilder.length() - delimiter.length() >= 0) {
            stringBuilder.setLength(stringBuilder.length() - delimiter.length());
        } else {
            stringBuilder.setLength(0);
        }
        return stringBuilder.toString();
    }

    private ArrayList<String> convertStringToList(String str) {
        String[] strSplit = str.split(delimiter);
        ArrayList<String> lst = new ArrayList<>();
        for (int i = 0; i < strSplit.length; i++) {
            if (!strSplit[i].trim().equalsIgnoreCase("")
                    && strSplit[i] != null) {
                lst.add(strSplit[i]);
            }
        }
        return lst;
    }

    public ArrayList<User> getUserData() {
        Cursor c = getAllUserData();
        ArrayList<User> userList = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                User user = new User(
                        c.getString(1),
                        c.getString(0)
                );
                userList.add(user);
            }
        } catch (SQLiteException e) {
            Log.d("SQL error parsing User data!", e.getMessage());
            return null;
        } finally {
            c.close();
        }
        return userList;
    }

    public ArrayList<CampusActivity> getUserRSVPedActivityData(String email) {
        ArrayList<CampusActivity> activities = getActivityData();
        ArrayList<CampusActivity> attendingActivities = new ArrayList<>();
        ArrayList<User> users;
        for (CampusActivity activity : activities) {
            users = getAttendingUser(activity.getSubject());
            for (User u : users) {
                if (u.getEmail().equals(email)) {
                    attendingActivities.add(activity);
                    Log.i("DEBUG RSVPED", activity.getSubject());
                    break;
                }
            }
        }
        return attendingActivities;
    }

    public ArrayList<CampusActivity> getUserPotentiallyRSVPedActivityData(String email) {
        ArrayList<CampusActivity> activities = getActivityData();
        ArrayList<CampusActivity> potentiallyAttendingActivities = new ArrayList<>();
        ArrayList<User> users;
        for (CampusActivity activity : activities) {
            users = getPotentiallyAttendingUser(activity.getSubject());
            for (User u : users) {
                if (u.getEmail().equals(email)) {
                    potentiallyAttendingActivities.add(activity);
                    break;
                }
            }
        }
        return potentiallyAttendingActivities;
    }


    public ArrayList<CampusActivity> getFilteredActivityData(String organizer, String location,
                                                             String date) {
        ArrayList<CampusActivity> activities = getActivityData();
        CampusActivity activity;
        for (int i = 0; i < activities.size(); ++i) {
            activity = activities.get(i);
            Log.d("Parsing filtered data", activity.getSubject());
            if (!organizer.isEmpty() && !activity.getHost().equals(organizer)) {
                activities.set(i, null);
            } else if (!location.isEmpty() && !activity.getLocation().split("__,__")[0]
                    .equals(location)) {
                activities.set(i, null);
            } else if (!date.isEmpty() && !activity.getDate().equals(date)) {
                activities.set(i, null);
            }
        }
        activities.removeIf(Objects::isNull);
        return activities;
    }

    public ArrayList<CampusActivity> getUserFilteredActivityData(String email, String organizer,
                                                                 String location, String date) {
        ArrayList<CampusActivity> activities = getFilteredActivityData(organizer, location, date);
        ArrayList<CampusActivity> attendingActivities = new ArrayList<>();
        ArrayList<User> users;
        for (CampusActivity activity : activities) {
            users = getAttendingUser(activity.getSubject());
            for (User u : users) {
                if (u.getEmail().equals(email)) {
                    attendingActivities.add(activity);
                    Log.i("DEBUG RSVPED", activity.getSubject());
                    break;
                }
            }
        }
        return attendingActivities;
    }

    public ArrayList<CampusActivity> getUserFilteredPotentiallyActivityData(
            String email, String organizer, String location, String date) {
        ArrayList<CampusActivity> activities = getFilteredActivityData(organizer, location, date);
        ArrayList<CampusActivity> attendingActivities = new ArrayList<>();
        ArrayList<User> users;
        for (CampusActivity activity : activities) {
            users = getAttendingUser(activity.getSubject());
            for (User u : users) {
                if (u.getEmail().equals(email)) {
                    attendingActivities.add(activity);
                    Log.i("DEBUG RSVPED", activity.getSubject());
                    break;
                }
            }
        }
        return attendingActivities;
    }

    public ArrayList<CampusActivity> getActivityData() {
        Cursor c = getAllActivityData();
        ArrayList<CampusActivity> activityList = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                String[] args = {
                        c.getString(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5)
                };
                CampusActivity activity = new CampusActivity(
                        args,
                        c.getInt(6),
                        c.getInt(9)
                );
                Log.i("INVITE ONLY DBHELPER", "" + c.getInt(9));
                activityList.add(activity);
            }
        } catch (SQLiteException e) {
            Log.d("SQL error parsing Activity data!", e.getMessage());
            return null;
        } finally {
            c.close();
        }
        return activityList;
    }

    public Cursor getAllUserData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from Users", null);
    }

    public ArrayList<CampusActivity> getActivityFromEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM activities WHERE host = ?",
                new String[]{email});
        ArrayList<CampusActivity> activityList = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                String[] args = {
                        c.getString(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5)
                };
                CampusActivity activity = new CampusActivity(
                        args,
                        c.getInt(6),
                        c.getInt(9)
                );
                Log.i("INVITE ONLY DBHELPER", "" + c.getInt(9));
                activityList.add(activity);
            }
        } catch (SQLiteException e) {
            Log.d("SQL error parsing Activity data!", e.getMessage());
            return null;
        } finally {
            c.close();
        }
        return activityList;
    }

    public User getUserFromEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Users WHERE email = ?",
                new String[]{email});
        c.moveToFirst();
        if (c.getCount() > 0) {
            User user = new User(
                    c.getString(1),
                    c.getString(0)
            );
            c.close();
            return user;
        }
        c.close();
        return null;
    }

    public int getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Users WHERE email = ?", new String[]{email});
        c.moveToFirst();
        if (c.getCount() > 0) {
            int i = c.getInt(2);
            c.close();
            return i;
        }
        c.close();
        return -1;
    }

    public void updateUserInfo(String oldEmail, String newEmail, String newName) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues newUserData = new ContentValues();
        newUserData.put("name", newName);
        newUserData.put("email", newEmail);
        db.update("Users", newUserData, "email = ?",
                new String[]{oldEmail});
    }

    public void updateActivitiesHost(String oldEmail, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues newHostEmail = new ContentValues();
        newHostEmail.put("host", email);
        db.update("Activities", newHostEmail, "host = ?",
                new String[]{oldEmail});
    }

    public Cursor getAllActivityData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from Activities", null);
    }

    public CampusActivity getCampusActivityFromSubject(String subject) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = getAllActivityData();
        int columnId = c.getColumnIndexOrThrow(subject);
        c.move(columnId);
        String[] args = {
                c.getString(0),
                c.getString(1),
                c.getString(2),
                c.getString(3),
                c.getString(4),
                c.getString(5)
        };
        CampusActivity activity = new CampusActivity(
                args,
                c.getInt(6),
                c.getInt(9)
        );
        c.close();
        return activity;
    }

    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Users", "email = ?", new String[]{email}) > 0;
    }

    public boolean deleteActivity(String subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Activities", "subject = ?", new String[]{subject}) > 0;
    }
}
