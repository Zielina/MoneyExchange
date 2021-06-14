package presenter.business.layer.exchange.validation;

import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest;

public interface Validation {
    void validation( SearchExchangeCurrencyRequest request );
}
