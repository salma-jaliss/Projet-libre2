package com.medical.appointment.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DatabaseSchemaFixer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSchemaFixer.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaFixer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Exécution du correctif de schéma de base de données...");

        // Correctifs pour la table rendez_vous
        // Suppression des anciennes colonnes qui ne correspondent plus au nouvel ERD
        // (id_utilisateur, id_patient, id_cabinet)
        fixColumn("rendez_vous", "medecin_id");
        fixColumn("rendez_vous", "patient_id");
        fixColumn("rendez_vous", "cabinet_id");

        // Correctifs potentiels pour la table liste_attente
        fixColumn("liste_attente", "patient_id");
        fixColumn("liste_attente", "cabinet_id");
        fixColumn("liste_attente", "date_arrivee");

        // Correctif pour la contrainte de statut qui ne contient pas 'TERMINE'
        dropConstraint("rendez_vous", "rendez_vous_statut_check");

        // Correctif pour la contrainte de statut de liste_attente
        dropConstraint("liste_attente", "liste_attente_statut_check");
    }

    private void dropConstraint(String tableName, String constraintName) {
        try {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " DROP CONSTRAINT IF EXISTS " + constraintName);
            logger.info("Contrainte '" + constraintName + "' supprimée de '" + tableName + "'.");
        } catch (Exception e) {
            logger.warn("Impossible de supprimer la contrainte " + constraintName + ": " + e.getMessage());
        }
    }

    private void fixColumn(String tableName, String columnName) {
        try {
            // Tentative de suppression de la colonne obsolète
            // On utilise une requête SQL native car c'est de la maintenance de schéma
            jdbcTemplate.execute("ALTER TABLE " + tableName + " DROP COLUMN IF EXISTS " + columnName);
            logger.info("Colonne '" + columnName + "' de la table '" + tableName
                    + "' supprimée avec succès (si elle existait).");
        } catch (Exception e) {
            // Si la suppression échoue (ex: contraintes), on essaie au moins d'enlever la
            // contrainte NOT NULL
            logger.warn("Impossible de supprimer la colonne " + columnName + " de " + tableName
                    + ". Tentative de retrait de la contrainte NOT NULL.");
            try {
                jdbcTemplate.execute("ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " DROP NOT NULL");
                logger.info("Contrainte NOT NULL retirée de '" + columnName + "' dans '" + tableName + "'.");
            } catch (Exception ex) {
                // Ignorer silencieusement si la colonne n'existe pas ou autre erreur non
                // critique
                logger.debug("Info sur " + tableName + "." + columnName + ": " + ex.getMessage());
            }
        }
    }
}
