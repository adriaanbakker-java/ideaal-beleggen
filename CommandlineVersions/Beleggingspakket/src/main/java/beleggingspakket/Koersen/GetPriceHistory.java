package beleggingspakket.Koersen;

import beleggingspakket.MainController;
import beleggingspakket.Constants;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.*;

/* 
 * Get the price history for the stocks in the AEX index
 * 
 * Example:
 * https://www.belegger.nl/Aandeel-Koers/210964/Royal-Dutch-Shell-A/historische-koersen.aspx?maand=5
 * 
 * Issues:
 * B001- The volumes are not correct in historic price files of belegger.nl, those values are too low.
 *   (based on the Euronext Amsterdam stock market alone, I would presume).
 *   
 * B002- To be solved: current price record of today is not included or attached yet. 
 *   After 17:50 the closing price is available.
 *   Even with current price in the daytime as closing price the extra update would be very valuable.
 *   The open,high,low and volume of today are to be found in a separate html file: example:
 *   
 *   https://www.belegger.nl/Aandeel-Koers/11783/Philips-Koninklijke/koers.aspx
 *   
 * B003- In the graph, the fridays are shown under the vertical grid bars, should be the mondays
 *   for example, 14-09-2019 is shown instead of 16-09-2019, the monday corresponding to the bar 
 *   (first trading day of the week)
 */
public class GetPriceHistory {

	private class MyDate {
		int day;
		int month;
		int year;
		public MyDate(int day, int month, int year) {
			super();
			this.day = day;
			this.month = month;
			this.year = year;
		}
		
	}

	public GetPriceHistory() {
		fillLinkMap();
	}

	// private String paginalink =
	// "https://www.belegger.nl/Aandeel-Koers/210964/Royal-Dutch-Shell-A/historische-koersen.aspx?maand=5";
	// "https://www.belegger.nl/Aandeel-Koers/11773/ING-Groep/historische-koersen.aspx?maand=0"
	// Example: ING-Groep
	// Ticker = INGA1
	// Stocknr = 11773
	// Stockname = ING-Groep
	
	// todays price (latest price record)
	// https://www.belegger.nl/Aandeel-Koers/11773/ING-Groep/koers.aspx

	private HashMap<String, BeleggerLinkRec> bMap = new HashMap<String, BeleggerLinkRec>();

	public List<String> printStockOverview() {
		List<String> stocks = new ArrayList<String>();
		for (String key: bMap.keySet()) {
			stocks.add(key);
		}
		return stocks;
	} 
	
	public Set<String> getTickers() {
		  return bMap.keySet();
	}

	private void fillLinkMap() {
		bMap.put("AEX", new BeleggerLinkRec("12272", "AEX"));
		bMap.put("AALBERTS", new BeleggerLinkRec("11797", "AALBERTS-NV"));
		bMap.put("ABN", new BeleggerLinkRec("612967", "ABN-AMRO-BANK-NV"));
		bMap.put("ADYEN", new BeleggerLinkRec("613007", "ADYEN-NV"));
		bMap.put("AEGON", new BeleggerLinkRec("11754", "Aegon"));
		bMap.put("DELHAIZE", new BeleggerLinkRec("11755", "Ahold-Delhaize-Koninklijke"));
		bMap.put("AKZO", new BeleggerLinkRec("11756", "Akzo-Nobel"));
		bMap.put("ARCELOR", new BeleggerLinkRec("11895", "ArcelorMittal"));
		bMap.put("ASML", new BeleggerLinkRec("16923", "ASML-Holding"));
		bMap.put("ASMI", new BeleggerLinkRec("11808", "ASM-International"));
		bMap.put("ASR", new BeleggerLinkRec("596718", "ASR-Nederland"));
		bMap.put("DSM", new BeleggerLinkRec("11764", "DSM-Koninklijke"));
		bMap.put("GALAPAGOS", new BeleggerLinkRec("60189120", "Galapagos"));
		bMap.put("HEINEKEN", new BeleggerLinkRec("11770", "Heineken"));
		bMap.put("IMCD", new BeleggerLinkRec("610603", "IMCD"));
		bMap.put("ING", new BeleggerLinkRec("11773", "ING-Groep"));
		bMap.put("KPN", new BeleggerLinkRec("25845", "KPN-Koninklijke"));
		bMap.put("NN", new BeleggerLinkRec("610720", "NN-Group"));
		bMap.put("PHILIPS", new BeleggerLinkRec("11783", "Philips-Koninklijke"));
		bMap.put("RANDSTAD", new BeleggerLinkRec("11785", "RANDSTAD-NV"));
		bMap.put("RELIX", new BeleggerLinkRec("11765", "RELIX"));
		bMap.put("SHELL", new BeleggerLinkRec("210964", "Royal-Dutch-Shell-A"));
		bMap.put("TAKEAWAY", new BeleggerLinkRec("561749", "Takeawaycom"));
		bMap.put("UNIBAIL", new BeleggerLinkRec("360115972", "UNIBAIL-RODAMCO-WESTFIELD"));
		bMap.put("UNILEVER", new BeleggerLinkRec("11962", "UNILEVER"));
		bMap.put("VOPAK", new BeleggerLinkRec("101431", "Vopak-Koninklijke"));
		bMap.put("WOLTERS", new BeleggerLinkRec("11795", "Wolters-Kluwer"));

	}

