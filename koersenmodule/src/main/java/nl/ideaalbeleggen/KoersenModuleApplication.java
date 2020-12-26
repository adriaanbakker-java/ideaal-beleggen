package nl.ideaalbeleggen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;




@SpringBootApplication
@RestController
@RequestMapping("/koersen")
public class KoersenModuleApplication {


    @Autowired
    Koersenmodule koersenModule;


    @RequestMapping(value = "/haalintradagkoers")
    public String haalintradagkoers(@RequestParam(value="ticker") String ticker)  {

        try {
            DayPriceRecord dpr = koersenModule.haalIntraDagkoers(ticker);
            return "dagkoers ververst voor " + ticker + ":" + dpr.print();
        } catch (Exception e) {
            return "Fout bij verversen dagkoers:" + e.getLocalizedMessage();
        }
    }

    /*
    *  endpoint /koersen/verversen
    *  met optionele parameters eindJaar en eindMaand
    *
    *  voorbeeld:
    *
    *             localhost:8080/koersen/verversen
    *
    *  of
    *             localhost:8080/koersen/verversen?eindJaar=2020&eindMaand=08
    *
    * Waarbij 8080 het poortnummer is uit de application.properties
    * op dit moment is dit poort 8082.
    *
    * Het request bevat een expliciete laatste maand of geen jaar/maand, in het laatste geval wordt de huidige jaar
    * /maand bepaald. Dit koppel noemen we de laatste jaar/maand
    *
    * De koersbestanden worden gelezen. Er wordt een koersenlijst opgebouwd met de eindedagkoersen die nog VOOR
    * deze jaar/maand vallen.
    * Vervolgens wordt de koersenlijst uitgebreid met de prijzen het laatste maand/jaar door deze eindedagkoersen
    * van internet te halen.
    *
    * Mocht het zo zijn dat er geen prijzen uit het bestand kunnen worden gelezen dan worden prijzen van internet
    * gelezen vanuit het startjaar/startmaand uit Constants.java (2014)
    *
    * De laatste maand van het koersenbestand wordt dus altijd overschreven.
    *
    * De folder met prijzen is (op dit moment nog) specifiek voor de laptop waar het pakket is geinstalleerd
    * en is te vinden in Constants.java.
    *
    * Koersen bestanden voor aandelen hebben het volgende formaat:
    *
    * 2019-04-08;720,20;720,20;704,00;705,20;4.098
    *
    * Er staan dus twee cijfers achter de komma. Formaat is open, hoog, laag, slot, volume.
    * Bedoeling is om het nog mogelijk te gaan maken om een dagkoers op te halen voor de laatste dag.
    * Deze wordt onderscheiden van de einde dagkoers door een tijdstip, bijvoorbeeld
    *
    * 2019-04-08;720,20;720,20;704,00;705,20;4.098;16:12
    *
    * Er komt dan tevens een endpoint bij waarbij de laatste dagkoers kan worden vervangen. De koersbestanden
    * worden dan gecheckt op compleetheid en indien compleet tot de huidige datum uitgebreid met die dagkoers
    * In de grafiek is afwijkend aangegeven dat het om een dagkoers gaat en geen eindedagkoersen. De open zal dan
    * wel kloppen maar de open, hoog, laag en slot zijn dan nog niet definitief.
    *
    * endpoint /koersen/verversenhuidigedag
    *
    *
    */
    @RequestMapping(value = "/verversen")
    public String verversen(@RequestParam(value="eindJaar", defaultValue = "") String eindJaar,
                            @RequestParam(value="eindMaand", defaultValue = "") String eindMaand)
    {

        String[] args = new String[] {};

        if (!eindJaar.equals("")) { args = new String[] {"1", "2"};
            args[0] = eindJaar;
            args[1] = eindMaand;
        }
        try {
            koersenModule.verversKoersenfiles(args);
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }

        return "Koersen ververst!";
    }


    public static void main(String[] args) {
    	SpringApplication.run(KoersenModuleApplication.class, args);
    }

}
