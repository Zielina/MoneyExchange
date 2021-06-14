package presenter.business.layer.exchange.exception;

public class ValidationException extends RuntimeException {

    private final String result;

    public ValidationException( String result ) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
