package es.metrica.sept25.evolutivo.domain.dto.user.preferences;

import java.util.List;

public class PreferencesOptionsDTO {

	private List<EnumOptionDTO> languages;
    private List<EnumOptionDTO> themes;
	public PreferencesOptionsDTO(List<EnumOptionDTO> languages, List<EnumOptionDTO> themes) {
		super();
		this.languages = languages;
		this.themes = themes;
	}
	public List<EnumOptionDTO> getLanguages() {
		return languages;
	}
	public List<EnumOptionDTO> getThemes() {
		return themes;
	}
	
    
    
}
