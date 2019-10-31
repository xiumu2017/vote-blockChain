package com.gaoshan.linkvote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Paradise
 */
@EnableScheduling
@SpringBootApplication
public class LinkVoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkVoteApplication.class, args);
    }

}
