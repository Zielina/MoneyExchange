package presenter.business.layer.exchange.gateway;

import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest;
import presenter.business.layer.exchange.model.ExchangeCurrencyResponse;

public interface ExchangeGateway {

    ExchangeCurrencyResponse fetchCurrencyBaseEuroConverter();

    ExchangeCurrencyResponse fetchCurrencyBaseQueryConverter( SearchExchangeCurrencyRequest request );
}
