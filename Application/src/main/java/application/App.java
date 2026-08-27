package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the NexusMarket application.
 *
 * Responsibilities:
 * - Initialize the application.
 * - Load the infrastructure.
 * - Configure dependency injection.
 * - Start the REST server.
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}