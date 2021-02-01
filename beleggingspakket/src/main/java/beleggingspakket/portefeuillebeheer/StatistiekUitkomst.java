package beleggingspakket.portefeuillebeheer;

import java.util.ArrayList;

public class StatistiekUitkomst {
    private int aantalGebeurtenissen;

    ArrayList<Double> Kh = new ArrayList<>(); // kansen op koers "higher" dan k0 + delta% na n dagen
    ArrayList<Double> Kl = new ArrayList<>(); // kansen op "lower" dan k0 - delta% na n dagen
}
