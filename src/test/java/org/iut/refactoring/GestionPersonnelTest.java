package org.iut.refactoring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GestionPersonnelTest {

    private GestionPersonnel gestion;

    @BeforeEach
    void setUp() {
        gestion = new GestionPersonnel();
    }

    @Test
    void testAjouteSalarie_Developpeur_SalaireInitial() {
        double base = 2000.0;
        gestion.ajouteSalarie("DEVELOPPEUR", "Alice", base, 3, "DEV");

        assertEquals(1, gestion.employes.size());

        Employe emp = gestion.employes.get(0);
        String id = emp.getId();

        // Dans ajouteSalarie : pour DEVELOPPEUR, experience <= 5 : base * 1.2
        double expectedSalaireInitial = base * 1.2;
        Double salaireEnregistre = gestion.salairesEmployes.get(id);

        assertNotNull(salaireEnregistre);
        assertEquals(expectedSalaireInitial, salaireEnregistre, 0.0001);

        // Vérification du type interne
        assertEquals(TypeEmploye.DEVELOPPEUR, emp.getType());

        // Un log doit être ajouté
        assertEquals(1, gestion.logs.size());
        assertTrue(gestion.logs.get(0).contains("Ajout de l'employé: Alice"));
    }

    @Test
    void testAjouteSalarie_Developpeur_ExperienceSup5() {
        double base = 2000.0;
        gestion.ajouteSalarie("DEVELOPPEUR", "Bob", base, 6, "DEV");

        Employe emp = gestion.employes.get(0);
        String id = emp.getId();

        // Dans ajouteSalarie : base * 1.2 puis si exp > 5, * 1.15
        double expectedSalaireInitial = base * 1.2 * 1.15;
        Double salaireEnregistre = gestion.salairesEmployes.get(id);

        assertEquals(expectedSalaireInitial, salaireEnregistre, 0.0001);
        assertEquals(TypeEmploye.DEVELOPPEUR, emp.getType());
    }

    @Test
    void testCalculSalaire_Developpeur_SeniorEtTrèsSenior() {
        double base = 2000.0;

        // Dev avec 8 ans d'expérience (>5 mais <=10)
        gestion.ajouteSalarie("DEVELOPPEUR", "Charlie", base, 8, "DEV");
        String id1 = gestion.employes.get(0).getId();

        double salaire1 = gestion.calculSalaire(id1);
        double expected1 = base * 1.2 * 1.15; // pas de bonus >10
        assertEquals(expected1, salaire1, 0.0001);

        // Dev avec 12 ans d'expérience (>10 donc bonus supplémentaire)
        gestion.ajouteSalarie("DEVELOPPEUR", "David", base, 12, "DEV");
        String id2 = gestion.employes.get(1).getId();

        double salaire2 = gestion.calculSalaire(id2);
        double expected2 = base * 1.2 * 1.15 * 1.05;
        assertEquals(expected2, salaire2, 0.0001);
    }

    @Test
    void testCalculSalaire_ChefDeProjet() {
        double base = 3000.0;

        // Chef de projet avec 2 ans d’expérience (sans multiplicateur exp>3)
        gestion.ajouteSalarie("CHEF DE PROJET", "Emma", base, 2, "CP");
        String id1 = gestion.employes.get(0).getId();
        double salaire1 = gestion.calculSalaire(id1);

        // calculSalaire : base * 1.5 (+ 5000, mais pas de *1.1 car exp <=3)
        double expected1 = base * 1.5 + 5000;
        assertEquals(expected1, salaire1, 0.0001);

        // Chef de projet avec 5 ans d’expérience (avec multiplicateur exp>3)
        gestion.ajouteSalarie("CHEF DE PROJET", "Fred", base, 5, "CP");
        String id2 = gestion.employes.get(1).getId();
        double salaire2 = gestion.calculSalaire(id2);

        // calculSalaire : base * 1.5 * 1.1 + 5000
        double expected2 = base * 1.5 * 1.1 + 5000;
        assertEquals(expected2, salaire2, 0.0001);
    }

    @Test
    void testCalculSalaire_Stagiaire() {
        double base = 1000.0;
        gestion.ajouteSalarie("STAGIAIRE", "Greg", base, 1, "INTERN");

        String id = gestion.employes.get(0).getId();
        double salaire = gestion.calculSalaire(id);

        double expected = base * 0.6;
        assertEquals(expected, salaire, 0.0001);
    }

    @Test
    void testCalculSalaire_EmployeInconnu() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            double salaire = gestion.calculSalaire("inexistant");
            assertEquals(0.0, salaire, 0.0001);
            assertTrue(outContent.toString().contains("ERREUR: impossible de trouver l'employé"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testGenerationRapport_Salaire_AjouteLog() {
        gestion.ajouteSalarie("DEVELOPPEUR", "Alice", 2000.0, 3, "DEV");
        gestion.ajouteSalarie("STAGIAIRE", "Bob", 1000.0, 1, "INTERN");

        int logsAvant = gestion.logs.size();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            gestion.generationRapport("SALAIRE", null);

            String output = outContent.toString();
            assertTrue(output.contains("=== RAPPORT: SALAIRE ==="));
            assertTrue(output.contains("Alice"));
            assertTrue(output.contains("Bob"));
        } finally {
            System.setOut(originalOut);
        }

        // Un log supplémentaire doit avoir été ajouté
        assertEquals(logsAvant + 1, gestion.logs.size());
        assertTrue(gestion.logs.get(gestion.logs.size() - 1).contains("Rapport généré: SALAIRE"));
    }

    @Test
    void testGenerationRapport_Experience_FiltreDivision() {
        gestion.ajouteSalarie("DEVELOPPEUR", "Alice", 2000.0, 3, "DEV");
        gestion.ajouteSalarie("DEVELOPPEUR", "Bob", 2000.0, 5, "DEV");
        gestion.ajouteSalarie("STAGIAIRE", "Clara", 1000.0, 1, "INTERN");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            gestion.generationRapport("EXPERIENCE", "DEV");

            String output = outContent.toString();
            assertTrue(output.contains("Alice: 3 années"));
            assertTrue(output.contains("Bob: 5 années"));
            assertFalse(output.contains("Clara"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testGenerationRapport_Division() {
        gestion.ajouteSalarie("DEVELOPPEUR", "Alice", 2000.0, 3, "DEV");
        gestion.ajouteSalarie("DEVELOPPEUR", "Bob", 2000.0, 5, "DEV");
        gestion.ajouteSalarie("STAGIAIRE", "Clara", 1000.0, 1, "INTERN");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            gestion.generationRapport("DIVISION", null);

            String output = outContent.toString();
            assertTrue(output.contains("DEV: 2 employés"));
            assertTrue(output.contains("INTERN: 1 employés"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testAvancementEmploye_PromotionMetAJourTypeEtSalaire() {
        double base = 2000.0;
        gestion.ajouteSalarie("STAGIAIRE", "Alice", base, 1, "DEV");
        Employe emp = gestion.employes.get(0);
        String id = emp.getId();

        int logsAvant = gestion.logs.size();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            gestion.avancementEmploye(id, "DEVELOPPEUR");

            String output = outContent.toString();
            assertTrue(output.contains("Employé promu avec succès!"));
        } finally {
            System.setOut(originalOut);
        }

        // Type mis à jour (enum)
        assertEquals(TypeEmploye.DEVELOPPEUR, emp.getType());

        // Salaire conforme au calculSalaire pour ce nouvel état
        double salaireCalcule = gestion.calculSalaire(id);
        Double salaireEnregistre = gestion.salairesEmployes.get(id);
        assertEquals(salaireCalcule, salaireEnregistre, 0.0001);

        // Un log en plus
        assertEquals(logsAvant + 1, gestion.logs.size());
        assertTrue(gestion.logs.get(gestion.logs.size() - 1).contains("Employé promu: Alice"));
    }

    @Test
    void testAvancementEmploye_EmployeInexistant() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            gestion.avancementEmploye("inexistant", "DEVELOPPEUR");
            String output = outContent.toString();
            assertTrue(output.contains("ERREUR: impossible de trouver l'employé"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testGetEmployesParDivision() {
        gestion.ajouteSalarie("DEVELOPPEUR", "Alice", 2000.0, 3, "DEV");
        gestion.ajouteSalarie("DEVELOPPEUR", "Bob", 2000.0, 5, "DEV");
        gestion.ajouteSalarie("STAGIAIRE", "Clara", 1000.0, 1, "INTERN");

        ArrayList<Employe> devs = gestion.getEmployesParDivision("DEV");
        ArrayList<Employe> interns = gestion.getEmployesParDivision("INTERN");

        assertEquals(2, devs.size());
        assertEquals(1, interns.size());
    }

    @Test
    void testCalculBonusAnnuel() {
        double base = 2000.0;

        // Dev exp 3
        gestion.ajouteSalarie("DEVELOPPEUR", "Alice", base, 3, "DEV");
        String idDevJunior = gestion.employes.get(0).getId();
        double bonusDevJunior = gestion.calculBonusAnnuel(idDevJunior);
        double expectedDevJunior = base * 0.1;
        assertEquals(expectedDevJunior, bonusDevJunior, 0.0001);

        // Dev exp 6 (>5)
        gestion.ajouteSalarie("DEVELOPPEUR", "Bob", base, 6, "DEV");
        String idDevSenior = gestion.employes.get(1).getId();
        double bonusDevSenior = gestion.calculBonusAnnuel(idDevSenior);
        double expectedDevSenior = base * 0.1 * 1.5;
        assertEquals(expectedDevSenior, bonusDevSenior, 0.0001);

        // Chef de projet exp 2
        gestion.ajouteSalarie("CHEF DE PROJET", "Clara", base, 2, "CP");
        String idChefJunior = gestion.employes.get(2).getId();
        double bonusChefJunior = gestion.calculBonusAnnuel(idChefJunior);
        double expectedChefJunior = base * 0.2;
        assertEquals(expectedChefJunior, bonusChefJunior, 0.0001);

        // Chef de projet exp 5 (>3)
        gestion.ajouteSalarie("CHEF DE PROJET", "David", base, 5, "CP");
        String idChefSenior = gestion.employes.get(3).getId();
        double bonusChefSenior = gestion.calculBonusAnnuel(idChefSenior);
        double expectedChefSenior = base * 0.2 * 1.3;
        assertEquals(expectedChefSenior, bonusChefSenior, 0.0001);

        // Stagiaire : bonus 0
        gestion.ajouteSalarie("STAGIAIRE", "Eve", base, 1, "INTERN");
        String idStagiaire = gestion.employes.get(4).getId();
        double bonusStagiaire = gestion.calculBonusAnnuel(idStagiaire);
        assertEquals(0.0, bonusStagiaire, 0.0001);
    }
}
