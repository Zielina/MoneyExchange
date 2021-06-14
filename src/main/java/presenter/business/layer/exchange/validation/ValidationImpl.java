package presenter.business.layer.exchange.validation;

import org.springframework.stereotype.Component;
import presenter.business.layer.exchange.common.proporties.ExchangeAvailableCurrencyValidation;
import presenter.business.layer.exchange.exception.ValidationException;
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class ValidationImpl implements Validation {

    private static final String RESOURCE_PARAMETER_PROVIDED = "Field [%s] has to be provided.";
    private static final String RESOURCE_PARAMETER_EQUALS = "Fields [%s] and [%s] are equal. Please change source or target to other currency symbol.";
    private static final String RESOURCE_PARAMETER_BELONGS = "Field [%s] has to be equal to one of following values [%s] but [%s] value was provided.";
    private static final String RESOURCE_PARAMETER_PROVIDED_GREATER_THAN_X = "Field [%s] has to be greater than %s.";
    private static final String RESOURCE_PARAMETER_SCALE = "Field [%s] has to have %s decimal places of precision.";

    private final Set<String> availableCurrencyValidation;

    public ValidationImpl( ExchangeAvailableCurrencyValidation availableCurrencyValidation ) {
        this.availableCurrencyValidation = availableCurrencyValidation.getAvailableCurrency().keySet();
    }

    public void validation( SearchExchangeCurrencyRequest request ) {

        if ( request.getSource() == null) {
            throw new ValidationException( String.format( RESOURCE_PARAMETER_PROVIDED, "source" ) );
        }

        if ( request.getSource() != null && !availableCurrencyValidation.contains( request.getSource().toUpperCase() )  ) {
            throw new ValidationException( String.format( RESOURCE_PARAMETER_BELONGS, "source", createPossibleCode(), request.getSource().toUpperCase() ) );
        }

        if ( request.getTarget() == null) {
            throw new ValidationException( String.format( RESOURCE_PARAMETER_PROVIDED, "target" ) );
        }

        if ( request.getTarget() != null &&  !availableCurrencyValidation.contains( request.getTarget().toUpperCase() )  ) {
            throw new ValidationException( String.format( RESOURCE_PARAMETER_BELONGS, "target", createPossibleCode(), request.getTarget().toUpperCase() ) );
        }

        if ( ( request.getSource() != null && request.getTarget() != null ) && ( request.getSource().equalsIgnoreCase( request.getTarget() ) ) ) {
            throw new ValidationException( String.format( RESOURCE_PARAMETER_EQUALS, "source", "target" ) );
        }

        if ( request.getValue() == null ) {
            throw new ValidationException( String.format( RESOURCE_PARAMETER_PROVIDED, "value" ) );
        }

        if ( request.getValue() != null && request.getValue().compareTo( BigDecimal.ZERO ) < 1 ) {
            throw new ValidationException( String.format( RESOURCE_PARAMETER_PROVIDED_GREATER_THAN_X, "value", "0" ) );
        }

        if ( request.getValue() != null && request.getValue().scale() > 2 ) {
            throw new ValidationException( String.format( RESOURCE_PARAMETER_SCALE, "value" , "2" ) );
        }
    }

    private String createPossibleCode() {
        return availableCurrencyValidation.stream().collect( Collectors.joining(", "));
    }
}
