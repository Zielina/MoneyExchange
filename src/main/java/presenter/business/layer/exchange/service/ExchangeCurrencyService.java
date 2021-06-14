package presenter.business.layer.exchange.service;

import presenter.business.layer.exchange.model.SearchExchangeCurrencyResponse;
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest;

public interface ExchangeCurrencyService {

    SearchExchangeCurrencyResponse exchangeCurrency( SearchExchangeCurrencyRequest request ) throws Exception;
}
