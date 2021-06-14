package presenter.business.layer.exchange.service;

import org.springframework.stereotype.Component;
import presenter.business.layer.exchange.model.SearchExchangeCurrencyResponse;
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest;

import java.math.BigDecimal;

@Component
public class ExchangeCurrencyTransfer {

    public SearchExchangeCurrencyResponse toResponse( String source, BigDecimal result, String target ){
        return SearchExchangeCurrencyResponse.builder()
                .source( source )
                .resultExchange( result )
                .target( target )
                .build();
    }
    public SearchExchangeCurrencyResponse toResponse( SearchExchangeCurrencyRequest request, BigDecimal result ){
        return SearchExchangeCurrencyResponse.builder()
                .source( request.getSource() )
                .resultExchange( result )
                .target( request.getTarget() )
                .build();
    }

}
