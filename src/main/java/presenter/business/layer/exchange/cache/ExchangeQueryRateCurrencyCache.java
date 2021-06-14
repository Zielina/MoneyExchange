package presenter.business.layer.exchange.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import presenter.business.layer.exchange.gateway.ExchangeGateway;
import presenter.business.layer.exchange.model.SearchExchangeCurrencyRequest;
import presenter.business.layer.exchange.model.ExchangeCurrencyResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ExchangeQueryRateCurrencyCache {

    public static final int DEFAULT_DURATION = 15;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    private final LoadingCache<SearchExchangeCurrencyRequest, BigDecimal> currencyCache;
    private final ExchangeGateway exchangeGateway;

    public ExchangeQueryRateCurrencyCache( int lastPeriodDuration, TimeUnit lastPeriodTimeUnit, ExchangeGateway exchangeGateway ) {
        this.exchangeGateway = exchangeGateway;
        this.currencyCache = newCache( lastPeriodDuration, lastPeriodTimeUnit );
    }

    public static ExchangeQueryRateCurrencyCache of( ExchangeGateway exchangeGateway ) {
        return new ExchangeQueryRateCurrencyCache( DEFAULT_DURATION, DEFAULT_TIME_UNIT, exchangeGateway );
    }

    public BigDecimal retrieveRate( SearchExchangeCurrencyRequest request ) throws ExecutionException {
        return currencyCache.get( request );
    }

    private LoadingCache<SearchExchangeCurrencyRequest, BigDecimal> newCache( int duration, TimeUnit unit ) {
        LoadingCache<SearchExchangeCurrencyRequest, BigDecimal> loadingCache = CacheBuilder.newBuilder()
                .expireAfterAccess( duration, unit )
                .build( loadCurrencyRate() );

        return loadingCache;
    }

    private CacheLoader<SearchExchangeCurrencyRequest, BigDecimal> loadCurrencyRate() {
        return new CacheLoader<SearchExchangeCurrencyRequest, BigDecimal>() {

            @Override
            public BigDecimal load( SearchExchangeCurrencyRequest request ) {
                return fetchCurrency( request );
            }
        };
    }

    private BigDecimal fetchCurrency( SearchExchangeCurrencyRequest request ) {
        Map<String, BigDecimal> rates = fetchCurrencyRate( request );

        return rates.get( request.getTarget() );
    }

    private Map<String, BigDecimal> fetchCurrencyRate( SearchExchangeCurrencyRequest request ) {
        Map<String, BigDecimal> result = Optional.ofNullable( exchangeGateway.fetchCurrencyBaseQueryConverter( request ) )
                .map( ExchangeCurrencyResponse::getRates )
                .orElse( new HashMap() );

        Map<String, BigDecimal> resultOutput = new HashMap<>();

        for (String key : result.keySet()) {
            resultOutput.put( key.toUpperCase(), result.get( key ) );
        }

        return resultOutput;
    }
}
