# 🔧 Correction de la base de données - INSTRUCTIONS

## ⚠️ Problème

La base de données contient encore l'ancienne colonne `medecin_id` au lieu de `id_utilisateur`.

## ✅ Solution (UNE SEULE FOIS)

### Étape 1: Exécuter le script SQL

1. **Ouvrez pgAdmin, DBeaver ou psql**
2. **Connectez-vous à PostgreSQL**
3. **Sélectionnez la base de données** `appointment-db`
4. **Exécutez le fichier** `CORRECTION_DATABASE.sql`

OU copiez-collez directement ces 3 commandes :

```sql
ALTER TABLE rendez_vous RENAME COLUMN medecin_id TO id_utilisateur;
ALTER TABLE rendez_vous ADD COLUMN IF NOT EXISTS id_cabinet BIGINT;
DROP INDEX IF EXISTS idx_rendez_vous_medecin;
CREATE INDEX IF NOT EXISTS idx_rendez_vous_utilisateur ON rendez_vous(id_utilisateur);
```

### Étape 2: Vérifier

Après exécution, vérifiez avec :

```sql
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'rendez_vous'
ORDER BY ordinal_position;
```

Vous devriez voir :
- ✅ `id_utilisateur` (BIGINT, NOT NULL)
- ✅ `id_cabinet` (BIGINT)
- ❌ `medecin_id` (ne doit plus exister)

### Étape 3: Redémarrer l'application

1. Arrêtez l'application
2. Redémarrez l'application
3. Testez à nouveau POST `/api/rendez-vous`

## ✅ Après cette correction

L'erreur sera définitivement résolue. Hibernate gérera automatiquement le schéma à partir de maintenant.




