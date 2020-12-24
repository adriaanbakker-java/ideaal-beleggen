package nl.ideaalbeleggen;

// JSoup vervalt want draait geen javascript, daardoor wijken de prijzen af
// bij IEX en dit maakt hetge
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;

// 1 aug 2020 IEX heeft zijn website weer herzien. Ik krijg met HTMLUnit driver geen goede parsing
// elementen blijven leeg in de tabel. Ik gebruik nu dus wel weer JSoup om te parsen
// zodra het document is opgehaald met de HTMLUnit driver

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
//import org.openqa.selenium.phantomjs.PhantomJSDriver;
//import org.openqa.selenium.phantomjs.PhantomJSDriver;


public class WebAccess {
    private WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);

    public WebAccess(HashMap<String, GetPriceHistory.BeleggerLinkRec> bMap) {
        this.bMap = bMap;
    }

    public void closeDriver() {
        driver.close();
    }

    private HashMap<String, GetPriceHistory.BeleggerLinkRec> bMap;

    private static Integer parseVolume(String sval) {
        Integer result = 0;
        try {
            sval = sval.trim();
            sval = sval.replace(".", "");
            result = Integer.parseInt(sval);
        } catch (Exception e) {
            System.out.println("Parse error" + e.getMessage());
        }
        return result;
    }

    private static double parseDouble(String aDouble) {
        double result = 0.0;
        // get rid of possible dots
        aDouble = aDouble.replace(".", "");
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
            Number number = format.parse(aDouble);
            result = number.doubleValue();
        } catch (Exception e) {
            System.out.println("Parse error" + e.getMessage());
        }
        return result;
    }

    static String sUrl = "https://www.iex.nl/Aandeel-Koers/613007/ADYEN-NV/historische-koersen.aspx?maand=1";
    static String expectedTitle = "ADYEN NV » Historische koersen (Aandeel) | IEX.nl";
    //static String sUrl1 = "http://demo.guru99.com/test/newtours/";
    //static String expectedTitle1 = "Welcome: Mercury Tours";

    public void demoHeadlessDriverPrices() {
        System.out.println("Demo htmlUnitDriver to retrieve prices");
        WebDriver driver = new HtmlUnitDriver();
        // Navigate to Google
        String baseURL = sUrl;
        driver.get(baseURL);

        String actualTitle = driver.getTitle();

        List<WebElement> tables = driver.findElements(By.className("ContentTable"));
        List<WebElement> tbody = tables.get(1).findElements(By.tagName("tbody"));
        List<WebElement> rows = tbody.get(0).findElements(By.tagName("tr"));
        List<WebElement> cells = rows.get(0).findElements(By.tagName("td"));

        String sDay = cells.get(0).getText();
//        int iDay = getIntval(sDay);
        String sOpen = cells.get(1).getText();
//        Double dOpen = parseDouble(sOpen);
        String sClose = cells.get(2).getText();
//        Double dClose = parseDouble(sClose);
        String sLow = cells.get(3).getText();
//        Double dLow = parseDouble(sLow);
        String sHigh = cells.get(5).getText();
//        Double dHigh = parseDouble(sHigh);
        String sVolume = cells.get(6).getText();
//        int iVolume = parseVolume(sVolume);

//        DayPriceRecord dpr = new DayPriceRecord(iDay, )

        System.out.println("open:" + sOpen + " hoog:" + sHigh + " laag: " + sLow + " slot:" + sClose +
                " volume: " + sVolume);

        /*
         * compare the actual title of the page with the expected one and print
         * the result as "Passed" or "Failed"
         */
        if (actualTitle.contentEquals(expectedTitle)) {
            System.out.println("Test Passed, expected title found");
        } else {
            System.out.println("Test Failed, title not equal");
        }

        //close Fire fox
        driver.close();
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
    public List<DayPriceRecord> retrievePricesFromHistorypage(
            String aTicker, int aYear, int aMonth) throws Exception {
        System.out.println("getPricesMonth:" + aTicker + ":" + Integer.toString(aYear) + "-" + Integer.toString(aMonth));


        List<DayPriceRecord> result = new ArrayList<DayPriceRecord>();
        String paginalink = returnBeleggerLink(aTicker, aYear, aMonth);

        driver.get(paginalink);

        String actualTitle = driver.getTitle();

        String docString = driver.getPageSource();
        Document doc = Jsoup.parse(docString);
        int rowcount = 0;

        try {
            Elements elements = doc.getElementsByTag("table");
            Element table = elements.get(1); // there are three tables, we need the datatable
            Elements table_elements = table.children();
            Elements rows = table_elements.get(1).getElementsByTag("tr");

            for (Element row : rows) {
                Elements e_cells = row.getElementsByTag("td");
                DayPriceRecord dpr = process_row(aYear, aMonth, e_cells);
                if (dpr == null)
                    throw new Exception("Null encountered");
                result.add(dpr);
            } // for elements

        } catch (Exception e) {
            throw new Exception("retrievePricesFromHistorypage: rowcount =" + rowcount + "Error: " + e.getMessage());
        }
        return result;
    }


    private static DayPriceRecord process_row(int aYear, int aMonth, Elements e_cells)
            throws Exception {
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
                    day = retrieveDayNr(sval);
                    break;
                }
                case 2:
                    open = parseDouble(sval);
                    // System.out.print("Open: " + printPrice(dval));
                    break;
                case 3:
                    low = parseDouble(sval);
                    break;
                case 4:
                    high = parseDouble(sval);
                    break;
                case 5:
                    close = parseDouble(sval);
                    break;
                case 6:
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

    // retrieve day nr for example the 4 from "maandag 4 mei 2020"
    private static int retrieveDayNr(String sval) throws Exception {
        String arr[] = sval.split(" ");
        if (arr.length < 4)
            throw new Exception("retrieveDayNr: four elements expected:[" + sval + "]");
        return Integer.parseInt(arr[1]);
    }

    // format aDate of 04-okt-19
    // convert to MyDate
    public MyDate convertToDate(String aString) throws Exception {
        MyDate result = null;
        try {
            String[] parts = aString.split(" ");
            if (parts.length < 3)
                throw new Exception("invalid date");

            System.out.println("parts:");
            for (String part : parts) {
                System.out.println("[" + part + "]");
            }
            int year = Integer.parseInt(parts[2]);
            System.out.println("year found:" + year);
            int month = 0;
            if (parts[1].equals("jan")) month = 1;
            else if (parts[1].equals("feb")) month = 2;
            else if (parts[1].equals("maa")) month = 3;
            else if (parts[1].equals("apr")) month = 4;
            else if (parts[1].equals("mei")) month = 5;
            else if (parts[1].equals("jun")) month = 6;
            else if (parts[1].equals("jul")) month = 7;
            else if (parts[1].equals("aug")) month = 8;
            else if (parts[1].equals("sep")) month = 9;
            else if (parts[1].equals("okt")) month = 10;
            else if (parts[1].equals("nov")) month = 11;
            else if (parts[1].equals("dec")) month = 12;
            else throw new Exception("month not found");

            int day = Integer.parseInt(parts[0]);
            result = new MyDate(day, month, year);
            return result;
        } catch (Exception e) {
            throw new Exception("Date parse error:" + aString);
        }
    }


    public String returnBeleggerLinkDay(String aTicker) throws Exception {
        GetPriceHistory.BeleggerLinkRec refRec = bMap.get(aTicker);
        if (refRec == null)
            throw new Exception("Ticker not found:" + aTicker);
        return "https://www.iex.nl/Aandeel-Koers/" + refRec.Stocknr + "/" + refRec.Stockname + ".aspx";
    }


    public String returnBeleggerLink(String aTicker, int aYear, int aMonth) throws Exception {

        GetPriceHistory.BeleggerLinkRec refRec = bMap.get(aTicker);
        if (refRec == null)
            throw new Exception("Ticker not found:" + aTicker);
        LocalDateTime today = LocalDateTime.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        // System.out.println("returnBeleggerLink: year =" + year + " month =" + month);

        if (aYear < Constants.startYear)
            throw new Exception("returnBeleggerLink: year should be >= " + Constants.startYear + ": year=" + aYear);
        if (aYear > year) // unfortunately, we can't look in the future
            throw new Exception("returnBeleggerLink:  year should be <= " + year + " year=" + aYear);
        if ((aYear == year) && (aMonth > month))
            throw new Exception("returnBeleggerLink:  month should be <= " + month + " month=" + aMonth);

        String monthString = Integer.toString(aMonth);
        if (aMonth < 10) {
            monthString = "0" + monthString;
        }
        monthString = Integer.toString(aYear) + monthString;

        return "https://www.iex.nl/Aandeel-Koers/" + refRec.Stocknr + "/" + refRec.Stockname
                + "/historische-koersen.aspx?maand=" + monthString;
    }


    public DayPriceRecord getTodaysPrice(Document doc, String aTicker) throws Exception {
        System.out.println("getTodaysPrice:" + aTicker);

        String paginalink = returnBeleggerLinkDay(aTicker);
        System.out.println("Link:" + paginalink);

        int day = 0;
        int month = 0;
        int year = 0;
        double open = 0.0;
        double high = 0.0;
        double low = 0.0;
        double close = 0.0;
        Integer volume = 0;

        MyDate date = null;


        /*  panel (0) -> keylist--row (1) -> keylist (2) -> keylist__pair (3) -> (keylist__term, keylist__value) (4)
        * */
        try {
            Elements elements = doc.getElementsByClass("panel");
            Elements panel = elements.get(1).children();  // let op het is het tweede panel.
            Elements keylist_row = panel.get(0).children();  // (1)
            Elements keylist = keylist_row.get(0).children(); // (2)
            Elements keylist_pair = keylist.get(0).children(); // (3)
            Elements keylist_termvalue = keylist_pair.get(1).children(); // (4)
            String sClose = keylist_termvalue.text();                    // laatste koers
            close = parseDouble(sClose);


            keylist_pair = keylist.get(2).children();                    // dagvolume
            keylist_termvalue = keylist_pair.get(1).children();
            String sVolume = keylist_termvalue.text();
            volume = parseVolume(sVolume);

            keylist = keylist_row.get(1).children(); // (2)
            keylist_pair = keylist.get(2).children(); // (3)
            keylist_termvalue = keylist_pair.get(1).children(); // (4)
            String sLow = keylist_termvalue.text();                    // laag
            low = parseDouble(sLow);

            //keylist = keylist_row.get(1).children(); // (2)
            keylist_pair = keylist.get(3).children(); // (3)
            keylist_termvalue = keylist_pair.get(1).children(); // (4)
            String sHigh = keylist_termvalue.text();                    // hoog
            high = parseDouble(sHigh);

            //keylist = keylist_row.get(1).children(); // (2)
            keylist_pair = keylist.get(4).children(); // (3)
            keylist_termvalue = keylist_pair.get(1).children(); // (4)
            String sOpen = keylist_termvalue.text();                    // openingskoers
            open = parseDouble(sOpen);

            // Datum is nog een dingetje: de pagina blijft in het weekeinde staan en tot de volgende dag.
            // Aan de hand van de datum van "vandaag" zou wel een datum zijn te achterhalen maar dat is lastig
            // Gelukkig staat achter de "slotkoers vorige dag" een datum, (bijv. "(23 dec)"
            // we kunnen de volgende handelsdag berekenen aan de hand van deze datum.

            keylist_pair = keylist.get(5).children(); // (3)
            keylist_termvalue = keylist_pair.get(1).children(); // (4)
            String sSlotVorigeDag = keylist_termvalue.text();                    // slot vorige dag plus datum
            System.out.println(sSlotVorigeDag);
/*            if (elements.get(0).attr("class").equals("SimpleTable")) {
                // first row from table
                Elements e_rows = elements.get(0).getElementsByTag("tr");
                Elements e_cells = e_rows.get(0).getElementsByTag("td");
                // second cell of first row contains date in format 04-okt-19
                System.out.println("Found date:" + e_cells.get(1).text());

                date = convertToDate(e_cells.get(1).text());

                // second row second cell contains current price
                e_cells = e_rows.get(1).getElementsByTag("td");
                System.out.println("Found current price (will be close price end of day):" + e_cells.get(1).text());
                close = parseDouble(e_cells.get(1).text());

                // fourth row second cell contains high of the day
                e_cells = e_rows.get(3).getElementsByTag("td");
                System.out.println("Found high:" + e_cells.get(1).text());
                high = parseDouble(e_cells.get(1).text());

                // fifth row second cell contains low of the day
                e_cells = e_rows.get(4).getElementsByTag("td");
                System.out.println("Found low:" + e_cells.get(1).text());
                low = parseDouble(e_cells.get(1).text());

                // sixth row second cell contains low of the day
                e_cells = e_rows.get(5).getElementsByTag("td");
                System.out.println("Found volume:" + e_cells.get(1).text());
                volume = parseVolume(e_cells.get(1).text());

            } else {
                throw new Exception("SimpleTable as first table expected on web page");
            }
            if (elements.get(1).attr("class").equals("SimpleTable")) {

                Elements e_rows = elements.get(1).getElementsByTag("tr");
                Elements e_cells = e_rows.get(3).getElementsByTag("td");
                // second cell of fourth row contains opening price
                System.out.println("Found opening price:" + e_cells.get(1).text());
                open = parseDouble(e_cells.get(1).text());
            } else {
                throw new Exception("SimpleTable as second table expected on web page");
            }*/
        } catch (Exception e) {
            throw new Exception("getTodaysPrice " + paginalink + "\n" + e.getMessage());
        }
        DayPriceRecord result = new DayPriceRecord(date.day, date.month, date.year, open, high, low, close, volume);
        return result;
    }

    /* retrieve intraday prices for this stock from the internet
     */
    public DayPriceRecord getIntraDayPrices(String aTicker) throws Exception {
        System.out.println("Webaccess: retrieve intraday prices for " + aTicker);
        String paginalink = returnBeleggerLinkDay(aTicker);
        System.out.println(paginalink);

        driver.get(paginalink);

        String actualTitle = driver.getTitle();

        String docString = driver.getPageSource();
        Document doc = Jsoup.parse(docString);

        return getTodaysPrice(doc, aTicker);
    }
}