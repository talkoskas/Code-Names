package GameObjects;

import javax.xml.bind.annotation.XmlElement;


public class Player {
    private String name, team, roomName;
    private boolean definer;

    public Player(String name){
        this.name = name;
        definer = false;
    }

    public Player(String name, boolean definer){
        this.name = name;
        this.definer = definer;
    }

    public Player(String name, String team) {
        this.name = name;
        this.team = team;
        definer = false;
    }

    public Player(String name, String team, boolean definer) {
        this.name = name;
        this.team = team;
        this.definer = definer;
    }

    public Player(String name, String team, boolean definer, String roomName) {
        this.name = name;
        this.team = team;
        this.definer = definer;
        this.roomName = roomName;
    }


    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @XmlElement
    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    @XmlElement
    public boolean isDefiner() {
        return definer;
    }

    public void setDefiner(boolean definer) {
        this.definer = definer;
    }








    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            Player p = (Player) obj;
            return name.equals(p.name);
        }
        return false;
    }
}