	public void testGetPricesMonth() throws Exception {
		// Retrieve prices  september 2019
		List<DayPriceRecord> prices = retrievePricesFromHistorypage("ADYEN", 2020, 4);

		// List<DayPriceRecord> prices = GetPriceHistory.getPrices();
		System.out.println("----------------------------------------");
		System.out.println("nr of prices:" + prices.size());
		System.out.println("----------------------------------------");
		if (prices.size() > 0) {

			for (DayPriceRecord price : prices) {
				System.out.println(price);
			}
		}
	}

	
	
	
	/*
	 * Retrieve the prices from the internet for a certain year-month until a certain year-month
	 */
	List<DayPriceRecord> getPricesFromHistoryPages  (
			int aYearFrom,
			int aMonthFrom,
			int aYearTo,
			int aMonthTo,
			String aTicker) throws Exception {
		
		try {
			List<DayPriceRecord> result = new ArrayList<DayPriceRecord>();
			//
			// extend from the starting month/year with prices up and including the current
			// month/year
			int iMonthFrom = 0;
			int iMonthTo = 0;
			for (int iYear=aYearFrom; iYear <= aYearTo; iYear++) {
				if (iYear == aYearFrom) {
					iMonthFrom = aMonthFrom;
				} else {
					iMonthFrom = 1;
				}
				if (iYear == aYearTo) {
					iMonthTo = aMonthTo;
				} else {
					iMonthTo = 12;
				}
				
				for (int month = iMonthFrom; month <= iMonthTo; month++) {
					    //System.out.println("getPricesMonth " + aTicker + Integer.toString(month) + "-" + Integer.toString(iYear) );
					if ((month == iMonthTo) && (iYear == 2020)) {
						System.out.println("last month for " + aTicker);
					}
						List<DayPriceRecord> prices = retrievePricesFromHistorypage(aTicker, iYear, month);
						//System.out.println("getPricesMonth done for " + aTicker + Integer.toString(month) + "-" + Integer.toString(iYear) );						
						for (DayPriceRecord p : prices) {							
							result.add(p);
						} // for prices 
					
				} // for month
			}	// for year	
			
			return result;
		} catch (Exception e) {
			throw new Exception("getPricesFromHistoryPages:" + e.getMessage());
		}
		
	} // extendToCurrentMonth
	
	
	/*
	 * Retrieve the current price history for this stock from the price file.
	 * If the file does not exist, an empty list is returned.
	 */
	public ArrayList<DayPriceRecord> getHistoricPricesFromFile(String aTicker) throws Exception {
		try {
			ArrayList<DayPriceRecord> result = new ArrayList<DayPriceRecord>();
			
			String filename = Constants.getPricefolder()  + aTicker + ".csv";
			// check if file exists, otherwise create
			File myFile = new File(filename);
			if (!myFile.exists()) {
				return result;
			}
			
			try (	BufferedReader reader = new BufferedReader(new FileReader(filename))) {
				String currentLine = "";
				DayPriceRecord dpr = null;

				// Copy until but excluding the current month/year
				while ((currentLine = reader.readLine()) != null) {
					dpr = DayPriceRecord.parseLine(currentLine);
					result.add(dpr);
				} // while				
				reader.close();
				return result;				
			}
			
		} catch (Exception e) {
			throw new Exception("getHistoricPricesFromFile:" + aTicker + " :" + e.getMessage());
		}
	} // getHistoricPricesFromFile
	

/*
 * 
 * Update the stock price history file starting from aYear and aMonth
 * Updates from the net. Current day's stock price may be retrieved from a separate page. 
 */
	public void updatePriceHistory(String aTicker, int aYear, int aMonth, MainController.LocalLogging aLogging)  {
		int yearFrom, monthFrom;
		int currentYear, currentMonth, currentDay;
		
		try {
			List<DayPriceRecord> pricesFromFile =  getHistoricPricesFromFile(aTicker);
			// find out what the last year/month is and try to retrieve the rest of the prices
			// until today from internet
			LocalDateTime today = LocalDateTime.now();
			currentMonth = today.getMonthValue();
			currentYear = today.getYear();
			currentDay = today.getDayOfMonth();

			if (aTicker.equals("SHELL")) {
				System.out.println("shell gevonden");
			}

			List<DayPriceRecord> prices = new ArrayList<>();
			for (DayPriceRecord p: pricesFromFile) {
				if (p.isBefore(currentMonth, currentYear)) {
					prices.add(p);
				}
			}

			if (prices.isEmpty()) {
				System.out.println("no prices used from file");
				yearFrom = Constants.startYear;
				monthFrom = 1;
			} else {  // find out what the last month is from the price file and extend
				DayPriceRecord lastRec = prices.get(prices.size()-1);
				
				System.out.println("price history from file runs until "
					       + lastRec.getDay() + " - " + lastRec.getMonth() + " - " + lastRec.getYear());
				
				
				yearFrom = lastRec.getYear();
				monthFrom = lastRec.getMonth();	
				monthFrom = monthFrom + 1;
				if (monthFrom > 12) {
					monthFrom = 1;
					yearFrom ++;
				}
			}
			
			System.out.println("retrieve prices from internet from " + monthFrom + " - " + yearFrom + 
					" until " + currentMonth + " - " + currentYear);
			List<DayPriceRecord> pricesFromHistoryPages = 
					getPricesFromHistoryPages(yearFrom, monthFrom, currentYear, currentMonth, aTicker);
			
			for (DayPriceRecord p: pricesFromHistoryPages) {              
					prices.add(p);					
			}
	
		    // In some cases today's date is not present. In that case, do an attempt to
			// retrieve this price from a different page.
			
			try {
				DayPriceRecord dpr = prices.get(prices.size()-1);
				if (dpr.isBefore(today)) {
					// Check if today's date is a trading day (monday to friday).
					// If so, check if the market has been open already
					
					if (checkMarketOpenedToday()) {
						// In that case, retrieve today's price info from a separate page
						// and add this price record to the list.
						DayPriceRecord dpr1 = retrievePricesOfToday(aTicker);
						// Check if the date equals today's date. In that case, append
						if ((dpr1.getYear() == currentYear) && (dpr.getMonth() == currentMonth) && (dpr.getDay() == currentDay) ) {
							System.out.println("ADDING TODAYS PRICE FROM SEPARATE PAGE");
							System.out.println("add price record of TODAY:" + dpr);
							prices.add(dpr);					
						}
					} 
				}
				
			} catch (Exception e) {
				aLogging.printMessage("ERROR retrieving today's price for " + aTicker + ":" + e.getMessage());
			}

			// now rewrite the prices in the price history file for this stock
			rewritePriceHistoryFile(aTicker, prices);
		} catch (Exception e) {
			aLogging.printMessage("updatePriceHistory:" + e.getMessage());
		}
	}

