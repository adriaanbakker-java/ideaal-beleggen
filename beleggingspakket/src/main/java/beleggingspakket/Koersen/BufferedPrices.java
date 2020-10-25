package beleggingspakket.Koersen;

import java.util.ArrayList;
import java.util.HashMap;

public class BufferedPrices {

    HashMap<String, ArrayList<DayPriceRecord>> bufferedPrices = null;

    public BufferedPrices() {
        bufferedPrices = new HashMap<>();
    }

    void addPrices(String aTicker) throws Exception {
        if (!bufferedPrices.containsKey(aTicker)) {
            GetPriceHistory gph = new GetPriceHistory();
            ArrayList<DayPriceRecord> prices =
                    gph.getHistoricPricesFromFile(aTicker);
            bufferedPrices.put(aTicker, prices);
        }
    }

    public Double getClosePrice(String aTicker,
                                int aYear,
                                int aMonth,
                                int aDay) throws Exception {
        if (!bufferedPrices.containsKey(aTicker)) {
            addPrices(aTicker);
        }
        ArrayList<DayPriceRecord> prices = bufferedPrices.get(aTicker);

        int i= 0;
        DayPriceRecord dpr = null;
        boolean found = false;
        while ((!found) &&
                (i < prices.size())) {
            dpr = prices.get(i);
            found = ((dpr.getYear() == aYear) &&
                    (dpr.getMonth() == aMonth) &&
                    (dpr.getDay() == aDay));
            i++;
        }
        if (!found)
            return null;
        return dpr.getClose();
    }


}
