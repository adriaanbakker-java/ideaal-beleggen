package nl.beleggingspakket.util;

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