	public void testUpdatePrice() throws Exception {
		List<DayPriceRecord> prices =
				retrievePricesFromHistorypage("ADYEN", 2020, 4);
	}

	/*
	 * 
	 *   rewrite the price history file
	 */
	private void rewritePriceHistoryFile(String aTicker, List<DayPriceRecord> aPrices)
	throws Exception {
		
		String filename = Constants.getPricefolder() + aTicker + ".csv";
		
		try (	FileWriter writer = new FileWriter(filename); 
				BufferedWriter bw = new BufferedWriter(writer)) {
			for (DayPriceRecord p : aPrices) {
				String csvLine = p.toCSVLine();
				//System.out.println("writing:" + csvLine);
				writer.write(csvLine + "\n");
			} // for prices
			
			bw.close();
			writer.close();
		} 	
	}

	// retrieve today's price info of this stock
	public DayPriceRecord retrievePricesOfToday(String aTicker) throws Exception{

		DayPriceRecord dpr = getTodaysPrice(aTicker);
		return dpr;
	}

	// check today is a trading day and current time is after stock market opening hours
	private boolean checkMarketOpenedToday() {
		
		LocalDateTime today = LocalDateTime.now();
		int iDayOfWeek = today.getDayOfWeek().getValue();  // wednesday is a 3.
		System.out.println("Day of week:" + iDayOfWeek);
		
		if ((iDayOfWeek == 6) || (iDayOfWeek == 7)) {
			System.out.println("saturday or sunday - not a trading day");
			return false;
		}

	    if (today.getHour() < 9 ) {
	    	System.out.println("Not after nine, stock market not open yet");
	    	return false;
	    }
	    if ((today.getHour() == 9) && (today.getMinute() <10)) {
	    	System.out.println("Not after 09:10, stock market not open yet");
	    	return false;
	    }
	    System.out.println("Today after 09:10, stock market has been open");
		return true;
	}

