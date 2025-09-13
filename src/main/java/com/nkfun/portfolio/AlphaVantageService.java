package com.nkfun.portfolio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class AlphaVantageService implements MarketDataService {
    
    private static final String API_KEY = "DM5QP8DZ7K049IKK";
    private static final String BASE_URL = "https://www.alphavantage.co/query";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public double getPrice(String tickerSymbol){
        String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", 
                                   BASE_URL, tickerSymbol, API_KEY); // creating the full web address for the stock to be fetched
        try{
            // Creating and sending the http request:
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).build(); // preparing the request by creating a new request with the url to specify the target
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()); // converting the response from the api to string -> decode method chosen

            // parsing the json file returned to find the live price of the stock:
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode globalQuotNode = rootNode.path("Global Quote");

            if(globalQuotNode.isMissingNode() || globalQuotNode.isEmpty()){
                System.err.println("API limit exceeded likely or the tickerSymbol is invalid: " + tickerSymbol);
                return 0.0;
            }

            // 05. price is the key in the globalQuote
            String priceString = globalQuotNode.path("05. price").asText();
            return Double.parseDouble(priceString);

        }
        catch(Exception e){
            System.err.println("Error fetching the price for : " + tickerSymbol + ":" + e.getMessage());
            return 0.0;
        }
    }
}
