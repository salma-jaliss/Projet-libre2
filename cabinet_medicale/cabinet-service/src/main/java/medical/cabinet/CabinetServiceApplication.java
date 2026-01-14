package medical.cabinet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.Arrays;

@SpringBootApplication
@EnableMethodSecurity
public class CabinetServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CabinetServiceApplication.class);
        // Si aucun profil actif n'est fourni, utiliser 'dev' par défaut
        if (System.getProperty("spring.profiles.active") == null && System.getenv("SPRING_PROFILES_ACTIVE") == null) {
            app.setAdditionalProfiles("dev");
        }
        ConfigurableApplicationContext ctx = app.run(args);
        Environment env = ctx.getEnvironment();
        System.out.println("Active profiles: " + Arrays.toString(env.getActiveProfiles()));
        // Afficher la datasource URL pour confirmer quelle DB est utilisée
        System.out.println("Datasource URL: " + env.getProperty("spring.datasource.url"));
    }

}
