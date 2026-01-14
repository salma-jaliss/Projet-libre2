package medical.cabinet.exception;

class UtilisateurNotFoundException extends RuntimeException {
    public UtilisateurNotFoundException(String message) {
        super(message);
    }

    public UtilisateurNotFoundException(Long id) {
        super("Utilisateur avec l'ID " + id + " introuvable");
    }
}