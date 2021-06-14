package presenter.business.layer.exchange.exception;

public class HostEndpointException extends RuntimeException {

    private final String result;

    public HostEndpointException( String result ) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
