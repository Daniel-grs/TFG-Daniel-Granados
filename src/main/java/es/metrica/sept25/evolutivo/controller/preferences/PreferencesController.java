package es.metrica.sept25.evolutivo.controller.preferences;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.user.preferences.EnumOptionDTO;
import es.metrica.sept25.evolutivo.service.traductor.PreferencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/preferences")
@Tag(name = "Preferencias", description = "Endpoints para obtener idiomas y temas por defecto")
public class PreferencesController {

	@Autowired
	private PreferencesService preferencesService;

	@Operation(
			summary = "Devuelve los lenguajes disponibles para la API"
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Datos recuperados con éxito"),
	})
	@GetMapping("/languages")
	public List<EnumOptionDTO> getLanguages() {
		return preferencesService.getLanguages(LocaleContextHolder.getLocale());
	}

	@Operation(
			summary = "Devuelve los temas disponibles en la API"
			)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Datos recuperados con éxito"),
	})
	@GetMapping("/themes")
	public List<EnumOptionDTO> getThemes() {
		return preferencesService.getThemes(LocaleContextHolder.getLocale());
	}
}
