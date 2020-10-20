package beleggingspakket.util;

import beleggingspakket.portefeuillebeheer.Ordertype;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Util {

    public static String toCurrency(double aBedrag) {

        NumberFormat df = NumberFormat.getInstance(Locale.US);
        // US has a dot for a decimal separator. That's what we need in a comma separated file.
        // In addition, remove all comma's and spaces

        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        String formatted = df.format(aBedrag);
        String formatted1 = formatted.replaceAll("\\s","");
        String formatted2 = formatted1.replaceAll("\\,","");

        return formatted2;
    }

    public static String toYYYYMMDDHHMM(LocalDateTime orderDate) {
        String europeanDatePattern = "dd.MM.yyyy";
        DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern(europeanDatePattern);
        String result = europeanDateFormatter.format(orderDate);
        return result;
    }

    public static Ordertype toOrderType(String orderTypeString) {
        Ordertype result = null;
        for (Ordertype o: Ordertype.values()) {
            if (o.name().equals(orderTypeString))
                result = o;
        }
        return result;
    }

    public static double toDouble(String orderelement) throws Exception {
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        Number number = format.parse(orderelement);
        return number.doubleValue();
    }

    public static LocalDateTime toLocalDateTime(String sYYYYMMDD) {
        //default, ISO_LOCAL_DATE
        LocalDateTime localDate = LocalDateTime.parse(sYYYYMMDD);
        return localDate;
    }
}
