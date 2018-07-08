package ru.iswt.weather.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ru.iswt.weather.model.Forecast;
import ru.iswt.weather.model.Subscription;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("weatherService")
public class WeatherServiceImpl implements WeatherService {
    final private static String APPID = "a797e6e45fa264fca6747a6e2697df70";

    @Override
    public List<Forecast> load(Subscription subscription) {
        String s = getWeather(subscription.getCity(), subscription.getCountry());
        List<Forecast> list = getObjectData(s);
        return list;
    }

    private List<Forecast> getObjectData(String jsonInString) {
        ObjectMapper mapper = new ObjectMapper();
        List<Forecast> forecasts = new ArrayList<>();
        try {
            Object o = mapper.readValue(jsonInString, Object.class);
            if (o instanceof Map) {
                Map map = (Map) o;
                Object o2 = map.get("list");
                if (o2 instanceof List) {
                    List list = (List) o2;
                    for (Object value : list) {
                        Forecast forecast = new Forecast();
                        forecasts.add(forecast);
                        if (value instanceof Map) {
                            Map mapValue = (Map) value;
                            Map mapMain = (Map) mapValue.get("main");
                            forecast.setDate(new Date(Long.parseLong(String.valueOf(mapValue.get("dt")) + "000")));
                            forecast.setTemp(Double.parseDouble(String.valueOf(mapMain.get("temp"))));
                            List listMain = (List) mapValue.get("weather");
                            if (listMain.size() > 0) {
                                Map mapWeather = (Map) listMain.get(0);
                                mapWeather.get("description");
                                forecast.setDescription(String.valueOf(mapWeather.get("description")));
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return forecasts;
    }


    private String getWeather(String city, String country) {
        String url = "http://api.openweathermap.org/data/2.5/forecast?q=" + city + "," + country + "&units=metric&appid=" + APPID;
        Map<String, String> map = new HashMap<>();
        String res = null;
        try {
            res = executeRequest("GET", url, map, null);
        } catch (UnrecoverableKeyException | CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException | KeyManagementException e) {
            e.printStackTrace();
        }
        return res;
    }

    private String executeRequest(String method, String urlAddress, Map<String, String> map, Object o) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        String postUrl = urlAddress;
        URL url = new URL(postUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-type", "application/json");
        for (String key : map.keySet()) {
            conn.setRequestProperty(key, map.get(key));
        }
        conn.setUseCaches(false);

        if (o != null) {
            byte[] postData = new ObjectMapper().writeValueAsBytes(o);
            int postDataLength = postData.length;
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }
        }
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            return sb.toString();
        }
        return null;
    }
}
