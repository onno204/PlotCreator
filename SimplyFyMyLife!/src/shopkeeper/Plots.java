package shopkeeper;

import java.util.ArrayList;
import java.util.List;

public enum Plots {
	Spawn (104, "spawn") ,
	StartersHuisje (19, "startershuisje") ;

    private final int AantalPlots;
    private final String AfKorting;

    Plots(int AantalPlots, String AfKorting) {
        this.AantalPlots = AantalPlots;
        this.AfKorting = AfKorting;
    }
    
    public int GetAantal() {
        return this.AantalPlots;
    }
    public String getAfkorting() {
        return this.AfKorting;
    }
    public static List<Plots> GetPlots() {
    	List<Plots> plots = new ArrayList<Plots>();
    	plots.add(Spawn);
    	plots.add(StartersHuisje);
    	
        return plots;
    }
}
