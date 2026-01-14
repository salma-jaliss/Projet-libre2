package medical.cabinet.exception;

class LoginAlreadyExistsException extends RuntimeException {
    public LoginAlreadyExistsException(String login) {
        super("Le login '" + login + "' existe déjà");
    }
}