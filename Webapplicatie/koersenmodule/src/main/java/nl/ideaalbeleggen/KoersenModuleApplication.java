package nl.ideaalbeleggen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/koersen")
public class KoersenModuleApplication {


    @RequestMapping(value = "/verversen")
    public String verversen(@RequestParam(value="eindJaar", defaultValue = "") String eindJaar,
                            @RequestParam(value="eindMaand", defaultValue = "") String eindMaand) {

        String[] args = new String[] {};

        if (!eindJaar.equals("")) { args = new String[] {"1", "2"};
            args[0] = eindJaar;
            args[1] = eindMaand;
        }
        Main.main(args);

        return "Greetings from Spring Boot!";
    }


    public static void main(String[] args) {
    	SpringApplication.run(KoersenModuleApplication.class, args);
    }

}
