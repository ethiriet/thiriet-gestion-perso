package org.iut.refactoring;

public class Employe {

    private final String id;
    private TypeEmploye type;
    private String nom;
    private double salaireDeBase;
    private int experience;
    private String equipe;

    public Employe(String id, TypeEmploye type, String nom,
                   double salaireDeBase, int experience, String equipe) {
        this.id = id;
        this.type = type;
        this.nom = nom;
        this.salaireDeBase = salaireDeBase;
        this.experience = experience;
        this.equipe = equipe;
    }

    public String getId() {
        return id;
    }

    public TypeEmploye getType() {
        return type;
    }

    public void setType(TypeEmploye type) {
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getSalaireDeBase() {
        return salaireDeBase;
    }

    public void setSalaireDeBase(double salaireDeBase) {
        this.salaireDeBase = salaireDeBase;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getEquipe() {
        return equipe;
    }

    public void setEquipe(String equipe) {
        this.equipe = equipe;
    }
}
