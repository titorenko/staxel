package uk.elementarysoftware.staxel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CursorTest {
    private final StaxelReaderFactory f = new StaxelReaderFactory();
    
    @Test
    public void testTopLevelCursorPath() throws Exception {
        List<String> eventPaths = new ArrayList<>();
        try(StaxelReader r = f.fromStream(getClass().getResourceAsStream("/weather-example-small.xml"))) {
            Cursor cursor = r.getCursor();
            cursor.forEach(e -> eventPaths.add(e.getPath().toString()));
        }
        assertArrayEquals(new String[] {
            "[weatherdata]",
            "[weatherdata, location]",
            "[weatherdata, location, name]",
            "[weatherdata, location, timezone]",
            "[weatherdata, sun]",
            "[weatherdata, forecast]",
            "[weatherdata, forecast, time]",
            "[weatherdata, forecast, time, symbol]",
            "[weatherdata, forecast, time, precipitation]",
            "[weatherdata, forecast, time]",
            "[weatherdata, forecast, time, symbol]",
            "[weatherdata, forecast, time, precipitation]"
        }, eventPaths.toArray(new String[0]));
    }
    
    @Test
    public void testTopLevelCursorFirstElement() throws Exception {
        try(StaxelReader r = f.fromStream(getClass().getResourceAsStream("/weather-example-small.xml"))) {
            Cursor cursor = r.getCursor();
            XMLElement first = cursor.iterator().next();
            assertEquals("[weatherdata]", first.getPath().toString());
            assertEquals("ok", first.getAttribute("quality"));
        }
    }
    
    @Test
    public void testSimpleChildCursor() throws Exception {
        List<String> parseResults = new ArrayList<>();
        try(StaxelReader r = f.fromStream(getClass().getResourceAsStream("/weather-example-small.xml"))) {
            for (XMLElement e : r.getCursor()) {
                if (e.pathEndsWith("forecast", "time")) {
                    parseResults.add(e.parseWithChildCursor(this::parseTimeForecast));
                }
            }
        }
        assertArrayEquals(new String[] {
                "2016-08-04T12:00:00 - overcast clouds",
                "2016-08-04T15:00:00 - overcast clouds"
            }, parseResults.toArray(new String[0]));
    }

    private String parseTimeForecast(Cursor c) {
        StringBuffer result = new StringBuffer();
        for (XMLElement e : c) {
            switch(e.getName()) {
            case "time": 
                result.append(e.getAttribute("from"));
                result.append(" - ");
                break;
            case "symbol": 
                result.append(e.getText());
                break;
            case "precipitation":
                e.parseWithChildCursor(this::parsePrecipitation);
                break;
            }
        }
        return result.toString();
    }
    
    private void parsePrecipitation(Cursor c) {
        //just testing that empty child cursor works
    }
}