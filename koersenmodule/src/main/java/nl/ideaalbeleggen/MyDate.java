package nl.ideaalbeleggen;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.now;

public class MyDate {
    int day;
    int month;
    int year;

    public MyDate(int day, int month, int year) {
        super();
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public boolean equals(MyDate aDate) {
        if ((day == aDate.day) &&
                (month == aDate.month) &&
                (year == aDate.year))
            return true;
        return false;
    }

    public static MyDate geefHuidigeDatum() {
        LocalDateTime now = now();
        MyDate result =
                new MyDate(now.getDayOfMonth(),
                        now.getMonthValue(),
                        now.getYear());
        return result;
    }

    public LocalDate toLocalDate() {
        LocalDate ld = LocalDate.of(year, month, day);
        return ld;
    }

    public MyDate(LocalDate ld) {
        super();
        this.year = ld.getYear();
        this.month = ld.getMonthValue();
        this.day = ld.getDayOfMonth();
    }

    public int geefDagnrInWeek() { //1..7, maandag is 1
        LocalDate ld = toLocalDate();
        DayOfWeek dayOfWeek = ld.getDayOfWeek();
        int dayOfWeekIntValue = dayOfWeek.getValue();
        return dayOfWeekIntValue;
    }


    public MyDate geefVolgendeHandelsdag() {
        int dagnr = geefDagnrInWeek();
        if (dagnr <= 4) {
            LocalDate ld = this.toLocalDate().plusDays(1);
            return new MyDate(ld);
        } else {
            // dagnr=5 --> +3
            // 6 --> +2
            // 7 --> +1
            LocalDate ld = this.toLocalDate().plusDays(dagnr - 5 + 3);
            return new MyDate(ld);
        }
    }
}
