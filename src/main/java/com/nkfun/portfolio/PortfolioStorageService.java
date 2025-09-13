package com.nkfun.portfolio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
public class PortfolioStorageService {
    
    private static final String FILE_NAME = "portfolio.json";
    private final ObjectMapper objectMapper; // for conversion between JSON and Java objects

    public PortfolioStorageService(){
        this.objectMapper = new ObjectMapper();
        // accessing the variables in the json file not their getter and setters: 
        // Accessing the stocks list in Portfolio only
        this.objectMapper.setVisibility(PropertyAccessor.FIELD,JsonAutoDetect.Visibility.ANY);
        // formatting the display in the json file:
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); 
    }

    // saving the portfolio onto the json file:
    public void savePortfolio(Portfolio portfolio){
        try{
            objectMapper.writeValue(new File(FILE_NAME), portfolio);
            System.out.println("Portfolio successfully saved to : " + FILE_NAME);

        }
        catch(IOException e){
            System.err.println("Error saving the portfolio: " + e.getMessage());
        }
    }

    // loading the portfolio from the file:
    public Portfolio loadPortfolio(MarketDataService marketDataService){
        File file = new File(FILE_NAME);
        if(file.exists()){
            try{
                System.out.println("Loading the portfolio from: " + FILE_NAME);
                return objectMapper.readValue(file, Portfolio.class);
            }
            catch(IOException e){
                System.err.println("Error loading the portfolio, creating a new one: " + e.getMessage());
            }
        }
        System.out.println("No saved portfolio found! Creating a new Portfolio");
        return new Portfolio(marketDataService);
    }

}
