package com.cw.financials;

import com.cw.financials.data.mutualfund.*;
import com.cw.financials.data.pricedata.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.time.Duration;


@Configuration
@EnableAutoConfiguration
@ComponentScan({
        "controller",
        "com.cw.financials.controller",
        "com.cw.financials.data",
        "com.cw.financials.data.mutualfund",
        "com.cw.financials.data.pricedata",
})
@SpringBootApplication
public class Application implements CommandLineRunner {
    @Autowired
    MutualFundService mfService;
    @Autowired
    PriceDataService pdService;


    private static final String mutualFundURL = "ftp://ftp.nasdaqtrader.com/symboldirectory/mfundslist.txt";


    public static void main(String[] args) throws Exception{
        //SpringApplication.run(Application.class, args);
    	System.out.println("Hello World!");
    }

    public void run(String[] args) {
        try {
            mfService.syncMutualFundsAsync(new URL(mutualFundURL), Duration.ofHours(1));
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
}

