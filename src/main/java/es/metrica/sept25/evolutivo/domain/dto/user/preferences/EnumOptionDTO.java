package es.metrica.sept25.evolutivo.domain.dto.user.preferences;

public class EnumOptionDTO {

	private String code;
    private String label;

    public EnumOptionDTO(String code, String label) {
        this.code = code;
        this.label = label;
    }

	public String getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

    
}
