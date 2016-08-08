Staxel
=============

[![Build Status](https://travis-ci.org/titorenko/staxel.svg?branch=master)](https://travis-ci.org/titorenko/staxel)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/uk.elementarysoftware/staxel/badge.svg)](https://maven-badges.herokuapp.com/maven-central/uk.elementarysoftware/staxel/)

Staxel is a library that can greatly simplify [StAX](https://en.wikipedia.org/wiki/StAX) XML parsing.
It also supports permissive XML parsing using [StAX] without significant performance sacrifice. See example usage for more details.


Maven dependency
--------------

Available from Maven Central:

```xml
<dependency>
    <groupId>uk.elementarysoftware</groupId>
    <artifactId>staxel</artifactId>
    <version>0.1.0</version>
</dependency>
```

Example usage
--------------
Let's assume we want to parse XML response from [OpenWeatherMap](http://openweathermap.org/) public API.

Simplified repsonse to weather forecast request looks like so

```xml
<weatherdata>
    <location>
        <name>London</name>
        <country>GB</country>
    </location>
    <sun rise="2016-08-04T04:30:14" set="2016-08-04T19:41:42" />
    <forecast>
        <time from="2016-08-04T12:00:00" to="2016-08-04T15:00:00">
            <symbol>overcast clouds</symbol>
            <temperature unit="celsius" value="21.68" min="19.71" max="21.68" />
        </time>
        <time from="2016-08-04T15:00:00" to="2016-08-04T18:00:00">
            <symbol>overcast clouds</symbol>
            <temperature unit="celsius" value="20.67" min="19.19" max="20.67" />
        </time>
    </forecast>
</weatherdata>
```  

Staxel is most useful when XML to be parsed is more complex than that, but we want to keep things simple here.

Suppose we model this in Java like below, with getter/setters/constuctors/builders omitted. In real code you would
most likely want to define builder classes to simpify model objects construction.
```Java
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
```
First we create `StaxelReaderFactory` by calling it's no argument constructor and get reference to `StaxelReader` by supplying
the factory with some input, in this case `InputStream`:
```Java
StaxelReaderFactory f = new StaxelReaderFactory();
try(StaxelReader r = f.fromStream(getClass().getResourceAsStream(resource))) {
  ... parsing happens here...
}
```
`StaxelReader` is an extension of StAX API's `XMLEventReader` and adds concept of cursors. Cursors iterate over parts or whole of
the XML and allow to structure code efficiently. To create a cursor you need to specify element name (or more generally path suffix)
from which the cursor should start. Child cursors can be created at any point and can be nested as required. Let's take a look at main parsing loop:
 ```Java
 WeatherData wd = new WeatherData();
 Cursor cur = r.getCursor("weatherdata"); //cursor will start from <weatherdata> and will finish at </weatherdata>.
 for (XMLElement e : cur) { //iterate over all XML elements inside <weatherdata>
   if (e.pathEndsWith("location")) { // is this location
     wd.location = e.parseWithChildCursor(this::parseLocation); //create child cursor to parse location data
   } else if (e.pathEndsWith("forecast", "time")) { //is this forecast, note how we can check parent element name as well as actual element name
     wd.forecasts.add(e.parseWithChildCursor(this::parseForecast));  //create child cursor to parse forecast data
   }
 }
```
and the rest
 ```Java
private Location parseLocation(Cursor cur) {
  Location loc = new Location();
  for (XMLElement e : cur) { //child cursor will iterate over <location> and all of it's child elements
      switch (e.getName()) {
      case "name":
          loc.city = e.getText();//get inner text of current element from XMLElement
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
    for (XMLElement e : cur) { //child cursor will iterate over <forecast> and all of it's child elements
        switch (e.getName()) {
        case "time":
            f.from = LocalDateTime.parse(e.getAttribute("from")); //get attribute value from XMLElement
            f.to = LocalDateTime.parse(e.getAttribute("to"));
            break;
        case "temperature":
            f.temperature = Double.parseDouble(e.getAttribute("value"));
            break;
        }
    }
    return f;
}
 ```

Note how easy it is with Staxel to structure the code to parse XML fragments to particular model classes. The code that parses fragments
is unaware of absolute position of the fragment in the XML tree. Also note that if you want to check parent element name it is easily
possible with `XMLElement.pathEndsWith(String... suffix)` method as was done to parse forecast `pathEndsWith("forecast", "time")`.

For full source look at `WeatherDataParsingIntegrationTest`.

Prerequisites
--------------
Staxel requires Java 8 and has no other dependencies.

License
--------------
Library is licensed under the terms of [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).
