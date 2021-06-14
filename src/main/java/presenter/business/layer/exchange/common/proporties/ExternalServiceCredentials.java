package presenter.business.layer.exchange.common.proporties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presenter.business.layer.exchange.exception.HostEndpointException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ExternalServiceCredentials {

    private RateEndpoint rateEuroEndpoint = new RateEndpoint();
    private RateEndpoint rateQueryEndpoint = new RateEndpoint();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static public class RateEndpoint {

        private final Logger log = LoggerFactory.getLogger(RateEndpoint.class);

        private boolean enable;
        private String host;
        private String accessKey;

        public String createEndpoint() {
            try {

                if ( !this.host.contains( "{access_key}" ) ) {
                    throw new HostEndpointException( "Problem with address" );
                }

                return this.host.replace( "{access_key}", this.accessKey );
            } catch ( Exception e ) {
                log.error( "Problem with address" , e );
                throw new HostEndpointException( "Problem with address" );
            }
        }
    }
}
