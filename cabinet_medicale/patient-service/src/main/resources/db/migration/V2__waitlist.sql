CREATE TABLE IF NOT EXISTS liste_attente (
    id_attente BIGSERIAL PRIMARY KEY,
    position INT,
    heure_arrivee TIME,
    date DATE,
    etat VARCHAR(20),
    id_patient BIGINT,
    id_cabinet BIGINT
);
