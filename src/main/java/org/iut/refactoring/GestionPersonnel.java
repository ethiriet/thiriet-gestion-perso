package org.iut.refactoring;

import java.util.*;
import java.time.*;

public class GestionPersonnel {

    public ArrayList<Object[]> employes = new ArrayList<>();
    public HashMap<String, Double> salairesEmployes = new HashMap<>();
    public ArrayList<String> logs = new ArrayList<>();


    private Object[] findEmployeById(String employeId) {
        for (Object[] e : employes) {
            if (e[0].equals(employeId)) {
                return e;
            }
        }
        return null;
    }


    private void log(String message) {
        logs.add(LocalDateTime.now() + " - " + message);
    }

    public void ajouteSalarie(String type, String nom, double salaireDeBase, int experience, String equipe) {
        Object[] emp = new Object[6];
        emp[0] = UUID.randomUUID().toString();


        TypeEmploye typeEmploye = TypeEmploye.fromString(type);
        emp[1] = typeEmploye;

        emp[2] = nom;
        emp[3] = salaireDeBase;
        emp[4] = experience;
        emp[5] = equipe;

        employes.add(emp);

        double salaireFinal = salaireDeBase;
        switch (typeEmploye) {
            case DEVELOPPEUR:
                salaireFinal = salaireDeBase * 1.2;
                if (experience > 5) {
                    salaireFinal = salaireFinal * 1.15;
                }
                break;
            case CHEF_DE_PROJET:
                salaireFinal = salaireDeBase * 1.5;
                if (experience > 3) {
                    salaireFinal = salaireFinal * 1.1;
                }
                break;
            case STAGIAIRE:
                salaireFinal = salaireDeBase * 0.6;
                break;
            case AUTRE:
            default:

                break;
        }

        salairesEmployes.put((String) emp[0], salaireFinal);

        log("Ajout de l'employé: " + nom);
    }

    public double calculSalaire(String employeId) {
        Object[] emp = findEmployeById(employeId);

        if (emp == null) {
            System.out.println("ERREUR: impossible de trouver l'employé");
            return 0;
        }

        TypeEmploye type = (TypeEmploye) emp[1];
        double salaireDeBase = (double) emp[3];
        int experience = (int) emp[4];

        double salaireFinal = salaireDeBase;
        switch (type) {
            case DEVELOPPEUR:
                salaireFinal = salaireDeBase * 1.2;
                if (experience > 5) {
                    salaireFinal = salaireFinal * 1.15;
                }
                if (experience > 10) {
                    salaireFinal = salaireFinal * 1.05; // bonus
                }
                break;

            case CHEF_DE_PROJET:
                salaireFinal = salaireDeBase * 1.5;
                if (experience > 3) {
                    salaireFinal = salaireFinal * 1.1;
                }
                salaireFinal = salaireFinal + 5000; // bonus
                break;

            case STAGIAIRE:
                salaireFinal = salaireDeBase * 0.6;
                // Pas de bonus pour les stagiaires
                break;

            case AUTRE:
            default:
                // salaireFinal = salaireDeBase;
                break;
        }

        return salaireFinal;
    }

    public void generationRapport(String typeRapport, String filtre) {
        System.out.println("=== RAPPORT: " + typeRapport + " ===");

        if (typeRapport.equals("SALAIRE")) {
            for (Object[] emp : employes) {
                if (filtre == null || filtre.isEmpty() ||
                        emp[5].equals(filtre)) {
                    String id = (String) emp[0];
                    String nom = (String) emp[2];
                    double salaire = calculSalaire(id);
                    System.out.println(nom + ": " + salaire + " €");
                }
            }
        } else if (typeRapport.equals("EXPERIENCE")) {
            for (Object[] emp : employes) {
                if (filtre == null || filtre.isEmpty() ||
                        emp[5].equals(filtre)) {
                    String nom = (String) emp[2];
                    int exp = (int) emp[4];
                    System.out.println(nom + ": " + exp + " années");
                }
            }
        } else if (typeRapport.equals("DIVISION")) {
            HashMap<String, Integer> compteurDivisions = new HashMap<>();
            for (Object[] emp : employes) {
                String div = (String) emp[5];
                compteurDivisions.put(div, compteurDivisions.getOrDefault(div, 0) + 1);
            }
            for (Map.Entry<String, Integer> entry : compteurDivisions.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + " employés");
            }
        }
        log("Rapport généré: " + typeRapport);
    }

    public void avancementEmploye(String employeId, String newType) {
        Object[] emp = findEmployeById(employeId);

        if (emp != null) {
            // 🔸 On convertit aussi le nouveau type vers l'enum
            TypeEmploye nouveauType = TypeEmploye.fromString(newType);
            emp[1] = nouveauType;

            double nouveauSalaire = calculSalaire(employeId);
            salairesEmployes.put(employeId, nouveauSalaire);

            log("Employé promu: " + emp[2]);
            System.out.println("Employé promu avec succès!");
        } else {
            System.out.println("ERREUR: impossible de trouver l'employé");
        }
    }

    public ArrayList<Object[]> getEmployesParDivision(String division) {
        ArrayList<Object[]> resultat = new ArrayList<>();
        for (Object[] emp : employes) {
            if (emp[5].equals(division)) {
                resultat.add(emp);
            }
        }
        return resultat;
    }

    public void printLogs() {
        System.out.println("=== LOGS ===");
        for (String log : logs) {
            System.out.println(log);
        }
    }

    public double calculBonusAnnuel(String employeId) {
        Object[] emp = findEmployeById(employeId);
        if (emp == null) return 0;

        TypeEmploye type = (TypeEmploye) emp[1];
        int experience = (int) emp[4];
        double salaireDeBase = (double) emp[3];

        double bonus = 0;
        switch (type) {
            case DEVELOPPEUR:
                bonus = salaireDeBase * 0.1;
                if (experience > 5) {
                    bonus = bonus * 1.5;
                }
                break;

            case CHEF_DE_PROJET:
                bonus = salaireDeBase * 0.2;
                if (experience > 3) {
                    bonus = bonus * 1.3;
                }
                break;

            case STAGIAIRE:
            case AUTRE:
            default:
                // bonus = 0
                break;
        }

        return bonus;
    }
}
