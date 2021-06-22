package presenter.business.layer.service

import presenter.business.layer.exchange.cache.ExchangeEuroRateCurrencyCache
import presenter.business.layer.exchange.cache.ExchangeQueryRateCurrencyCache
import presenter.business.layer.exchange.common.proporties.ExchangeAvailableCurrencyValidation
import presenter.business.layer.exchange.common.proporties.ExternalServiceCredentials
import presenter.business.layer.exchange.gateway.ExchangeGateway
import presenter.business.layer.exchange.model.ExchangeCurrencyResponse
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest
import presenter.business.layer.exchange.service.ExchangeCurrencyService
import presenter.business.layer.exchange.service.ExchangeCurrencyServiceImpl
import presenter.business.layer.exchange.service.ExchangeCurrencyTransfer
import presenter.business.layer.exchange.validation.Validation
import presenter.business.layer.exchange.validation.ValidationImpl
import spock.lang.Specification

class ExchangeCurrencyServiceImplTest extends Specification {

    ExchangeGateway exchangeGateway = Mock()

    private ExternalServiceCredentials.RateEndpoint rateEuroEndpoint = Mock()
    private ExternalServiceCredentials.RateEndpoint rateQueryEndpoint = Mock()

    private ExchangeCurrencyService exchangeCurrencyService

    def setup() {

        ExchangeAvailableCurrencyValidation availableCurrencyValidation = new ExchangeAvailableCurrencyValidation()
        availableCurrencyValidation.availableCurrency = ["USD": "USA", "GBP": "gbp"]

        ExternalServiceCredentials externalServiceCredentials = new ExternalServiceCredentials( rateEuroEndpoint: rateEuroEndpoint, rateQueryEndpoint: rateQueryEndpoint )

        Validation validation = new ValidationImpl( availableCurrencyValidation )
        ExchangeCurrencyTransfer transfer = new ExchangeCurrencyTransfer()

        ExchangeCurrencyResponse expectGateway = new ExchangeCurrencyResponse(
                success: Boolean.TRUE,
                timestamp: 123,
                base: "base",
                date: "date",
                rates: ["usd": new BigDecimal( "1.22" ), "gbp": new BigDecimal( "0.822" )] )

        exchangeGateway.fetchCurrencyBaseEuroConverter() >> expectGateway

        ExchangeEuroRateCurrencyCache exchangeEuroRateCurrencyCache = ExchangeEuroRateCurrencyCache.of( exchangeGateway )
        ExchangeQueryRateCurrencyCache exchangeQueryRateCurrencyCache = ExchangeQueryRateCurrencyCache.of( exchangeGateway )

        exchangeCurrencyService = new ExchangeCurrencyServiceImpl( validation, transfer, externalServiceCredentials, exchangeEuroRateCurrencyCache, exchangeQueryRateCurrencyCache )
    }

    def 'Should convert Euro Money return success response'() {

        when:
            def response = exchangeCurrencyService.exchangeCurrency( sendRequest() )
        then:

            1 * rateEuroEndpoint.isEnable() >> true

            response.target == "GBP"
            response.source == "USD"
            response.resultExchange == new BigDecimal("20.213115")
    }

    def 'Should convert Money return success response for other endpoint'() {
        given:

            ExchangeCurrencyResponse expectGateway = new ExchangeCurrencyResponse(
                    success: Boolean.TRUE,
                    timestamp: 123,
                    base: "base",
                    date: "date",
                    rates: ["usd": new BigDecimal( "1.22" ), "gbp": new BigDecimal( "0.822" )] )

        when:
            def response = exchangeCurrencyService.exchangeCurrency( sendRequest() )
        then:

            1 * rateEuroEndpoint.isEnable() >> false
            1 * rateQueryEndpoint.isEnable() >> true
            exchangeGateway.fetchCurrencyBaseQueryConverter( _ ) >> expectGateway


            response.target == "GBP"
            response.source == "USD"
            response.resultExchange == new BigDecimal("24.660")
    }

    static SearchExchangeCurrencyRequest sendRequest() {
        return SearchExchangeCurrencyRequest.builder()
                .source( "usd" )
                .target( "gbp" )
                .value( new BigDecimal( "30" ) )
                .build()
    }
}
