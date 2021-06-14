package presenter.business.layer.exchange.config.proportiest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionHttpClientFeature {

    private int maxIdleConnections;
    private long keepAliveDuration;
    private long connectTimeout;
}
