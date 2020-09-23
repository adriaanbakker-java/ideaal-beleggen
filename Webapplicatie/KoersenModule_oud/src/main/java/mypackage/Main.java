package mypackage;


import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    static String sUrl = "https://www.iex.nl/Aandeel-Koers/613007/ADYEN-NV/historische-koersen.aspx?maand=1";
    static String expectedTitle = "ADYEN NV » Historische koersen (Aandeel) | IEX.nl";
//    static String sUrl1 = "http://demo.guru99.com/test/newtours/";
//    static String expectedTitle1 = "Welcome: Mercury Tours";


/*
    private static void demoFirefoxDriver() {
        System.setProperty("webdriver.gecko.driver","D:\\installatiepakketten\\Selenium\\geckodriver.exe");
        WebDriver driver = new FirefoxDriver();
        //comment the above 2 lines and uncomment below 2 lines to use Chrome
        //System.setProperty("webdriver.chrome.driver","G:\\chromedriver.exe");
        //WebDriver driver = new ChromeDriver();

        String baseUrl = sUrl;
        String actualTitle = "";

        // launch Fire fox and direct it to the Base URL
        driver.get(baseUrl);

        // get the actual value of the title
        actualTitle = driver.getTitle();

        List<WebElement> tables = driver.findElements(By.className("ContentTable"));
        List<WebElement> tbody = tables.get(1).findElements(By.tagName("tbody"));
        List<WebElement> rows = tbody.get(0).findElements(By.tagName("tr"));
        List<WebElement> cells = rows.get(0).findElements(By.tagName("td"));

        String sOpen = cells.get(1).getText();
        String sClose = cells.get(2).getText();
        String sLow = cells.get(3).getText();
        String sHigh = cells.get(5).getText();
        String sVolume = cells.get(6).getText();

         * compare the actual title of the page with the expected one and print
         * the result as "Passed" or "Failed"

        if (actualTitle.contentEquals(expectedTitle)){
            System.out.println("Test Passed!");
            System.out.println("open:" + sOpen + " hoog:" + sHigh + " laag: " + sLow + " slot:" + sClose +
                    " volume: " + sVolume);
        } else {
            System.out.println("Test Failed, title not equal");
        }

        //close Fire fox
        driver.close();
    }


    private static void demoHeadlessDriverPrices() {
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

        String sOpen = cells.get(1).getText();
        String sClose = cells.get(2).getText();
        String sLow = cells.get(3).getText();
        String sHigh = cells.get(5).getText();
        String sVolume = cells.get(6).getText();

        System.out.println("open:" + sOpen + " hoog:" + sHigh + " laag: " + sLow + " slot:" + sClose +
                " volume: " + sVolume);


         * compare the actual title of the page with the expected one and print
         * the result as "Passed" or "Failed"

        if (actualTitle.contentEquals(expectedTitle)){
            System.out.println("Test Passed, expected title found");
        } else {
            System.out.println("Test Failed, title not equal");
        }

        //close Fire fox
        driver.close();
    }
    private static void demoHeadlessDriver() {
        System.out.println("Demo htmlUnitDriver");
        WebDriver driver = new HtmlUnitDriver();
        // Navigate to Google
        driver.get("http://www.google.com");

        // Locate the searchbox using its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter a search query
        element.sendKeys("Guru99");

        // Submit the query. Webdriver searches for the form using the text input element automatically
        // No need to locate/find the submit button
        element.submit();

        // This code will print the page title
        System.out.println("Page title is: " + driver.getTitle());

        driver.quit();

    }
*/

/*    private static void demoPhantomJSDriver() {
        System.out.println("Demo demoPhantomJSDriver");
        File file = new File("C:\\Program Files\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
        System.setProperty("phantomjs.binary.path", file.getAbsolutePath());
        WebDriver driver = new PhantomJSDriver();
        driver.get("http://www.google.com");
        WebElement element = driver.findElement(By.name("q"));
        element.sendKeys("Guru99");
        element.submit();
        System.out.println("Page title is: " + driver.getTitle());
        driver.quit();
    }
*/

/*    private static void demoPhantomJSDriverGetPrices() {
        System.out.println("Demo demoPhantomJSDriver");
        File file = new File("C:\\Program Files\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
        System.setProperty("phantomjs.binary.path", file.getAbsolutePath());
        WebDriver driver = new PhantomJSDriver();
// Navigate to Google
        String baseURL = sUrl;
        driver.get(baseURL);

        String actualTitle = driver.getTitle();
        System.out.println("page title:" + actualTitle);


        List<WebElement> tables = driver.findElements(By.className("ContentTable"));
        List<WebElement> tbody = tables.get(1).findElements(By.tagName("tbody"));
        List<WebElement> rows = tbody.get(0).findElements(By.tagName("tr"));
        List<WebElement> cells = rows.get(0).findElements(By.tagName("td"));

        String sOpen = cells.get(1).getText();
        String sClose = cells.get(2).getText();
        String sLow = cells.get(3).getText();
        String sHigh = cells.get(5).getText();
        String sVolume = cells.get(6).getText();

        System.out.println("open:" + sOpen + " hoog:" + sHigh + " laag: " + sLow + " slot:" + sClose +
                " volume: " + sVolume);
        driver.quit();
    }
*/
    public static void getPrices(String[] args) {
        System.out.println("Koersenmodule: java -jar Koersenmodule<..>.jar [endYear endMonth]");
        System.out.println("ververst koersen in " + Constants.getPricefolder());
        System.out.println("tot aan huidige datum of (indien gegeven) endYear endMonth");
        System.out.println("Door de week zal de laatste beursdag ontbreken wegens nog niet aanwezig op iex");


        //demoFirefoxDriver();
        //demoHeadlessDriver();
        //demoHeadlessDriverPrices();
        //demoPhantomJSDriver();
        //demoPhantomJSDriverGetPrices();
        System.out.println("Webcomponent aanmaken, duurt ongeveer 20 seconden...");
        System.out.println("svp de volgende waarschuwingen negeren!");
        GetPriceHistory gph = new GetPriceHistory();

        //gph.testGetPricesMonth();

       //Constants.setPricefolder(txtKoersfolder.getText());


        MainController.logInTextArea("koersen verversen");
        int endYear = -1;
        int endMonth = -1;
        if (args.length == 2) {
            System.out.println("arguments given: endYear=" + args[0] + " endMonth=" + args[1]);
            endYear = Integer.parseInt(args[0]);
            endMonth = Integer.parseInt(args[1]);
        } else {
            System.out.println("no arguments");
        }
        /*
        try {
            Set<String> tickerSet = gph.getTickers();

            for (String ticker1 : tickerSet) {
                if (ticker1.equals("hier ticket")) {
                    System.out.println("Skip" + ticker1+"  for now");

                } else {
                    MainController.LocalLogging localLogging = new MainController.LocalLogging();
                    gph.updatePriceHistory(
                            ticker1,
                            Constants.startYear, 1,
                            endYear, endMonth,
                            localLogging);
                }

            }
        } catch (Exception e) {
            MainController.logInTextArea(e.getLocalizedMessage());
            MainController.logInTextArea(e.getLocalizedMessage());
        }
        */

        MainController.logInTextArea( "koersen ververst");


        gph.closeDriver();
    }

}
