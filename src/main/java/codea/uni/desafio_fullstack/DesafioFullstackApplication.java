package codea.uni.desafio_fullstack;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DesafioFullstackApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DesafioFullstackApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
