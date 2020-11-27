package beleggingspakket.Koersen;

import beleggingspakket.Constants;
import beleggingspakket.MainController;
/*import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;*/

import java.io.*;
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

	Constants constants = new Constants();

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
// Enkele midkappers
        //https://www.iex.nl/Aandeel-Koers/11816/BAM-Groep-Koninklijke/historische-koersen.aspx?maand=202010
		bMap.put("BAM", new BeleggerLinkRec("11816", "BAM-Groep-Koninklijke"));
		// https://www.iex.nl/Aandeel-Koers/360114884/Air-France-KLM/historische-koersen.aspx?maand=202010
		bMap.put("AIRFRANCEKLM", new BeleggerLinkRec("360114884", "Air-France-KLM"));


	}

	
	/*
	 * Retrieve the current price history for this stock from the price file.
	 * If the file does not exist, an empty list is returned.
	 */
	public ArrayList<DayPriceRecord> getHistoricPricesFromFile(String aTicker) throws Exception {
		try {
			ArrayList<DayPriceRecord> result = new ArrayList<DayPriceRecord>();

			String filename = constants.getPricefolder()  + aTicker + ".csv";
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

	public String returnBeleggerLink(String aTicker, int aYear, int aMonth) throws Exception {

		BeleggerLinkRec refRec = bMap.get(aTicker);
		if (refRec == null)
			throw new Exception("Ticker not found:" + aTicker);
		LocalDateTime today = LocalDateTime.now();
		int month = today.getMonthValue();
		int year = today.getYear();
		// System.out.println("returnBeleggerLink: year =" + year + " month =" + month);

		if (aYear < constants.startYear)
			throw new Exception("returnBeleggerLink: year should be >= " + constants.startYear+": year=" + aYear);
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



}
