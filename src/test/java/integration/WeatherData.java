package integration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class WeatherData {
    Location location = new Location();
    
    List<Forecast> forecasts =  new ArrayList<>();
}

class Location {
    String city;
    String country;
}


class Forecast {
    LocalDateTime from;
    LocalDateTime to;
    
    double temperature;
}