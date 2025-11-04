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

        Object[] emp = gestion.employes.get(0);
        String id = (String) emp[0];


        double expectedSalaireInitial = base * 1.2;
        Double salaireEnregistre = gestion.salairesEmployes.get(id);

        assertNotNull(salaireEnregistre);
        assertEquals(expectedSalaireInitial, salaireEnregistre, 0.0001);


        assertEquals(1, gestion.logs.size());
        assertTrue(gestion.logs.get(0).contains("Ajout de l'employé: Alice"));
    }

    @Test
    void testAjouteSalarie_Developpeur_ExperienceSup5() {
        double base = 2000.0;
        gestion.ajouteSalarie("DEVELOPPEUR", "Bob", base, 6, "DEV");

        Object[] emp = gestion.employes.get(0);
        String id = (String) emp[0];


        double expectedSalaireInitial = base * 1.2 * 1.15;
        Double salaireEnregistre = gestion.salairesEmployes.get(id);

        assertEquals(expectedSalaireInitial, salaireEnregistre, 0.0001);
    }

    @Test
    void testCalculSalaire_Developpeur_SeniorEtTrèsSenior() {
        double base = 2000.0;


        gestion.ajouteSalarie("DEVELOPPEUR", "Charlie", base, 8, "DEV");
        String id1 = (String) gestion.employes.get(0)[0];

        double salaire1 = gestion.calculSalaire(id1);
        double expected1 = base * 1.2 * 1.15;
        assertEquals(expected1, salaire1, 0.0001);


        gestion.ajouteSalarie("DEVELOPPEUR", "David", base, 12, "DEV");
        String id2 = (String) gestion.employes.get(1)[0];

        double salaire2 = gestion.calculSalaire(id2);
        double expected2 = base * 1.2 * 1.15 * 1.05;
        assertEquals(expected2, salaire2, 0.0001);
    }

    @Test
    void testCalculSalaire_ChefDeProjet() {
        double base = 3000.0;


        gestion.ajouteSalarie("CHEF DE PROJET", "Emma", base, 2, "CP");
        String id1 = (String) gestion.employes.get(0)[0];
        double salaire1 = gestion.calculSalaire(id1);


        double expected1 = base * 1.5 + 5000;
        assertEquals(expected1, salaire1, 0.0001);


        gestion.ajouteSalarie("CHEF DE PROJET", "Fred", base, 5, "CP");
        String id2 = (String) gestion.employes.get(1)[0];
        double salaire2 = gestion.calculSalaire(id2);


        double expected2 = base * 1.5 * 1.1 + 5000;
        assertEquals(expected2, salaire2, 0.0001);
    }

    @Test
    void testCalculSalaire_Stagiaire() {
        double base = 1000.0;
        gestion.ajouteSalarie("STAGIAIRE", "Greg", base, 1, "INTERN");

        String id = (String) gestion.employes.get(0)[0];
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
        Object[] emp = gestion.employes.get(0);
        String id = (String) emp[0];

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


        assertEquals("DEVELOPPEUR", emp[1]);


        double salaireCalcule = gestion.calculSalaire(id);
        Double salaireEnregistre = gestion.salairesEmployes.get(id);
        assertEquals(salaireCalcule, salaireEnregistre, 0.0001);


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

        ArrayList<Object[]> devs = gestion.getEmployesParDivision("DEV");
        ArrayList<Object[]> interns = gestion.getEmployesParDivision("INTERN");

        assertEquals(2, devs.size());
        assertEquals(1, interns.size());
    }

    @Test
    void testCalculBonusAnnuel() {
        double base = 2000.0;


        gestion.ajouteSalarie("DEVELOPPEUR", "Alice", base, 3, "DEV");
        String idDevJunior = (String) gestion.employes.get(0)[0];
        double bonusDevJunior = gestion.calculBonusAnnuel(idDevJunior);
        double expectedDevJunior = base * 0.1;
        assertEquals(expectedDevJunior, bonusDevJunior, 0.0001);


        gestion.ajouteSalarie("DEVELOPPEUR", "Bob", base, 6, "DEV");
        String idDevSenior = (String) gestion.employes.get(1)[0];
        double bonusDevSenior = gestion.calculBonusAnnuel(idDevSenior);
        double expectedDevSenior = base * 0.1 * 1.5;
        assertEquals(expectedDevSenior, bonusDevSenior, 0.0001);


        gestion.ajouteSalarie("CHEF DE PROJET", "Clara", base, 2, "CP");
        String idChefJunior = (String) gestion.employes.get(2)[0];
        double bonusChefJunior = gestion.calculBonusAnnuel(idChefJunior);
        double expectedChefJunior = base * 0.2;
        assertEquals(expectedChefJunior, bonusChefJunior, 0.0001);


        gestion.ajouteSalarie("CHEF DE PROJET", "David", base, 5, "CP");
        String idChefSenior = (String) gestion.employes.get(3)[0];
        double bonusChefSenior = gestion.calculBonusAnnuel(idChefSenior);
        double expectedChefSenior = base * 0.2 * 1.3;
        assertEquals(expectedChefSenior, bonusChefSenior, 0.0001);


        gestion.ajouteSalarie("STAGIAIRE", "Eve", base, 1, "INTERN");
        String idStagiaire = (String) gestion.employes.get(4)[0];
        double bonusStagiaire = gestion.calculBonusAnnuel(idStagiaire);
        assertEquals(0.0, bonusStagiaire, 0.0001);
    }
}
