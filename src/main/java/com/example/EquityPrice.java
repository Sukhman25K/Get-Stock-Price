package com.example;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.http.HttpClient;
import com.google.gson.Gson;
import java.util.Map;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;


public class EquityPrice {
    private static String ticker;

    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        GetTicker(input);
        String data = APICall(System.getenv("API_KEY"),input);
        List<Float>prices = ExtractPrice(data);
        System.out.println("\nCurrent data for "+ticker);
        System.out.println("Current price: $"+prices.get(0)+"\nDaily change: "+String.format("%.2f",((prices.get(1) - prices.get(0)) / prices.get(0) * 100))+"%\nWeekly change: "+String.format("%.2f",((prices.get(2) - prices.get(0)) / prices.get(0) * 100))+"%\nMonthly change: "+String.format("%.2f",((prices.get(3) - prices.get(0)) / prices.get(0) * 100))+"%");
    }

    public static String APICall(String API_KEY,Scanner input){
        while (true){
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&outputsize=compact&apikey=%s",ticker,API_KEY)))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<String> response = null;

            try {
                response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response.body() != null && response.body().length() > 200){
                return response.body();
            }else {
                System.out.print("Invalid ticker. No traded stock found:");
                while (true){
                    ticker = input.nextLine().toUpperCase();
                    if (ValidateTicker()){
                        break;
                    }else{
                        System.out.print("Invalid ticker format. Please try again:");
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Float> ExtractPrice(String data){
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();

        Map<String, Object> parsedData = gson.fromJson(data, type);

        Map<String, Object> TimeSeries = (Map<String, Object>) parsedData.get("Time Series (Daily)");

        int i = 0;
        List<Float> prices = new ArrayList<>();

        for (Map.Entry<String, Object> entry: TimeSeries.entrySet()){
            if (i == 0){
                prices.add(Float.parseFloat(((Map<String, String>) entry.getValue()).get("4. close")));
            }else if (i == 1){
                prices.add(Float.parseFloat(((Map<String, String>) entry.getValue()).get("4. close")));
            }else if (i == 6){
                prices.add(Float.parseFloat(((Map<String, String>) entry.getValue()).get("4. close")));
            }else if (i == 27){
                prices.add(Float.parseFloat(((Map<String, String>) entry.getValue()).get("4. close")));
            }
            i++;
        }
        return prices;
    }

    public static void GetTicker(Scanner input){
        System.out.print("Enter stock ticker: ");
        ticker = input.nextLine().toUpperCase();

        while (true){
            if (ValidateTicker()){
                return;
            }else{
                System.out.print("Invalid ticker format. Please try again:");
                ticker = input.nextLine().toUpperCase();
            }
        }
    }

    public static boolean ValidateTicker(){
        String tickerPattern = "^[A-Z]{1,5}(\\.[A-Z]{1,3})?$"; 
        Pattern compiledPattern = Pattern.compile(tickerPattern);
        Matcher matcher = compiledPattern.matcher(ticker);
        return matcher.matches();
    }
}
