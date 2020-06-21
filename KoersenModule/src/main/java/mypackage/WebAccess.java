package mypackage;

// JSoup vervalt want draait geen javascript, daardoor wijken de prijzen af
// bij IEX en dit maakt hetge
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
//import org.openqa.selenium.phantomjs.PhantomJSDriver;
//import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebAccess {
    private WebDriver driver = new HtmlUnitDriver();

    public WebAccess(HashMap<String, GetPriceHistory.BeleggerLinkRec> bMap) {
        this.bMap = bMap;
    }

    public void closeDriver() {
        driver.close();
    }

    private HashMap<String, GetPriceHistory.BeleggerLinkRec> bMap;

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
        if (actualTitle.contentEquals(expectedTitle)){
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
        // 5= apr 4= mei 3=jun 2=jul 1=aug 0=sep

        List<DayPriceRecord> result = new ArrayList<DayPriceRecord>();
        String paginalink = returnBeleggerLink(aTicker, aYear, aMonth);

        driver.get(paginalink);

        String actualTitle = driver.getTitle();

        System.out.println(actualTitle);
        List<WebElement> tables = driver.findElements(By.className("ContentTable"));
        List<WebElement> tbody = tables.get(1).findElements(By.tagName("tbody"));
        List<WebElement> rows = tbody.get(0).findElements(By.tagName("tr"));

        for (int row = 0; row <= rows.size()-1; row++ ) {
            List<WebElement> cells = rows.get(row).findElements(By.tagName("td"));

            String sDay = cells.get(0).getText();
            int iDay = getIntval(sDay);
            String sOpen = cells.get(1).getText();
            Double dOpen = parseDouble(sOpen);
            String sClose = cells.get(2).getText();
            Double dClose = parseDouble(sClose);
            String sLow = cells.get(3).getText();
            Double dLow = parseDouble(sLow);
            String sHigh = cells.get(5).getText();
            Double dHigh = parseDouble(sHigh);
            String sVolume = cells.get(6).getText();
            int iVolume = parseVolume(sVolume);

            DayPriceRecord dpr = new DayPriceRecord(iDay, aMonth, aYear,
                    dOpen, dHigh, dLow, dClose, iVolume);

            System.out.println(dpr);
            result.add(dpr);
        }

        return result;
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


    public String returnBeleggerLinkDay(String aTicker) throws Exception {
        GetPriceHistory.BeleggerLinkRec refRec = bMap.get(aTicker);
        if (refRec == null)
            throw new Exception("Ticker not found:" + aTicker);
        return "https://www.belegger.nl/Aandeel-Koers/" + refRec.Stocknr + "/" + refRec.Stockname + "/koers.aspx";
    }

  /*  public DayPriceRecord getTodaysPrice(String aTicker) throws Exception {
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
*/

    public String returnBeleggerLink(String aTicker, int aYear, int aMonth) throws Exception {

        GetPriceHistory.BeleggerLinkRec refRec = bMap.get(aTicker);
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

        return "https://www.iex.nl/Aandeel-Koers/" + refRec.Stocknr + "/" + refRec.Stockname
                + "/historische-koersen.aspx?maand=" + months;
    }

 /*   private static DayPriceRecord process_row(int aYear, int aMonth, Elements e_cells) {
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
*/
}
