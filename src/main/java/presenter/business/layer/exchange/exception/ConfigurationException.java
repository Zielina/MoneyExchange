package presenter.business.layer.exchange.exception;

public class ConfigurationException extends RuntimeException {

    private final String result;

    public ConfigurationException( String result ) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
