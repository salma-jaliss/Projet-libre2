package medical.cabinet.enums;

/**
 * Énumération des rôles des utilisateurs conformément au diagramme de classes
 * Rôles: MEDECIN, SECRETAIRE, ADMINISTRATEUR
 */
public enum Role {
    MEDECIN("Médecin"),
    SECRETAIRE("Secrétaire"),
    ADMINISTRATEUR("Administrateur");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}