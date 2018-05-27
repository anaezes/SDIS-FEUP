package xet.server.users;

import java.util.ArrayList;

public class UsersManager {
    private static final UsersManager instance = new UsersManager();
    private static int guests = 0;
    public static UsersManager Get() {
        return instance;
    }

    private ArrayList<User> users;

    public UsersManager() {
        users = new ArrayList<>();
    }

    public boolean addUser(User user) {
        if (getUser(user.getId()) == null) {
            if (user.getProviderId().equals("guest")) guests++;
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

    public boolean exists(String id) {
        return getUser(id) != null;
    }

    public int getGuests() {
        return guests;
    }
}
