package models;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by mike on 5 Apr 2018.
 */

public class List {
String name;
ArrayList<String> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }
}
