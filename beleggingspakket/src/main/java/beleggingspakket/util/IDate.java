package beleggingspakket.util;

import java.time.LocalDateTime;

public class IDate {
    private int year;
    private int month;
    private int day;

    public IDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public boolean isSmaller(IDate aIDate) {
        if (year < aIDate.getYear() ) return true;
        if (year > aIDate.getYear() ) return false;
        if (month < aIDate.getMonth()) return true;
        if (month > aIDate.getMonth()) return false;
        if (day < aIDate.getDay()) return true;
        return false;
    }

    public boolean isEqual(IDate aIDate) {
        return ((aIDate.getYear() == year) &&
                (aIDate.getMonth() == month) &&
                (aIDate.getDay() == day));
    }

    public boolean isSmallerEqual(IDate aIDate) {
        if (isSmaller(aIDate)) return true;
        if (isEqual(aIDate)) return true;
        return false;
    }


    public IDate addNrDays(int n) throws Exception{
        try {
            LocalDateTime ldt = Util.toLocalDateTime(this);
            LocalDateTime ldtadd = ldt.plusDays(n);
            return Util.toIDate(ldtadd);
        } catch (Exception e) {
            throw new Exception("IDate.addNrDays():" + e.getLocalizedMessage());
        }
    }

    public IDate(LocalDateTime orderDate) {
        year = orderDate.getYear();
        month = orderDate.getMonthValue();
        day = orderDate.getDayOfMonth();
    }

    public String toString() {
        String sDay = "";
        String sMonth = "";
        if (day < 10) sDay = "0" + day;
        else sDay = "" + day;
        if (month < 10) sMonth = "0" + month;
        else sMonth = "" + month;
        
        return sDay + "-" + sMonth + "-" + year;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
