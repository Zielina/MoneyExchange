package presenter.business.layer.validation

import presenter.business.layer.exchange.common.proportiest.ExchangeAvailableCurrencyValidation
import presenter.business.layer.exchange.exception.ValidationException
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest
import presenter.business.layer.exchange.validation.Validation
import presenter.business.layer.exchange.validation.ValidationImpl
import spock.lang.Specification

class ValidationTest extends Specification {

    Validation validator

    def "setup"() {
        ExchangeAvailableCurrencyValidation availableCurrencyValidation = new ExchangeAvailableCurrencyValidation()
        availableCurrencyValidation.availableCurrency = ["USD": "USA", "GBP": "gbp"]

        validator = new ValidationImpl( availableCurrencyValidation )
    }

    def "should validation mandatory field of GetDebitCreditListRequest fail"() {
        when:
            validator.validation( request )
        then:
            ValidationException e = thrown()
            e.result == "Field [source] has to be provided."
        when:
            request = new SearchExchangeCurrencyRequest( source: "Euro" )
            validator.validation( request )
        then:
            ValidationException e2 = thrown()
            e2.result == "Field [source] has to be equal to one of following values [USD, GBP] but [EURO] value was provided."
        when:
            request = new SearchExchangeCurrencyRequest( source: "USD" )
            validator.validation( request )
        then:
            ValidationException e3 = thrown()
            e3.result == "Field [target] has to be provided."
        when:
            request = new SearchExchangeCurrencyRequest( source: "USD", target: "euro" )
            validator.validation( request )
        then:
            ValidationException e4 = thrown()
            e4.result == "Field [target] has to be equal to one of following values [USD, GBP] but [EURO] value was provided."
        when:
            request = new SearchExchangeCurrencyRequest( source: "USD", target: "USD" )
            validator.validation( request )
        then:
            ValidationException e5 = thrown()
            e5.result == "Fields [source] and [target] are equal. Please change source or target to other currency symbol."
        when:
            request = new SearchExchangeCurrencyRequest(  source: "USD", target: "GBP" )
            validator.validation( request )
        then:
            ValidationException e6 = thrown()
            e6.result == "Field [value] has to be provided."
        when:
            request = new SearchExchangeCurrencyRequest( source: "USD", target: "GBP", value: BigDecimal.ZERO )
            validator.validation( request )
        then:
            ValidationException e7 = thrown()
            e7.result == "Field [value] has to be greater than 0."
        when:
            request = new SearchExchangeCurrencyRequest( source: "USD", target: "GBP", value: new BigDecimal( "1.222") )
            validator.validation( request )
        then:
            ValidationException e8 = thrown()
            e8.result == "Field [value] has to have 2 decimal places of precision."
        where:
            request << new SearchExchangeCurrencyRequest()
    }
}