package nl.beleggingspakket.grafiekenscherm;

public class Line {
    private  int aantalBeursdagenKoersreeks = 0;

    public int getAantalBeursdagenKoersreeks() {
        return aantalBeursdagenKoersreeks;
    }

    public int getStartindex() {
        return startindex;
    }

    private int startindex = 0;

    public int getSeqnr() {
        return seqnr;
    }

    private int seqnr;
    private Point p1;
    private Point p2;


    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }


    /*
    *    After zoom the nr of trading days will change and thus the slope of the original line
    *    After zoom/pan the relative position of the first point in the line in the graph will change.
    *
    */
    public Line(Point aPoint1, Point aPoint2, int aStartindex, int aAantalBeursdagenKoersreeks, int aSeqnr) {
        p1 = aPoint1;
        p2 = aPoint2;
        seqnr = aSeqnr;
        startindex = aStartindex;
        aantalBeursdagenKoersreeks = aAantalBeursdagenKoersreeks;
    }
}
