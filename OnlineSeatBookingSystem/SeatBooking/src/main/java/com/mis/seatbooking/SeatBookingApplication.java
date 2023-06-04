package com.mis.seatbooking;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        }
)
@ComponentScan("com.*")
@EntityScan("com.*")
@EnableJpaRepositories("com.mis.bookingrepositories")
public class SeatBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeatBookingApplication.class, args);
    }

}
