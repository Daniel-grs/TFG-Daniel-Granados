package es.metrica.sept25.evolutivo.controller.maps.routes.options;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.user.preferences.EnumOptionDTO;
import es.metrica.sept25.evolutivo.service.maps.routes.options.RouteOptionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Opciones de ruta", description = "Endpoints para obtener opciones "
		+ "de combustibles, tipos de mapa y emisiones por defecto")
@RequestMapping("/api/route-options")
public class RouteOptionsController {
	@Autowired
	private RouteOptionsService service;

    @Operation(
    		summary = "Devuelve los tipos de combustible disponibles en la API"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datos recuperados con éxito"),
    })
	@GetMapping("/fuels")
	public List<EnumOptionDTO> getFuels() {
		return service.getFuelTypes(LocaleContextHolder.getLocale());
	}

    @Operation(
    		summary = "Devuelve los tipos de vista para mapas disponibles en la API"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datos recuperados con éxito"),
    })
	@GetMapping("/map-types")
	public List<EnumOptionDTO> getMapTypes() {
		return service.getMapViewTypes(LocaleContextHolder.getLocale());
	}

    @Operation(
    		summary = "Devuelve los tipos de emisiones de gas disponibles en la API"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datos recuperados con éxito"),
    })
	@GetMapping("/emissions")
	public List<EnumOptionDTO> getEmissions() {
		return service.getEmissionTypes(LocaleContextHolder.getLocale());
	}
}
