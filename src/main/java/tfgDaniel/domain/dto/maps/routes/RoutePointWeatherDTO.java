package tfgDaniel.domain.dto.maps.routes;

public class RoutePointWeatherDTO {

    private String address;
    private Integer estimatedArrivalHour;
    private String weatherDescription;
    private Double temperature;

    public RoutePointWeatherDTO() {}

    public RoutePointWeatherDTO(String address, Integer estimatedArrivalHour, String weatherDescription, Double temperature) {
        this.address = address;
        this.estimatedArrivalHour = estimatedArrivalHour;
        this.weatherDescription = weatherDescription;
        this.temperature = temperature;
    }

    public String getAddress() {
        return address;
    }

    public Integer getEstimatedArrivalHour() {
        return estimatedArrivalHour;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEstimatedArrivalHour(Integer estimatedArrivalHour) {
        this.estimatedArrivalHour = estimatedArrivalHour;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}