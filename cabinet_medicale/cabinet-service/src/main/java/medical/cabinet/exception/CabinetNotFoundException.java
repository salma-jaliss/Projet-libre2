package medical.cabinet.exception;

public class CabinetNotFoundException extends RuntimeException {
    public CabinetNotFoundException(String message) {
        super(message);
    }

    public CabinetNotFoundException(Long id) {
        super("Cabinet avec l'ID " + id + " introuvable");
    }
}
