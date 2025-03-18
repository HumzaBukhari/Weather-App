import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import weather.*;

public class MyWeatherAPI extends WeatherAPI{
    //Code is mostly the same, just different url and Json fields
    public static Properties getRegion(double lat, double lon){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.weather.gov/points/"+String.valueOf(lat)+","+String.valueOf(lon)))     //Goes to api.weather.gov/points/
                .build();
        HttpResponse<String> response = null;
        try{
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        }catch(Exception e){e.printStackTrace();}

        Root r = getRegionObject(response.body());
        if(r == null){
            System.err.println("Failed to parse JSon");
            return null;
        }
        return r.properties;
    }

    public static MyWeatherAPI.Root getRegionObject(String json){
        ObjectMapper om = new ObjectMapper();
        Root toRet = null;
        try{
            toRet = om.readValue(json, Root.class);
            Properties p = toRet.properties;
        }catch(JsonProcessingException e){e.printStackTrace();}
        return toRet;
    }

    //All Json fields for api.weather.gov/points
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Root{
        public String id;
        public String type;
        public Geometry geometry;
        public Properties properties;
    }

    public static class Geometry{
        public String type;
        public double[] coordinates;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties{
        public String cwa;
        public String forecastOffice;
        public String gridId;
        public int gridX;
        public int gridY;
        public String forecast;
        public String forecastHourly;
        public String forecastGridData;
        public String observationStations;
        public RelativeLocation relativeLocation;
        public String forecastZone;
        public String county;
        public String fireWeatherZone;
        public String timeZone;
        public String radarStation;
    }

    public static class RelativeLocation{
        public String type;
        public Geometry geometry;
        public RelativeLocationProperties properties;
    }

    public static class RelativeLocationProperties{
        public String city;
        public String state;
        public Distance distance;
        public Bearing bearing;
    }

    public static class Distance{
        public String unitCode;
        public double value;
    }

    public static class Bearing{
        public String unitCode;
        public double value;
    }
}