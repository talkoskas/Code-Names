package Players;

public class Definer extends Player {
    String currDef;

    public Definer(String name){
        super(name, true);
        this.currDef = "";
    }

    public void setDef(String def){
        this.currDef = def;
    }

    public String getDef(){
        return this.currDef;
    }

}
