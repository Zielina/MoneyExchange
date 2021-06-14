package presenter.business.layer.exchange.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ExchangeCurrencyCache {

    public static final int DEFAULT_DURATION = 5;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    private final LoadingCache<String, BigDecimal> currencyCache;

    public ExchangeCurrencyCache( int lastPeriodDuration, TimeUnit lastPeriodTimeUnit ) {
        this.currencyCache = newCache( lastPeriodDuration, lastPeriodTimeUnit );
    }

    public static ExchangeCurrencyCache of() {
        return new ExchangeCurrencyCache( DEFAULT_DURATION, DEFAULT_TIME_UNIT );
    }

    public BigDecimal retrieveRate( String input ) throws ExecutionException {
        return currencyCache.get( input );
    }

    private LoadingCache<String, BigDecimal> newCache( int duration, TimeUnit unit ) {
        LoadingCache<String, BigDecimal> loadingCache = CacheBuilder.newBuilder()
                .expireAfterAccess( duration, unit )
                .build( loadCurrencyRate() );

        return loadingCache;
    }

    public void save( String key, BigDecimal rate ) {

        currencyCache.put( key, rate );
    }

    private CacheLoader<String, BigDecimal> loadCurrencyRate() {
        return new CacheLoader<String, BigDecimal>() {

            @Override
            public BigDecimal load( String s ) throws Exception {
                return BigDecimal.ZERO;
            }
        };
    }
}
