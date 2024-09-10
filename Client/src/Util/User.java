package Util;

import WebAppObjects.Objects.Room;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private boolean admin;
    private int adminMaxCommand, clientMaxCommand;
    private Room selectedRoom;

    public User(String username, boolean admin) {
        if (username == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        this.username = username;
        this.admin = admin;
        this.selectedRoom = null;
        this.clientMaxCommand = Config.CLIENT_JOIN_INT;
        if(admin){
            this.adminMaxCommand = Config.FILE_UPLOAD_COMMAND_INT;
        }
    }

    public User(String username, boolean admin, int maxCommand) {
        if (username == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        this.username = username;
        this.admin = admin;
        this.selectedRoom = null;
        this.clientMaxCommand = Config.CLIENT_JOIN_INT;
        this.adminMaxCommand = Config.FILE_UPLOAD_COMMAND_INT;

        if(admin){
            this.adminMaxCommand = maxCommand;
        }
        else{
            this.clientMaxCommand = maxCommand;
        }

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAdminMaxCommand(){ return adminMaxCommand;}

    public void setAdminMaxCommand(int maxCommand) { this.adminMaxCommand = maxCommand;}

    public int getClientMaxCommand(){ return clientMaxCommand; }

    public void setClientMaxCommand(int maxCommand) { this.adminMaxCommand = maxCommand;}

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Room getSelectedRoom() { return selectedRoom;}

    public void setSelectedRoom(Room selectedRoom) { this.selectedRoom = selectedRoom; }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }
}
