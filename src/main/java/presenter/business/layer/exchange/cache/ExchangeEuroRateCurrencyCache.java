package presenter.business.layer.exchange.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import presenter.business.layer.exchange.gateway.ExchangeGateway;
import presenter.business.layer.exchange.model.ExchangeCurrencyResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExchangeEuroRateCurrencyCache {

    public static final int DEFAULT_DURATION = 15;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    private final LoadingCache<String, BigDecimal> currencyCache;
    private final ExchangeGateway exchangeGateway;

    public ExchangeEuroRateCurrencyCache( int lastPeriodDuration, TimeUnit lastPeriodTimeUnit, ExchangeGateway exchangeGateway ) {
        this.exchangeGateway = exchangeGateway;
        this.currencyCache = newCache( lastPeriodDuration, lastPeriodTimeUnit );
    }

    public static ExchangeEuroRateCurrencyCache of( ExchangeGateway exchangeGateway ) {
        return new ExchangeEuroRateCurrencyCache( DEFAULT_DURATION, DEFAULT_TIME_UNIT, exchangeGateway );
    }

    public BigDecimal retrieveRate( String input ) throws ExecutionException {
            return currencyCache.get( input );
    }


    private LoadingCache<String, BigDecimal> newCache( int duration, TimeUnit unit ) {
        LoadingCache<String, BigDecimal> loadingCache = CacheBuilder.newBuilder()
                .expireAfterAccess( duration, unit )
                .build( loadCurrencyRate() );
        //TODO CHECK IF NECESSARY
        loadingCache.putAll( fetchCurrencyRate() );

        return loadingCache;
    }

    private CacheLoader<String, BigDecimal> loadCurrencyRate() {
        return new CacheLoader<String, BigDecimal>() {

            @Override
            public BigDecimal load( String s ) throws Exception {
                return fetchCurrency( s );
            }
        };
    }

    private BigDecimal fetchCurrency( String currency ) {
        Map<String, BigDecimal> rates = fetchCurrencyRate();
        if ( this.currencyCache != null ) {
            this.currencyCache.putAll( rates );
        }
        return rates.get( currency );
    }

    private Map<String, BigDecimal> fetchCurrencyRate() {
        Map<String, BigDecimal> result = Optional.ofNullable( exchangeGateway.fetchCurrencyBaseEuroConverter() ).map( ExchangeCurrencyResponse::getRates ).orElse( new HashMap<>() );

        Map<String, BigDecimal> resultOutput = new HashMap<>();

        for (String key : result.keySet()) {
            resultOutput.put( key.toUpperCase(), result.get( key ) );
        }

        return resultOutput;
    }
}
