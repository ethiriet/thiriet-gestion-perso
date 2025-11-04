package org.iut.refactoring;

public class DeveloppeurRemunerationStrategy implements RemunerationStrategy {

    @Override
    public double calculSalaire(Employe employe) {
        double salaireDeBase = employe.getSalaireDeBase();
        int experience = employe.getExperience();

        double salaireFinal = salaireDeBase * 1.2;
        if (experience > 5) {
            salaireFinal = salaireFinal * 1.15;
        }
        if (experience > 10) {
            salaireFinal = salaireFinal * 1.05; // bonus supplémentaire
        }
        return salaireFinal;
    }

    @Override
    public double calculBonusAnnuel(Employe employe) {
        double salaireDeBase = employe.getSalaireDeBase();
        int experience = employe.getExperience();

        double bonus = salaireDeBase * 0.1;
        if (experience > 5) {
            bonus = bonus * 1.5;
        }
        return bonus;
    }
}
