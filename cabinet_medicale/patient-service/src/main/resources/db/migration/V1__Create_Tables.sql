CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY,
    cin VARCHAR(255) UNIQUE NOT NULL,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    date_naissance DATE,
    sexe VARCHAR(50),
    adresse TEXT,
    email TEXT,
    profession VARCHAR(100),
    num_tel VARCHAR(255),
    type_mutuelle VARCHAR(50),
    groupe_sanguin VARCHAR(5),
    id_cabinet BIGINT
);

CREATE TABLE IF NOT EXISTS dossiers_medicaux (
    id_dossier BIGSERIAL PRIMARY KEY,
    ant_medicaux TEXT,
    ant_chirug TEXT,
    allergies TEXT,
    traitement TEXT,
    habitudes TEXT,
    groupe_sanguin VARCHAR(5),
    date_creation TIMESTAMP,
    date_modification TIMESTAMP,
    patient_id BIGINT UNIQUE REFERENCES patients(id)
);

CREATE TABLE IF NOT EXISTS documents_medicaux (
    id_document BIGSERIAL PRIMARY KEY,
    nom_document VARCHAR(255),
    type_document VARCHAR(100),
    chemin_fichier VARCHAR(500),
    taille_fichier BIGINT,
    date_upload TIMESTAMP,
    description TEXT,
    dossier_id BIGINT REFERENCES dossiers_medicaux(id_dossier)
);
