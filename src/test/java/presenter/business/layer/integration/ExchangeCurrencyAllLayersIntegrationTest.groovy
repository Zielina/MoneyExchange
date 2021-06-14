package presenter.business.layer.integration

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.client.RestTemplate
import presenter.business.layer.exchange.ErrorHandlerControllerAdvice
import presenter.business.layer.exchange.ExchangeApplication
import presenter.business.layer.exchange.common.ExchangeAvailableCurrencyValidationConfiguration
import presenter.business.layer.exchange.common.ExchangeCurrencyCacheConfiguration
import presenter.business.layer.exchange.common.RestTemplateConfiguration
import presenter.business.layer.exchange.controller.ExchangeCurrencyController
import presenter.business.layer.exchange.gateway.ExchangeGateway
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest
import presenter.business.layer.exchange.service.ExchangeCurrencyService
import presenter.business.layer.exchange.service.ExchangeCurrencyTransfer
import presenter.business.layer.exchange.validation.Validation
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(classes = [
        ExchangeApplication,
        RestTemplateConfiguration,
        ExchangeCurrencyController,
        ExchangeAvailableCurrencyValidationConfiguration,
        ExchangeCurrencyCacheConfiguration,
        OkHttpClientFactory,
        RestTemplate,
        OkHttpClientFactory,
        ExchangeCurrencyService,
        Validation,
        ExchangeGateway,
        ExchangeCurrencyTransfer
])
class ExchangeCurrencyAllLayersIntegrationTest extends Specification {

    @Autowired
    private ExchangeCurrencyController exchangeCurrencyController

    @Autowired
    private ExchangeCurrencyService exchangeCurrencyService

    private MockMvc mockMvc

    @Autowired
    private ExchangeCurrencyTransfer exchangeCurrencyTransfer

    @Autowired
    @Qualifier("OKRestTemplate")
    private RestTemplate restTemplate

    @Autowired
    private ExchangeGateway exchangeGateway

    @Autowired
    private ErrorHandlerControllerAdvice errorHandlerControllerAdvice

    void setup() {

        mockMvc = MockMvcBuilders.standaloneSetup( exchangeCurrencyController )
                .setControllerAdvice( errorHandlerControllerAdvice )
                .build()
    }

    def 'Should convertMoney return success response'() {

        when:
            def response = mockMvc.perform( post( '/convertMoney', sendRequest() ) )
        then:
            response.andExpect( status().isOk() )
                    .andExpect( jsonPath( '$.source' ).value( 'USD' ) )
                    .andExpect( jsonPath( '$.target' ).value( 'GBP' ) )
                    .andExpect( jsonPath( '$.resultExchange' ).isNotEmpty() )
    }

    def 'Should convertMoney return validation error #statusCode'() {

        when:
            def response = mockMvc.perform( post( '/convertMoney', sendRequestNotCorrect() ) )

        then:
            response
                    .andExpect( status().is( statusCode ) )
                    .andExpect( jsonPath( '$.description' ).value( description ) )

        where:
            description                          || statusCode
            "Field [value] has to be provided."  || 422
    }

    static SearchExchangeCurrencyRequest sendRequest() {
        return SearchExchangeCurrencyRequest.builder()
                .source( "USD" )
                .target( "GBP" )
                .value( new BigDecimal( "30" ) )
                .build()
    }

    static SearchExchangeCurrencyRequest sendRequestNotCorrect() {
        return SearchExchangeCurrencyRequest.builder()
                .source( "USD" )
                .target( "GBP" )
                .value( null )
                .build()
    }

    static RequestBuilder post( String patch, Object request ) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create()
        return MockMvcRequestBuilders.post( patch, new Object[0] ).content( gson.toJson( request ) ).contentType( MediaType.APPLICATION_JSON )
    }
}
