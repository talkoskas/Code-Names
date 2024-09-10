package Players;

public class Player {
    String name;
    boolean definer;

    public Player(String name, boolean definer) {
        this.name = name;
        this.definer = definer;
    }

    public Player(String name) {
        this.name = name;
        this.definer = false;
    }

    public String getName() {
        return name;
    }

    public boolean isDefiner() {
        return definer;
    }
}
