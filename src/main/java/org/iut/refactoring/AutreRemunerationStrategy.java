package org.iut.refactoring;

public class AutreRemunerationStrategy implements RemunerationStrategy {

    @Override
    public double calculSalaire(Employe employe) {
        return employe.getSalaireDeBase();
    }

    @Override
    public double calculBonusAnnuel(Employe employe) {
        return 0;
    }
}
