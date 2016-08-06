package integration;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import uk.elementarysoftware.staxel.Cursor;
import uk.elementarysoftware.staxel.StaxelReader;
import uk.elementarysoftware.staxel.StaxelReaderFactory;
import uk.elementarysoftware.staxel.XMLElement;

@RunWith(Parameterized.class)
public class WeatherDataParsingIntegrationTest {
    
    @Parameters
    public static Collection<Object[]> xmls() {
        return Arrays.asList(new Object[][] {     
            {"/weather-example.xml"},  {"/weather-example-nested.xml"}  
        });
    }

    private String resource;
    
    public WeatherDataParsingIntegrationTest(String resource) {
        this.resource = resource;
    }
    
    @Test
    public void testNormalParsingEndToEnd() {
        StaxelReaderFactory f = new StaxelReaderFactory();
        try(StaxelReader r = f.fromStream(getClass().getResourceAsStream(resource))) {
            WeatherData wd = parse(r);
           
            assertEquals("London", wd.location.city);
            assertEquals("GB", wd.location.country);
            
            assertEquals(2, wd.forecasts.size());
            assertEquals("2016-08-04T12:00", wd.forecasts.get(0).from.toString());
            assertEquals("2016-08-04T15:00", wd.forecasts.get(0).to.toString());
            assertEquals(21.68, wd.forecasts.get(0).temperature, 1E-16);
            
            assertEquals("2016-08-04T15:00", wd.forecasts.get(1).from.toString());
            assertEquals("2016-08-04T18:00", wd.forecasts.get(1).to.toString());
            assertEquals(20.67, wd.forecasts.get(1).temperature, 1E-16);
        }
    }

    private WeatherData parse(StaxelReader r) {
        WeatherData wd = new WeatherData();
        Cursor cur = r.getCursor("weatherdata");
        for (XMLElement e : cur) {
            if (e.pathEndsWith("location")) {
                wd.location = e.parseWithChildCursor(this::parseLocation);
            } else if (e.pathEndsWith("forecast", "time")) {
                wd.forecasts.add(e.parseWithChildCursor(this::parseForecast));
            }
        }
        return wd;
    }
    
    private Location parseLocation(Cursor cur) {
        Location loc = new Location();
        for (XMLElement e : cur) {
            switch (e.getName()) {
            case "name": 
                loc.city = e.getText();
                break;
            case "country": 
                loc.country = e.getText();
                break;
            }
        }
        return loc;
    }
    
    private Forecast parseForecast(Cursor cur) {
        Forecast f = new Forecast();
        for (XMLElement e : cur) {
            switch (e.getName()) {
            case "time": 
                f.from = LocalDateTime.parse(e.getAttribute("from"));
                f.to = LocalDateTime.parse(e.getAttribute("to"));
                break;
            case "temperature": 
                f.temperature = Double.parseDouble(e.getAttribute("value"));
                break;
            }
        }
        return f;
        
    }
}
