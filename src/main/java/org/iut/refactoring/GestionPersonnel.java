package org.iut.refactoring;

import java.util.*;
import java.time.*;

public class GestionPersonnel {

    public ArrayList<Employe> employes = new ArrayList<>();
    public HashMap<String, Double> salairesEmployes = new HashMap<>();
    public ArrayList<String> logs = new ArrayList<>();

    // 🔹 Stratégies de rémunération par type
    private final Map<TypeEmploye, RemunerationStrategy> remunerationStrategies = new HashMap<>();

    public GestionPersonnel() {
        // initialisation des stratégies
        remunerationStrategies.put(TypeEmploye.DEVELOPPEUR, new DeveloppeurRemunerationStrategy());
        remunerationStrategies.put(TypeEmploye.CHEF_DE_PROJET, new ChefDeProjetRemunerationStrategy());
        remunerationStrategies.put(TypeEmploye.STAGIAIRE, new StagiaireRemunerationStrategy());
        remunerationStrategies.put(TypeEmploye.AUTRE, new AutreRemunerationStrategy());
    }

    // 🔹 Méthode utilitaire : trouver un employé par son id
    private Employe findEmployeById(String employeId) {
        for (Employe e : employes) {
            if (e.getId().equals(employeId)) {
                return e;
            }
        }
        return null;
    }

    // 🔹 Méthode utilitaire : ajouter un log avec timestamp
    private void log(String message) {
        logs.add(LocalDateTime.now() + " - " + message);
    }

    // 🔹 Récupération de la stratégie associée au type
    private RemunerationStrategy getStrategy(TypeEmploye typeEmploye) {
        RemunerationStrategy strategy = remunerationStrategies.get(typeEmploye);
        if (strategy == null) {
            strategy = remunerationStrategies.get(TypeEmploye.AUTRE);
        }
        return strategy;
    }

    public void ajouteSalarie(String type, String nom, double salaireDeBase, int experience, String equipe) {
        String id = UUID.randomUUID().toString();
        TypeEmploye typeEmploye = TypeEmploye.fromString(type);

        Employe emp = new Employe(id, typeEmploye, nom, salaireDeBase, experience, equipe);
        employes.add(emp);

        // ⚠ On conserve le comportement original de ajouteSalarie
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
                // salaireFinal reste salaireDeBase
                break;
        }

        salairesEmployes.put(id, salaireFinal);

        log("Ajout de l'employé: " + nom);
    }

    public double calculSalaire(String employeId) {
        Employe emp = findEmployeById(employeId);

        if (emp == null) {
            System.out.println("ERREUR: impossible de trouver l'employé");
            return 0;
        }

        RemunerationStrategy strategy = getStrategy(emp.getType());
        return strategy.calculSalaire(emp);
    }

    public void generationRapport(String typeRapport, String filtre) {
        System.out.println("=== RAPPORT: " + typeRapport + " ===");

        if (typeRapport.equals("SALAIRE")) {
            for (Employe emp : employes) {
                if (filtre == null || filtre.isEmpty() ||
                        emp.getEquipe().equals(filtre)) {
                    String id = emp.getId();
                    String nom = emp.getNom();
                    double salaire = calculSalaire(id);
                    System.out.println(nom + ": " + salaire + " €");
                }
            }
        } else if (typeRapport.equals("EXPERIENCE")) {
            for (Employe emp : employes) {
                if (filtre == null || filtre.isEmpty() ||
                        emp.getEquipe().equals(filtre)) {
                    String nom = emp.getNom();
                    int exp = emp.getExperience();
                    System.out.println(nom + ": " + exp + " années");
                }
            }
        } else if (typeRapport.equals("DIVISION")) {
            HashMap<String, Integer> compteurDivisions = new HashMap<>();
            for (Employe emp : employes) {
                String div = emp.getEquipe();
                compteurDivisions.put(div, compteurDivisions.getOrDefault(div, 0) + 1);
            }
            for (Map.Entry<String, Integer> entry : compteurDivisions.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + " employés");
            }
        }
        log("Rapport généré: " + typeRapport);
    }

    public void avancementEmploye(String employeId, String newType) {
        Employe emp = findEmployeById(employeId);

        if (emp != null) {
            TypeEmploye nouveauType = TypeEmploye.fromString(newType);
            emp.setType(nouveauType);

            double nouveauSalaire = calculSalaire(employeId);
            salairesEmployes.put(employeId, nouveauSalaire);

            log("Employé promu: " + emp.getNom());
            System.out.println("Employé promu avec succès!");
        } else {
            System.out.println("ERREUR: impossible de trouver l'employé");
        }
    }

    public ArrayList<Employe> getEmployesParDivision(String division) {
        ArrayList<Employe> resultat = new ArrayList<>();
        for (Employe emp : employes) {
            if (emp.getEquipe().equals(division)) {
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
        Employe emp = findEmployeById(employeId);
        if (emp == null) return 0;

        RemunerationStrategy strategy = getStrategy(emp.getType());
        return strategy.calculBonusAnnuel(emp);
    }
}