	public void testReturnBeleggerLink() throws Exception {
		// BeleggerLinkRec brec = new BeleggerLinkRec("11773", "ING-Groep");

		String link = returnBeleggerLink("ING", 2018, 4);
		System.out.println(link);
	}

	
    public String returnBeleggerLinkDay(String aTicker) throws Exception {
    	BeleggerLinkRec refRec = bMap.get(aTicker);
		if (refRec == null)
			throw new Exception("Ticker not found:" + aTicker);
		return "https://www.belegger.nl/Aandeel-Koers/" + refRec.Stocknr + "/" + refRec.Stockname + "/koers.aspx";
    }
	
	public String returnBeleggerLink(String aTicker, int aYear, int aMonth) throws Exception {

		BeleggerLinkRec refRec = bMap.get(aTicker);
		if (refRec == null)
			throw new Exception("Ticker not found:" + aTicker);
		LocalDateTime today = LocalDateTime.now();
		int month = today.getMonthValue();
		int year = today.getYear();
		// System.out.println("returnBeleggerLink: year =" + year + " month =" + month);

		if (aYear < Constants.startYear)
			throw new Exception("returnBeleggerLink: year should be >= " + Constants.startYear+": year=" + aYear);
		if (aYear > year) // unfortunately, we can't look in the future
			throw new Exception("returnBeleggerLink:  year should be <= " + year + " year=" + aYear);
		if ((aYear == year) && (aMonth > month))
			throw new Exception("returnBeleggerLink:  month should be <= " + month + " month=" + aMonth);

		int months = month - aMonth;
		if (aYear < year)
			months += 12 * (year - aYear);
		return "https://www.belegger.nl/Aandeel-Koers/" + refRec.Stocknr + "/" + refRec.Stockname
				+ "/historische-koersen.aspx?maand=" + months;
	}

	public class BeleggerLinkRec {
		String Stocknr;
		String Stockname;

		public BeleggerLinkRec(String stocknr, String stockname) {
			super();
			Stocknr = stocknr;
			Stockname = stockname;
		}
	}

	private static int parseVolume(String sval) {
		int result = 0;
		try {
			sval = sval.replace(".", "");
			result = Integer.parseInt(sval);
		} catch (Exception e) {
			System.out.println("Parse error" + e.getMessage());
		}
		return result;
	}

	private static double parseDouble(String aDouble) {
		double result = 0.0;
		try {
			NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
			Number number = format.parse(aDouble);
			result = number.doubleValue();
		} catch (Exception e) {
			System.out.println("Parse error" + e.getMessage());
		}
		return result;
	}

