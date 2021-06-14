package presenter.business.layer.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory
import org.springframework.web.client.RestTemplate
import presenter.business.layer.exchange.ExchangeApplication
import presenter.business.layer.exchange.cache.ExchangeEuroRateCurrencyCache
import presenter.business.layer.exchange.cache.ExchangeQueryRateCurrencyCache
import presenter.business.layer.exchange.common.ExchangeAvailableCurrencyValidationConfiguration
import presenter.business.layer.exchange.common.ExchangeCurrencyCacheConfiguration
import presenter.business.layer.exchange.common.RestTemplateConfiguration
import presenter.business.layer.exchange.gateway.ExchangeGateway
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest
import presenter.business.layer.exchange.service.ExchangeCurrencyService
import spock.lang.Specification
import spock.lang.Title


@SpringBootTest(classes = [
        ExchangeApplication,
        ExchangeAvailableCurrencyValidationConfiguration,
        ExchangeCurrencyCacheConfiguration,
        RestTemplateConfiguration,
        OkHttpClientFactory,
        ExchangeGateway,
        RestTemplate,
        OkHttpClientFactory
])
@Title('Integration tests convert money')
class ExchangeCurrencyIntegrationTest extends Specification {


    @Autowired
    @Qualifier("OKRestTemplate")
    private RestTemplate restTemplate

    @Autowired
    private ExchangeGateway gateway

    @Autowired
    private ExchangeCurrencyService service

    @Autowired
    private ExchangeEuroRateCurrencyCache euroRateCurrencyCache

    @Autowired
    private ExchangeQueryRateCurrencyCache queryRateCurrencyCache

    def 'should return result'() {
        given:
            SearchExchangeCurrencyRequest request = SearchExchangeCurrencyRequest.builder()
                    .source( "USD" )
                    .target( "GBP" )
                    .value( new BigDecimal( "30" ) )
                    .build()
        when:
            def response = service.exchangeCurrency( request )

        then:
            response != null
            response != BigDecimal.ZERO
    }
}