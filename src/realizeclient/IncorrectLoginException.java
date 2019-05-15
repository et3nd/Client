package realizeclient;

class IncorrectLoginException extends Exception {
    IncorrectLoginException(String message) {
        super(message);
    }
}