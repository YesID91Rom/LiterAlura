package com.alura.literAlura.Model;

public enum Idioma {

    ES("es", "Español"),
    EN("en", "Inglés"),
    FR("fr", "Francés"),
    PT("pt", "Portugués");

    private String idiomaSigla;
    private String idiomaEspanol;

    Idioma(String idiomaSigla, String idiomaEspanol) {
        this.idiomaSigla = idiomaSigla;
        this.idiomaEspanol = idiomaEspanol;
    }
    // Obteniendo un idioma desde su sigla
    public static Idioma fromString(String text) {
        for (Idioma idioma : Idioma.values()){
            if (idioma.idiomaSigla.equalsIgnoreCase(text)){
                return idioma;
            }
        }
        throw new IllegalArgumentException("No se encontró el idioma ingresado: " + text);
    }
    // Verificando si una sigla es válida
    public static boolean IdiomaValido(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.idiomaSigla.equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }

    public String getIdiomaSigla() {
        return idiomaSigla;
    }

    public String getIdiomaEspanol() {
        return idiomaEspanol;
    }
}
