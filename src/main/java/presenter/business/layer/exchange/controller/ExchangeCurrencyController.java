package presenter.business.layer.exchange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import presenter.business.layer.exchange.model.SearchExchangeCurrencyResponse;
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest;
import presenter.business.layer.exchange.service.ExchangeCurrencyService;

@RestController
public class ExchangeCurrencyController {

    private final ExchangeCurrencyService exchangeCurrencyService;

    @Autowired
    public ExchangeCurrencyController( ExchangeCurrencyService exchangeCurrencyService ) {
        this.exchangeCurrencyService = exchangeCurrencyService;
    }

    @PostMapping ( value = "/convertMoney" )
    public SearchExchangeCurrencyResponse convertMoney( @RequestBody SearchExchangeCurrencyRequest request ) throws Exception {
        SearchExchangeCurrencyResponse response = exchangeCurrencyService.exchangeCurrency( request );
        return response;
    }
}
