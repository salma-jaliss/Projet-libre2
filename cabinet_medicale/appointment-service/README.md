# Appointment Service

Microservice de gestion des rendez-vous et de la liste d'attente pour le système de gestion de cabinet médical.

## Configuration

- **Port**: 8084 (selon diagramme de composants)
- **Base de données**: PostgreSQL (`appointment-db`)
- **Gestion du schéma**: Hibernate avec `ddl-auto=update` (création automatique des tables)

## Entités

- **RendezVous**: Gestion des rendez-vous médicaux
- **ListeAttente**: Gestion de la file d'attente des patients

## API Endpoints

### Rendez-vous
- `POST /api/rendez-vous` - Créer un rendez-vous
- `GET /api/rendez-vous/{id}` - Obtenir un rendez-vous
- `GET /api/rendez-vous` - Liste tous les rendez-vous
- `PUT /api/rendez-vous/{id}` - Modifier un rendez-vous
- `PATCH /api/rendez-vous/{id}/annuler` - Annuler un rendez-vous
- `PATCH /api/rendez-vous/{id}/confirmer` - Confirmer un rendez-vous
- `GET /api/rendez-vous/du-jour` - Rendez-vous du jour
- `GET /api/rendez-vous/disponibilite` - Vérifier disponibilité

### Liste d'attente
- `POST /api/liste-attente` - Ajouter un patient
- `DELETE /api/liste-attente/{id}` - Retirer un patient
- `PATCH /api/liste-attente/{id}/envoyer/{medecinId}` - Envoyer au médecin
- `GET /api/liste-attente/suivant` - Obtenir le prochain patient
- `GET /api/liste-attente` - Liste tous les patients en attente

## Base de données

Les tables sont créées automatiquement par Hibernate au démarrage de l'application selon les entités JPA définies.

## Note importante

Si vous avez des erreurs liées au schéma de base de données, exécutez cette commande SQL pour corriger :

```sql
-- Renommer medecin_id en id_utilisateur si nécessaire
ALTER TABLE rendez_vous RENAME COLUMN medecin_id TO id_utilisateur;

-- Ajouter id_cabinet si manquant
ALTER TABLE rendez_vous ADD COLUMN IF NOT EXISTS id_cabinet BIGINT;
```

