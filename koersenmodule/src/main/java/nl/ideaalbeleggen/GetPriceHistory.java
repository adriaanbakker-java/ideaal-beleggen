package nl.ideaalbeleggen;


import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

    private WebAccess webAccess;
    public GetPriceHistory() {
        fillLinkMap();
        webAccess = new WebAccess(bMap);
    }

    public void closeDriver() {
    	webAccess.closeDriver();
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
		bMap.put("ASMI", new BeleggerLinkRec("11808", "ASM-International"));
		bMap.put("ASML", new BeleggerLinkRec("16923", "ASML-Holding"));
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

	public void testGetPricesMonth()  {

		//webAccess.demoHeadlessDriverPrices();

		try {
			// Retrieve prices  september 2019
			List<DayPriceRecord> prices = webAccess.retrievePricesFromHistorypage("ADYEN", 2020, 4);

			// List<DayPriceRecord> prices = GetPriceHistory.getPrices();
			System.out.println("----------------------------------------");
			System.out.println("nr of prices:" + prices.size());
			System.out.println("----------------------------------------");
			if (prices.size() > 0) {

				for (DayPriceRecord price : prices) {
					System.out.println(price);
				}
			}
		} catch(Exception e) {
			System.out.println("testGetPricesMonth:" + e.getLocalizedMessage());
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
						List<DayPriceRecord> prices =
                                webAccess.retrievePricesFromHistorypage(aTicker, iYear, month);
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
	public void updatePriceHistory(
            String aTicker,
            int startYear, int month,
            int aEndYear,
            int aEndMonth,
            MainController.LocalLogging aLogging)  {
		int yearFrom, monthFrom;
		int currentYear, currentMonth;
		
		try {
			List<DayPriceRecord> pricesFromFile =  getHistoricPricesFromFile(aTicker);
			// in case no end year and and end month given (values -1)
            // find out what the last year/month is and try to retrieve the rest of the prices
			// until today from internet
            // otherwise set end year and end month

            if (aEndYear == -1) {
                LocalDateTime today = LocalDateTime.now();
                currentMonth = today.getMonthValue();
                currentYear = today.getYear();
            } else {
                currentMonth = aEndMonth;
                currentYear = aEndYear;
            }


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
			} else {
			    // find out what the last month is from the price file and extend
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

/*			NOG TE IMPLEMENTEREN met HtmlUnitDriver

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
*/
			// now rewrite the prices in the price history file for this stock
			rewritePriceHistoryFile(aTicker, prices);
		} catch (Exception e) {
			aLogging.printMessage("updatePriceHistory:" + e.getMessage());
		}
	}

	public void testUpdatePrice() throws Exception {
		webAccess.demoHeadlessDriverPrices();
		//List<DayPriceRecord> prices =
		//		webAccess.retrievePricesFromHistorypage("ADYEN", 2020, 4);
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
	/*public DayPriceRecord retrievePricesOfToday(String aTicker) throws Exception{

		DayPriceRecord dpr = webAccess.getTodaysPrice(aTicker);
		return dpr;
	} */

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

		String link = webAccess.returnBeleggerLink("ING", 2018, 4);
		System.out.println(link);
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

}
