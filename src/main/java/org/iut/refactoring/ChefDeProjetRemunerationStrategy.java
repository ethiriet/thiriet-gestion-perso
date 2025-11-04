package org.iut.refactoring;

public class ChefDeProjetRemunerationStrategy implements RemunerationStrategy {

    @Override
    public double calculSalaire(Employe employe) {
        double salaireDeBase = employe.getSalaireDeBase();
        int experience = employe.getExperience();

        double salaireFinal = salaireDeBase * 1.5;
        if (experience > 3) {
            salaireFinal = salaireFinal * 1.1;
        }
        salaireFinal = salaireFinal + 5000;
        return salaireFinal;
    }

    @Override
    public double calculBonusAnnuel(Employe employe) {
        double salaireDeBase = employe.getSalaireDeBase();
        int experience = employe.getExperience();

        double bonus = salaireDeBase * 0.2;
        if (experience > 3) {
            bonus = bonus * 1.3;
        }
        return bonus;
    }
}
