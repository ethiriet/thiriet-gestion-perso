package org.iut.refactoring;

public interface RemunerationStrategy {

    double calculSalaire(Employe employe);

    double calculBonusAnnuel(Employe employe);
}