-- Align patients table with JPA mappings
ALTER TABLE patients
    ALTER COLUMN adresse TYPE VARCHAR(255) USING adresse::varchar(255);

ALTER TABLE patients
    ALTER COLUMN email TYPE VARCHAR(255) USING email::varchar(255);

-- Align dossiers_medicaux table with JPA mappings
ALTER TABLE dossiers_medicaux
    RENAME COLUMN ant_chirug TO ant_chirurgicaux;

ALTER TABLE dossiers_medicaux
    ALTER COLUMN date_creation TYPE TIMESTAMP USING date_creation::timestamp;

ALTER TABLE dossiers_medicaux
    ADD COLUMN IF NOT EXISTS risque TEXT;
