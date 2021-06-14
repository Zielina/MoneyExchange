package presenter.business.layer.exchange.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import presenter.business.layer.exchange.common.proportiest.ExternalServiceCredentials;
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest;
import presenter.business.layer.exchange.model.ExchangeCurrencyResponse;

@Component
class ExchangeGatewayImpl implements ExchangeGateway {

    private final String base = "{source}";
    private final String target = "{target}";

    private final RestTemplate okRestTemplate;
    private final ExternalServiceCredentials externalServiceCredentials;

    @Autowired
    public ExchangeGatewayImpl( @Qualifier ( "OKRestTemplate" ) RestTemplate okRestTemplate, ExternalServiceCredentials externalServiceCredentials ) {
        this.okRestTemplate = okRestTemplate;
        this.externalServiceCredentials = externalServiceCredentials;
    }

    @Override
    public ExchangeCurrencyResponse fetchCurrencyBaseEuroConverter() {

        String endpoint = externalServiceCredentials.getRateEuroEndpoint().createEndpoint();
        ExchangeCurrencyResponse response = okRestTemplate.getForObject( endpoint, ExchangeCurrencyResponse.class );
        return response;
    }

    @Override
    public ExchangeCurrencyResponse fetchCurrencyBaseQueryConverter( SearchExchangeCurrencyRequest request ) {
        String endpoint = externalServiceCredentials.getRateQueryEndpoint().createEndpoint().replace( base, request.getSource() ).replace( target, request.getTarget() );
        ExchangeCurrencyResponse response = okRestTemplate.getForObject( endpoint, ExchangeCurrencyResponse.class );
        return response;
    }
}
