package es.metrica.sept25.evolutivo.enums;

public enum BrandEnum {
	REPSOL("Repsol"),
	CEPSA("Cepsa"),
	BP("BP"),
	GALP("Galp"),
	SHELL("Shell"),
	BALLENOIL("Ballenoil"),
	PLENOIL("Plenoil"),
	PETROPRIX("Petroprix"),
	ELECLERC("E.Leclerc"),
	BEROIL("Beroil"),
	AUTONETOIL("Autonetoil"),
	ANDAMUR("Andamur"),
	IDSQ8("IDS Q8"),
	PETRONOR("Petronor");

    private final String displayName;

    BrandEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
