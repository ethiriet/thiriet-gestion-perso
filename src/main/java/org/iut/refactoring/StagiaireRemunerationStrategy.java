package org.iut.refactoring;

public class StagiaireRemunerationStrategy implements RemunerationStrategy {

    @Override
    public double calculSalaire(Employe employe) {
        double salaireDeBase = employe.getSalaireDeBase();
        return salaireDeBase * 0.6;
    }

    @Override
    public double calculBonusAnnuel(Employe employe) {
        return 0; // pas de bonus
    }
}