	/*
	 * retrievePricesFromHistorypage
	 * 
	 * Retrieve prices from the internet for a certain AEX stock for a certain month
	 * 
	 * Uses links from stock price site https://www.belegger.nl to retrieve price
	 * history for a certain month
	 * 
	 * Example link:
	 * https://www.belegger.nl/Aandeel-Koers/210964/Royal-Dutch-Shell-A/historische-
	 * koersen.aspx?maand=0"
	 * 
	 * Structure of these web pages are of course specific to www.belegger.nl
	 * 
	 * Parameters: String aTicker - Stock ticket - this is the ticker used with
	 * Euronext, to be found in iex.nl int aMonth - Month number 1..current month
	 * (1..12) int aYear - Year number Constants.startYear..current year (for example, 2019)
	 * 
	 * Private table linknames[] is used to translate this to a link
	 * 
	 * 
	 */
	public List<DayPriceRecord> retrievePricesFromHistorypage(String aTicker, int aYear, int aMonth) throws Exception {
		System.out.println("getPricesMonth:" + aTicker + ":" + Integer.toString(aYear) + "-" + Integer.toString(aMonth));
		// 5= apr 4= mei 3=jun 2=jul 1=aug 0=sep
		if (aTicker.equals("VOPAK")) {
			System.out.println("VOPAK found");
		}


		String paginalink = returnBeleggerLink(aTicker, aYear, aMonth);
		// "https://www.belegger.nl/Aandeel-Koers/210964/Royal-Dutch-Shell-A/historische-koersen.aspx?maand=0";
		// System.out.println(paginalink);
		List<DayPriceRecord> result = new ArrayList<DayPriceRecord>();
		int rowcount = 0;
		Document doc = null;
		
		try {
			 
			 //Jsoup.connect(paginalink).userAgent("Opera").get();
			Connection connect = Jsoup.connect(paginalink)
                    .ignoreHttpErrors(true)
                    .timeout(20*1000)
                    // use this for chrome
                    .userAgent("Mozilla");

					//	System.out.println("Connection made BEFORE document.");
					doc = connect.post();
					//  System.out.println("Connection made AFTER document.");
		} catch (Exception e) {
			throw new Exception("Error connecting to " + paginalink + "\n" + e.getMessage());
		}
		  
		
		try {
			Elements elements = doc.getElementsByTag("table");
			int i = 0;
			for (Element e : elements) {
				i++;

				if (i == 1) {
					// System.out.println(e.attr("class"));
					Elements e_rows = e.getElementsByTag("tr");
					rowcount = 0;
					for (Element e_row : e_rows) {
						rowcount++;
						if (rowcount > 1) {
							Elements e_cells = e_row.getElementsByTag("td");

							DayPriceRecord dpr = process_row(aYear, aMonth, e_cells);
							if (dpr == null)
								throw new Exception("Null encountered");
							result.add(dpr);
						} // if rowcount (first row is header row)
					}
				} // if i==1 (only first table to be processed)

			} // for elements

		} catch (Exception e) {
			throw new Exception ("retrievePricesFromHistorypage: rowcount =" + rowcount + "Error: " + e.getMessage());
		}
		return result;
	}

	
	public DayPriceRecord getTodaysPrice(String aTicker) throws Exception {
		System.out.println("getTodaysPrice:" + aTicker );
		
		String paginalink = returnBeleggerLinkDay(aTicker);
		System.out.println("Link:" + paginalink);
		Document doc = null;
		
		try {
			 
			 //Jsoup.connect(paginalink).userAgent("Opera").get();
			Connection connect = Jsoup.connect(paginalink)
                    .ignoreHttpErrors(true)
                    .timeout(20*1000)
                    // use this for chrome
                    .userAgent("Mozilla");

					//	System.out.println("Connection made BEFORE document.");
					doc = connect.get();
					//  System.out.println("Connection made AFTER document.");
		} catch (Exception e) {
			throw new Exception("Error connecting to " + paginalink + "\n" + e.getMessage());
		}
		
		int day = 0;
		int month = 0;
		int year = 0;
		double open = 0.0;
		double high = 0.0;
		double low = 0.0;
		double close = 0.0;
		int volume = 0;
		
		MyDate date = null;
		
		System.out.println("doc retrieved");
		try {
			Elements elements = doc.getElementsByTag("table");
			if (elements.get(0).attr("class").equals("SimpleTable")) {
				// first row from table
				Elements e_rows = elements.get(0).getElementsByTag("tr");
				Elements e_cells = e_rows.get(0).getElementsByTag("td");
				// second cell of first row contains date in format 04-okt-19
				System.out.println("Found date:" +  e_cells.get(1).text());
				
				date = convertToDate(e_cells.get(1).text());
				
				// second row second cell contains current price
				e_cells = e_rows.get(1).getElementsByTag("td");
				System.out.println("Found current price (will be close price end of day):" +  e_cells.get(1).text());
				close = parseDouble(e_cells.get(1).text());
				
				// fourth row second cell contains high of the day
				e_cells = e_rows.get(3).getElementsByTag("td");
				System.out.println("Found high:" +  e_cells.get(1).text());
				high = parseDouble(e_cells.get(1).text());

				// fifth row second cell contains low of the day
				e_cells = e_rows.get(4).getElementsByTag("td");
				System.out.println("Found low:" +  e_cells.get(1).text());
				low = parseDouble(e_cells.get(1).text());
				
				// sixth row second cell contains low of the day
				e_cells = e_rows.get(5).getElementsByTag("td");
				System.out.println("Found volume:" +  e_cells.get(1).text());
				volume = parseVolume( e_cells.get(1).text());
				
			} else {
				throw new Exception("SimpleTable as first table expected on web page");
			}
			if (elements.get(1).attr("class").equals("SimpleTable")) {
				
				Elements e_rows = elements.get(1).getElementsByTag("tr");
				Elements e_cells = e_rows.get(3).getElementsByTag("td");
				// second cell of fourth row contains opening price 
				System.out.println("Found opening price:" +  e_cells.get(1).text());
				open = parseDouble(e_cells.get(1).text());
			} else {
				throw new Exception("SimpleTable as second table expected on web page");
			}
		} catch (Exception e) {
			throw new Exception("getTodaysPrice " + paginalink + "\n" + e.getMessage());
		}
		DayPriceRecord result = new DayPriceRecord(date.day, date.month, date.year, open, high, low, close, volume);
		return result;
	}
	
	
	// format aDate of 04-okt-19
	// convert to MyDate
	private MyDate convertToDate(String aString) throws Exception {
		MyDate result = null;
		try {
			
			String[] parts = aString.split(" ");
			if (parts.length < 3)
				throw new Exception("invalid date");

			System.out.println("parts:");
			for (String part: parts) {
				System.out.println("[" + part + "]");
			}
			int year = Integer.parseInt(parts[2]);
			System.out.println("year found:" + year);
			int month = 0;
			if (parts[1].equals("jan")) month =1; 
			else if (parts[1].equals("feb")) month =2;
			else if (parts[1].equals("maa")) month =3;
			else if (parts[1].equals("apr")) month =4;
			else if (parts[1].equals("mei")) month =5;
			else if (parts[1].equals("jun")) month =6;
			else if (parts[1].equals("jul")) month =7;
			else if (parts[1].equals("aug")) month =8;
			else if (parts[1].equals("sep")) month =9;
			else if (parts[1].equals("okt")) month =10;
			else if (parts[1].equals("nov")) month =11;
			else if (parts[1].equals("dec")) month =12;
			else throw new Exception("month not found");
			
			
			int day = Integer.parseInt(parts[0]);
			result = new MyDate(day, month, year);
			return result;
		} catch (Exception e) {
			throw new Exception("Date parse error:" + aString);
		}
		
		
	}

