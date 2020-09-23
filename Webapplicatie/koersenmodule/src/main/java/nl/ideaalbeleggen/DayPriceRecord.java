package nl.ideaalbeleggen;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DayPriceRecord {
	private final int day, month, year;
	private final double Open, High, Low, Close;
	private final int Volume;

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	public double getOpen() {
		return Open;
	}

	public double getHigh() {
		return High;
	}

	public double getLow() {
		return Low;
	}

	public double getClose() {
		return Close;
	}

	public int getVolume() {
		return Volume;
	}

	public DayPriceRecord(int day, int month, int year, double open, double high, double low, double close,
                          int volume) {
		super();
		this.day = day;
		this.month = month;
		this.year = year;
		Open = open;
		High = high;
		Low = low;
		Close = close;
		Volume = volume;
	}

	/*
	 * used for the format in the price file 
	 */
	private static String printDate(int day2, int month2, int year2) {
		String result = String.format("%d", year2) + "-" + String.format("%02d", month2) + "-"
				+ String.format("%02d", day2);
		return result;
	}

	/*
	 * used for human readable output of the date 
	 */
	public String printDate() {
		String result = String.format("%d", day) + "-" + String.format("%02d", month) + "-"
				+ String.format("%02d", year);
		return result;
	}


	public String printClose() {
		return String.format("%.2f", getClose());
	}

	private static String printPrice(double aPrice) {

		return String.format("%.2f", aPrice);

//		// problem with locale: could not find Locale.NL in java library
//		// fixed this by creating an explicit formatter
//		DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.getDefault());
//		formatSymbols.setDecimalSeparator('.');
//
//		NumberFormat formatter = new DecimalFormat("#0.00", formatSymbols);
//		formatter.setGroupingUsed(false);
//
//		return formatter.format(aPrice);
	}

	@Override
	public String toString() {
		return "Datum:" + printDate(day, month, year) + " Open:" + printPrice(Open) + " Hoog:" + printPrice(High)
				+ " Laag:" + printPrice(Low) + " Slot: " + printPrice(Close) + " Vol:" + printVolume(Volume);
	}

	public String toCSVLine() {
		return printDate(day, month, year) + ";" + printPrice(Open) + ";" + printPrice(High) + ";" + printPrice(Low)
				+ ";" + printPrice(Close) + ";" + printVolume(Volume);
	}

	private static String printVolume(int volume2) {
		// problem with locale: could not find Locale.NL in java library
		// fixed this by replacement of "," by "."
		String result = NumberFormat.getNumberInstance(Locale.US).format(volume2);
		return result.replace(",", ".");
	}

	public static DayPriceRecord parseLine(String currentLine) throws Exception {
		int iYear = 0;
		int iMonth = 0;
		int iDay = 0;
		DayPriceRecord dpr = null;;
		try {
			String parts[] = currentLine.split(";");
			if (parts.length != 6) {
				throw new Exception("6 elements expected, found:" + parts.length);
			}
			Pattern pat = Pattern.compile("\\d+");
			Matcher m = pat.matcher(parts[0]);

			if (!m.find()) {
				throw new Exception("Error in date (year)");
			}
			iYear = Integer.parseInt(m.group());
			if (!m.find()) {
				throw new Exception("Error in date (month)");
			}
			iMonth = Integer.parseInt(m.group());

			if (!m.find()) {
				throw new Exception("Error in date (day)");
			}
			iDay = Integer.parseInt(m.group());

			double open, high, low, close;
			int volume;
			for (int i = 1; i<= 4; i++) {
				parts[i]=parts[i].replace(',','.');
			}

			open = Float.parseFloat(parts[1]);
			high = Float.parseFloat(parts[2]);
			low = Float.parseFloat(parts[3]);
			close = Float.parseFloat(parts[4]);
			String sVolume = parts[5];

			sVolume = sVolume.replace(".", "");
			volume = Integer.parseInt(sVolume);

			dpr = new DayPriceRecord(iDay, iMonth, iYear, open, high, low, close, volume);
			//System.out.println("Read from file:" + dpr);
		} catch (Exception e) {
			System.out.println("Error parseLine:" + e.getMessage() + "[" + currentLine + "]");
		}
		return dpr;
	}

	public boolean isBefore(int monthTo, int yearTo) {
		if ( this.year > yearTo ) return false;
		if ((this.year == yearTo ) && (this.month >= monthTo)) return false;
		return true;
	}

	public boolean isBefore(LocalDateTime today) {
		int day = today.getDayOfMonth();
		int month = today.getMonthValue();
		int year = today.getYear();
		if ( this.year > year ) return false;
		if ((this.year == year ) && (this.month > month)) return false;
		if ((this.year == year ) && (this.month == month) && (this.day > day)) return false;
		
		return true;
	}

	public String printLow() {
		return String.format("%.2f", getLow());
	}
}