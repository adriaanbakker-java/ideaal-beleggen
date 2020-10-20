package beleggingspakket.portefeuillebeheer;

import java.util.ArrayList;

public class Posities {
    private ArrayList<Positie> posities = new ArrayList<>();

    public ArrayList<Positie> getPosities() {
        return posities;
    }

    public void addPositie(Positie positie) {
        posities.add(positie);
    }


}
