package WebAppObjects.Managers;

import WebAppObjects.Objects.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private final Map<String, User> users;
    private User admin;

    public UserManager() {
        this.users = new ConcurrentHashMap<>();

    }

    public String addUser(User user) {
        if (user == null || user.getUsername() == null) {
            return "Invalid user";
        }
        if(user.isAdmin()&&admin!=null&&!admin.equals(user)){
            return "Admin already exists";
        }
        if(user.isAdmin()&&admin==null){
            admin = user;
        }
        if (users.putIfAbsent(user.getUsername(), user) != null) {
            return "User already exists";
        }

        return "Created";
    }

    public String removeUser(User user) {
        if(user == null){
            return "Trying to delete a null user";
        }
        if(user.getUsername() == null){
            return "Trying to delete a null username";
        }

        synchronized (this.users) {
            if (!this.users.containsKey(user.getUsername())) {
                return "Trying to log out of a user that isn't logged in";
            }
            this.users.remove(user.getUsername());
        }
        if(user.isAdmin()){
            admin = null;
        }
        return "Deleted";
    }

    public User containsUser(String username) {
        synchronized (this.users) {
            if(username == null){
                System.out.println("Username is null!!!!");
                return null;
            }
            return users.get(username);
        }
    }


    public String getAllUsernames(){
        StringBuilder sb = new StringBuilder();
        synchronized (this.users){
            for (User user : this.users.values()) {
                sb.append(user.getUsername());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public User getAdmin(){ return admin; }

    public void setAdmin(User admin) {
        this.admin = admin;
    }


}


