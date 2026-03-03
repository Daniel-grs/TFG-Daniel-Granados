package es.metrica.sept25.evolutivo.domain.dto.maps.geocode;

public class GeocodeResultAddress {

	private AddressComponent[] address_components;

	public AddressComponent[] getAddress_components() {
		return address_components;
	}

	public void setAddress_components(AddressComponent[] address_components) {
		this.address_components = address_components;
	}
}
