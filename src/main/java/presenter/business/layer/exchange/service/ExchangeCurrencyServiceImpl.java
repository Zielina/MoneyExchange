package presenter.business.layer.exchange.service;

import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import presenter.business.layer.exchange.cache.ExchangeEuroRateCurrencyCache;
import presenter.business.layer.exchange.cache.ExchangeQueryRateCurrencyCache;
import presenter.business.layer.exchange.common.proportiest.ExternalServiceCredentials;
import presenter.business.layer.exchange.exception.ConfigurationException;
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest;
import presenter.business.layer.exchange.model.SearchExchangeCurrencyResponse;
import presenter.business.layer.exchange.validation.Validation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;

@Service
class ExchangeCurrencyServiceImpl implements ExchangeCurrencyService {

    private final Validation validation;
    private final ExchangeCurrencyTransfer transfer;
    private final ExternalServiceCredentials externalServiceCredentials;
    private final ExchangeEuroRateCurrencyCache exchangeEuroRateCurrencyCache;
    private final ExchangeQueryRateCurrencyCache exchangeQueryRateCurrencyCache;

    @Autowired
    public ExchangeCurrencyServiceImpl( Validation validation,
            ExchangeCurrencyTransfer transfer,
            ExternalServiceCredentials externalServiceCredentials,
            ExchangeEuroRateCurrencyCache exchangeEuroRateCurrencyCache,
            ExchangeQueryRateCurrencyCache exchangeQueryRateCurrencyCache ) {

        this.transfer = transfer;
        this.validation = validation;
        this.externalServiceCredentials = externalServiceCredentials;
        this.exchangeEuroRateCurrencyCache = exchangeEuroRateCurrencyCache;
        this.exchangeQueryRateCurrencyCache = exchangeQueryRateCurrencyCache;
    }

    @Override
    public SearchExchangeCurrencyResponse exchangeCurrency( SearchExchangeCurrencyRequest request ) throws Exception {

        validation.validation( request );

        if ( externalServiceCredentials.getRateEuroEndpoint().isEnable() ) {
            return getExchangeCurrencyEuroBaseResponse( request );
        }

        if ( externalServiceCredentials.getRateQueryEndpoint().isEnable() ) {
            return getExchangeCurrencyQueryBaseResponse( request );
        }

        throw new ConfigurationException( " All exchange endpoints are disable." );
    }

    private SearchExchangeCurrencyResponse getExchangeCurrencyQueryBaseResponse( SearchExchangeCurrencyRequest request ) throws ExecutionException {

        BigDecimal rate = exchangeQueryRateCurrencyCache.retrieveRate( request );
        BigDecimal result = convertOther( request, rate );
        return transfer.toResponse( request, result );
    }

    private SearchExchangeCurrencyResponse getExchangeCurrencyEuroBaseResponse( SearchExchangeCurrencyRequest request )
            throws ExecutionException {

        String source = request.getSource();
        String target = request.getTarget();

        if ( "EUR".equals( source ) ) {

            BigDecimal output = convertEuroToOther( request ).getValue();
            return transfer.toResponse( source, output, target );
        } else if ( "EUR".equals( target ) ) {

            BigDecimal output = convertOtherToEuro( request ).getValue();
            return transfer.toResponse( source, output, target );
        } else {

            Amount outputFromOtherToEuro = convertEuroToOther( request );

            Amount outputToTarget = fetchOtherToEuro( source, outputFromOtherToEuro );

            return transfer.toResponse( source, outputToTarget.getValue(), target );

        }
    }

//    private void saveCacheCalculateRate( String source, String target, Amount outputFromOtherToEuro, Amount outputToTarget ) {
//        BigDecimal rateSourceToTarget = outputFromOtherToEuro.getRate().divide( outputToTarget.getRate(), 6, RoundingMode.UP );
//        BigDecimal rateTargetToSource = outputToTarget.getRate().divide( outputFromOtherToEuro.getRate(), 6, RoundingMode.UP );
//
//        exchangeCurrencyCache.save( source + target, rateSourceToTarget );
//        exchangeCurrencyCache.save( target + source, rateTargetToSource );
//    }

//    private BigDecimal fetchRateSaveCache( String input ) throws ExecutionException {
//        return exchangeCurrencyCache.retrieveRate( input );
//    }

    private Amount fetchOtherToEuro( String source, Amount outputFromOtherToEuro ) throws ExecutionException {
        SearchExchangeCurrencyRequest innerConverter = SearchExchangeCurrencyRequest.builder().source( source ).target( "EUR" )
                .value( outputFromOtherToEuro.getValue() ).build();

        Amount outputToTarget = convertOtherToEuro( innerConverter );
        return outputToTarget;
    }

    private BigDecimal fetchEuroRate( String input ) throws ExecutionException {
        return exchangeEuroRateCurrencyCache.retrieveRate( input );
    }

    private Amount convertEuroToOther( SearchExchangeCurrencyRequest request ) throws ExecutionException {
        BigDecimal value = request.getValue();
        BigDecimal rate = fetchEuroRate( request.getTarget() );

        BigDecimal valueOutput = value.multiply( rate );

        return Amount.of( valueOutput, rate );
    }

    private Amount convertOtherToEuro( SearchExchangeCurrencyRequest request ) throws ExecutionException {
        BigDecimal value = request.getValue();
        BigDecimal rate = fetchEuroRate( request.getSource() );

        BigDecimal valueOutput = value.divide( rate, 6, RoundingMode.UP );

        return Amount.of( valueOutput, rate );
    }

    private BigDecimal convertOther( SearchExchangeCurrencyRequest request, BigDecimal rate ) {
        BigDecimal value = request.getValue();

        return value.multiply( rate );
    }

    @Builder
    @Getter
    public static class Amount {

        private BigDecimal value;
        private BigDecimal rate;

        public static Amount of( BigDecimal value, BigDecimal rate ) {
            return Amount.builder().value( value ).rate( rate ).build();
        }
    }
}
