package presenter.business.layer.gateway

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import presenter.business.layer.exchange.common.proportiest.ExternalServiceCredentials
import presenter.business.layer.exchange.gateway.ExchangeGateway
import presenter.business.layer.exchange.gateway.ExchangeGatewayImpl
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest
import spock.lang.Specification

class ExchangeGatewayImplTest extends Specification {

    private ExchangeGateway exchangeGateway
    private ExternalServiceCredentials.RateEndpoint rateEuroEndpoint
    private ExternalServiceCredentials.RateEndpoint rateQueryEndpoint
    private ExternalServiceCredentials serviceCredentials

    private RestTemplate okRestTemplate = Mock()

    def setup() {

        rateEuroEndpoint = new ExternalServiceCredentials.RateEndpoint(  )
        rateEuroEndpoint.setAccessKey( "accessKey" )
        rateEuroEndpoint.setEnable( true )
        rateEuroEndpoint.setHost( "host?access_key={access_key}" )

        rateQueryEndpoint = new ExternalServiceCredentials.RateEndpoint(  )
        rateQueryEndpoint.setAccessKey( "accessKey" )
        rateQueryEndpoint.setEnable( true )
        rateQueryEndpoint.setHost( "host?access_key={access_key}" )

        serviceCredentials = new ExternalServiceCredentials( rateEuroEndpoint: rateEuroEndpoint, rateQueryEndpoint: rateQueryEndpoint )

        exchangeGateway = new ExchangeGatewayImpl( okRestTemplate, serviceCredentials )
    }

    def 'Should fetchCurrencyBaseEuroConverter return exception status 404'() {
        given:
            okRestTemplate.getForObject( _, _ ) >> { throw new HttpClientErrorException( HttpStatus.NOT_FOUND ) }

        when:
            exchangeGateway.fetchCurrencyBaseEuroConverter()
        then:
            def e = thrown( HttpClientErrorException.class )
            e.getStatusCode().value() == statusCode
            e.statusText.contains( responseMessage )

        where:
            responseMessage | statusCode
            "NOT_FOUND"     | 404
    }

    def 'Should fetchCurrencyBaseQueryConverter return exception status 404'() {

        given:

            okRestTemplate.getForObject( _, _ ) >> { throw new HttpClientErrorException( HttpStatus.NOT_FOUND ) }

        when:
            exchangeGateway.fetchCurrencyBaseQueryConverter( sendRequest() )
        then:
            def e = thrown( HttpClientErrorException.class )
            e.getStatusCode().value() == statusCode
            e.statusText.contains( responseMessage )

        where:
            responseMessage | statusCode
            "NOT_FOUND"     | 404
    }

    def 'Should fetchCurrencyBaseEuroConverter return exception status 500'() {

        given:

            okRestTemplate.getForObject( _, _ ) >> { throw new HttpServerErrorException( HttpStatus.INTERNAL_SERVER_ERROR ) }

        when:
            exchangeGateway.fetchCurrencyBaseEuroConverter()
        then:
            def e = thrown( HttpServerErrorException.class )
            e.getStatusCode().value() == statusCode
            e.statusText.contains( responseMsg )

        where:
            responseMsg             | statusCode
            "INTERNAL_SERVER_ERROR" | 500
    }

    def 'Should fetchCurrencyBaseQueryConverter return exception status 500'() {
        given:

            okRestTemplate.getForObject( _, _ ) >> { throw new HttpServerErrorException( HttpStatus.INTERNAL_SERVER_ERROR ) }

        when:
            exchangeGateway.fetchCurrencyBaseQueryConverter( sendRequest() )
        then:
            def e = thrown( HttpServerErrorException.class )
            e.getStatusCode().value() == statusCode
            e.statusText.contains( responseMsg )

        where:
            responseMsg             | statusCode
            "INTERNAL_SERVER_ERROR" | 500
    }

    static SearchExchangeCurrencyRequest sendRequest() {
        return SearchExchangeCurrencyRequest.builder()
                .source( "USD" )
                .target( "GBP" )
                .value( new BigDecimal( "30" ) )
                .build()
    }
}