	private static DayPriceRecord process_row(int aYear, int aMonth, Elements e_cells) {
		int ccount = 1;
		DayPriceRecord dpr = null;

		int day = 0;
		double open = 0.0;
		double high = 0.0;
		double low = 0.0;
		double close = 0.0;
		for (Element e_cell : e_cells) {

			// double fval = Double.parseDouble(e_cell.text());
			String sval = e_cell.text();
			String sout;

			int volume = 0;

			switch (ccount) {
			case 1: {
				day = getIntval(sval);
				break;
			}
			case 2:
				open = parseDouble(sval);
				// System.out.print("Open: " + printPrice(dval));
				break;
			case 3:
				close = parseDouble(sval);
				break;
			case 4:
				low = parseDouble(sval);
				break;
			case 6:
				high = parseDouble(sval);
				break;
			case 7:
				volume = parseVolume(sval);
				dpr = new DayPriceRecord(day, aMonth, aYear, open, high, low, close, volume);
				// System.out.println("Gelezen:" + dpr);
				break;
			default:
				break;
			} // switch

			ccount++;
		} // for element
		return dpr;
	}

	// Retrieve the day nr from the string, for example 08 sep
	private static int getIntval(String sval) {
		int i = 0;
		String sval2 = "";
		while (i < sval.length()) {
			char c = sval.charAt(i);
			if ((c >= '0') && (c <= '9'))
				sval2 += c;
			i++;
		}
		return Integer.valueOf(sval2);
	}



}
