package es.metrica.sept25.evolutivo.weather;

//import static org.hamcrest.CoreMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.lang.reflect.Method;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import es.metrica.sept25.evolutivo.domain.dto.weather.Dia;
import es.metrica.sept25.evolutivo.domain.dto.weather.Prediccion;
import es.metrica.sept25.evolutivo.domain.dto.weather.Weather;
import es.metrica.sept25.evolutivo.domain.dto.weather.WeatherLink;
import es.metrica.sept25.evolutivo.service.weather.WeatherServiceImpl;

@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "API_KEY_AEMET", "TEST_API_KEY");
    }

    @SuppressWarnings("unchecked")
    @Test
    void getWeather_ok() throws Exception {
        String code = "28079";
        String datosUrl = "http://datos.test/weather.json";

        WeatherLink link = new WeatherLink();
        link.setDatos(datosUrl);

        Weather weather = buildWeather();
        List<Weather> weatherList = List.of(weather);

        when(restTemplate.getForObject(anyString(), eq(WeatherLink.class)))
                .thenReturn(link);

        when(restTemplate.getForObject(datosUrl, String.class))
                .thenReturn("json");

        when(objectMapper.readValue(eq("json"), any(TypeReference.class)))
                .thenReturn(weatherList);

        Optional<Weather> result = weatherService.getWeather(code);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getPrediccion().getDia().size());

        verify(restTemplate).getForObject(anyString(), eq(WeatherLink.class));
        verify(restTemplate).getForObject(datosUrl, String.class);
        verify(objectMapper).readValue(eq("json"), any(TypeReference.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getWeather_emptyWeatherData() throws Exception {
        String code = "28079";
        String datosUrl = "http://datos.test/weather.json";

        WeatherLink link = new WeatherLink();
        link.setDatos(datosUrl);

        when(restTemplate.getForObject(anyString(), eq(WeatherLink.class)))
                .thenReturn(link);

        when(restTemplate.getForObject(datosUrl, String.class))
                .thenReturn("json");

        when(objectMapper.readValue(eq("json"), any(TypeReference.class)))
                .thenReturn(List.of());

        Optional<Weather> result = weatherService.getWeather(code);

        assertTrue(result.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void getWeatherData_ok() throws Exception {
        String url = "http://datos.test/weather.json";
        List<Weather> list = List.of(new Weather());	

        when(restTemplate.getForObject(url, String.class)).thenReturn("json");
        when(objectMapper.readValue(eq("json"), any(TypeReference.class)))
                .thenReturn(list);

        Method method = WeatherServiceImpl.class
                .getDeclaredMethod("getWeatherData", String.class);
        method.setAccessible(true);

        List<Weather> result =
                (List<Weather>) method.invoke(weatherService, url);

        assertEquals(1, result.size());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    void getWeatherData_jsonProcessingException() throws Exception {
        String url = "http://datos.test/weather.json";

        when(restTemplate.getForObject(url, String.class)).thenReturn("json");
        when(objectMapper.readValue(eq("json"), any(TypeReference.class)))
                .thenThrow(new JsonProcessingException("error") {});

        Method method = WeatherServiceImpl.class
                .getDeclaredMethod("getWeatherData", String.class);
        method.setAccessible(true);

        List<Weather> result =
                (List<Weather>) method.invoke(weatherService, url);

        assertTrue(result.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void getFirstWeatherDay_ok() throws Exception {
        Weather weather = buildWeather();
        List<Weather> list = List.of(weather);

        Method method = WeatherServiceImpl.class
                .getDeclaredMethod("getFirstWeatherDay", List.class);
        method.setAccessible(true);

        Optional<Weather> result =
                (Optional<Weather>) method.invoke(weatherService, list);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getPrediccion().getDia().size());
    }
    @SuppressWarnings("unchecked")
    @Test
    void getFirstWeatherDay_emptyList() throws Exception {
        Method method = WeatherServiceImpl.class
                .getDeclaredMethod("getFirstWeatherDay", List.class);
        method.setAccessible(true);

        Optional<Weather> result =
                (Optional<Weather>) method.invoke(weatherService, List.of());

        assertTrue(result.isEmpty());
    }


    private Weather buildWeather() {
        Dia d1 = new Dia();
        Dia d2 = new Dia();

        Prediccion p = new Prediccion();
        p.setDia(new ArrayList<>(List.of(d1, d2)));

        Weather w = new Weather();
        w.setPrediccion(p);

        return w;
    }
}
