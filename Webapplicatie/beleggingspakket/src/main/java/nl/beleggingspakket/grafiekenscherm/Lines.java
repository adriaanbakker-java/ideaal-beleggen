package nl.beleggingspakket.grafiekenscherm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Lines implements Iterable <Line> {
    private List<Line> myLines = new ArrayList<>();

    public void addLine(Line aLine) {
        myLines.add(aLine);
    }

    @Override
    public Iterator<Line> iterator() {
        return this.myLines.iterator();
    }

    // Zoek de vertikale lijn waarop is geklikt
    // en verwijder deze. Let op: Line kan een kopie zijn ivm scaling bij zoom in/out
    public void delLine(int aSeqnr) {
        Line myLine = null;
        for (Line aLine: myLines) {
            if (aSeqnr == aLine.getSeqnr()) {
                myLine = aLine;
            }
        }
        if (myLine != null)
            myLines.remove(myLine);
    }
}
