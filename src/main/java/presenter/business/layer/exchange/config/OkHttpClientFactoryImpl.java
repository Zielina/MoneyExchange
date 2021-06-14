package presenter.business.layer.exchange.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;
import org.springframework.stereotype.Component;
import presenter.business.layer.exchange.config.proportiest.ConnectionHttpClientFeature;

import java.util.concurrent.TimeUnit;

@Component
public class OkHttpClientFactoryImpl implements OkHttpClientFactory {

    private final ConnectionPool connectionPool;
    private final long timeout;

    @Autowired
    public OkHttpClientFactoryImpl( ConnectionPool connectionPool, ConnectionHttpClientFeature connectionHttpClientFeature ) {
        this.connectionPool = connectionPool;
        this.timeout = connectionHttpClientFeature.getConnectTimeout();
    }

    @Override
    public OkHttpClient.Builder createBuilder( boolean disableSslValidation ) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectionPool( connectionPool );
        builder.connectTimeout( timeout, TimeUnit.SECONDS );
        builder.retryOnConnectionFailure( false );
        return builder;
    }
}
