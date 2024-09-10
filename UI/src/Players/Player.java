package Players;

public class Player {
    private final String name;
    private final boolean definer;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return player.getName().equals(this.getName());
    }
}
