package org.iut.refactoring;

public enum TypeEmploye {
    DEVELOPPEUR("DEVELOPPEUR"),
    CHEF_DE_PROJET("CHEF DE PROJET"),
    STAGIAIRE("STAGIAIRE"),
    AUTRE("AUTRE");

    private final String label;

    TypeEmploye(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TypeEmploye fromString(String type) {
        if (type == null) {
            return AUTRE;
        }
        for (TypeEmploye t : values()) {
            if (t.label.equals(type)) {
                return t;
            }
        }
        return AUTRE;
    }
}
