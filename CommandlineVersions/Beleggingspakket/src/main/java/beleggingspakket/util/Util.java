package beleggingspakket.util;

import java.text.DecimalFormat;

public class Util {

    public static String toCurrency(double aBedrag) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        String formatted = df.format(aBedrag);

        return formatted;
    }
}
