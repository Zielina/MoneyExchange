package presenter.business.layer.controller

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import presenter.business.layer.exchange.ErrorHandlerControllerAdvice
import presenter.business.layer.exchange.controller.ExchangeCurrencyController
import presenter.business.layer.exchange.exception.ValidationException
import presenter.business.layer.exchange.model.SearchExchangeCurrencyResponse
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest
import presenter.business.layer.exchange.service.ExchangeCurrencyService
import spock.lang.Specification
import spock.lang.Unroll
import com.google.gson.Gson;
import com.google.gson.GsonBuilder

import java.util.concurrent.ExecutionException

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ExchangeCurrencyControllerTest extends Specification {

    private ExchangeCurrencyService exchangeCurrencyService = Mock()
    private ExchangeCurrencyController exchangeCurrencyController
    private MockMvc mockMvc

    void setup() {

        exchangeCurrencyController = new ExchangeCurrencyController( exchangeCurrencyService )
        ErrorHandlerControllerAdvice errorHandlerControllerAdvice = new ErrorHandlerControllerAdvice()

        mockMvc = MockMvcBuilders.standaloneSetup( exchangeCurrencyController )
                .setControllerAdvice( errorHandlerControllerAdvice )
                .build()
    }

    def 'Should convertMoney return success response'() {

        given:
            exchangeCurrencyService.exchangeCurrency( _ ) >> expectResponse()
        when:
            String mockMvcResponse = mockMvc.perform( post( '/convertMoney', sendRequest() ) )
            .andDo( print() )
            .andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString()

            SearchExchangeCurrencyResponse response = fromJson( mockMvcResponse )

        then:
            response != null
    }

    @Unroll
    def 'Should return status #statusCode and #description'() {

        given:
            exchangeCurrencyService.exchangeCurrency( { SearchExchangeCurrencyRequest s -> s.source == "USD"; s.target == 'GBP'; s.value == null } ) >> {
                throw new ValidationException( "Field [value] has to be provided." )
            }
            exchangeCurrencyService.exchangeCurrency( { SearchExchangeCurrencyRequest s -> s.source == "USD"; s.target == 'GBP'; s.value == new BigDecimal( "30" ) } ) >> { throw new ExecutionException( "Service is currently unavailable, please try again later." ) }

        when:
            def response = mockMvc.perform( post( '/convertMoney', request ) )

        then:
            response
                    .andExpect( status().is( statusCode ) )
                    .andExpect( jsonPath( '$.description' ).value( description ) )

        where:
            request                 || description                                                            || statusCode
            sendRequest()           || "Service is currently unavailable, please try again later."            || 500
            sendRequestNotCorrect() || "Field [value] has to be provided."                                    || 422
    }

    static SearchExchangeCurrencyResponse expectResponse() {
        return SearchExchangeCurrencyResponse.builder()
                .source( "USD" )
                .target( "GBP" )
                .resultExchange( new BigDecimal( "42.410370" ) )
                .build()
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

    def post( String patch, SearchExchangeCurrencyRequest request ) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create()

        def requestBuilders = MockMvcRequestBuilders.post( patch )
                .content( gson.toJson( request ) )
                .contentType( MediaType.APPLICATION_JSON )
                .accept(MediaType.APPLICATION_JSON)

        return requestBuilders
    }

    def fromJson(String json ) {
        Gson gson =new GsonBuilder().setPrettyPrinting().create()
        return gson.fromJson(json, SearchExchangeCurrencyResponse.class)
    }
}
