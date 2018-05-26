package xet.server;

import java.util.ArrayList;

public class UsersManager {
    private static UsersManager instance;
    public static UsersManager Get() {
        if (instance == null) instance = new UsersManager();
        return instance;
    }

    private ArrayList<User> users;

    public UsersManager() {
        users = new ArrayList<>();
    }

    public boolean addUser(User user) {
        if (getUser(user.getId()) == null) {
            return users.add(user);
        }
        return false;
    }

    public User getUser(String id) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) return users.get(i);
        }
        return null;
    }

    public String getUserName(String id) {
        User user = getUser(id);
        if (user != null) return user.getName();
        return "anonymous";
    }

    public void print() {
        System.out.println("Users: " + users.size());
        for (int i = 0; i < users.size(); i++) {
            System.out.println(users.get(i).getId() + ": " + users.get(i).getName());
        }
    }
}
