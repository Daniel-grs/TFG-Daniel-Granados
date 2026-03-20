public class UserSavedGasStationDto {

    private String alias;
    private Long idEstacion;
    private String nombreEstacion;
    private String marca;
    private String direccion;
    private String localidad;
    private String provincia;

    public UserSavedGasStationDto() {}

    public UserSavedGasStationDto(String alias, Long idEstacion, String nombreEstacion, String marca,
                                 String direccion, String localidad, String provincia) {
        this.alias = alias;
        this.idEstacion = idEstacion;
        this.nombreEstacion = nombreEstacion;
        this.marca = marca;
        this.direccion = direccion;
        this.localidad = localidad;
        this.provincia = provincia;
    }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public Long getIdEstacion() { return idEstacion; }
    public void setIdEstacion(Long idEstacion) { this.idEstacion = idEstacion; }

    public String getNombreEstacion() { return nombreEstacion; }
    public void setNombreEstacion(String nombreEstacion) { this.nombreEstacion = nombreEstacion; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
}