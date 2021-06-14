package presenter.business.layer.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchExchangeCurrencyRequest {
    private String source;
    private String target;
    private BigDecimal value;

    public String getSource() {
        return source != null ? source.toUpperCase() : source;
    }

    public String getTarget() {
        return target != null ? target.toUpperCase() : target;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals( java.lang.Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        SearchExchangeCurrencyRequest searchExchangeCurrencyRequest = (SearchExchangeCurrencyRequest) o;
        return Objects.equals( this.source, searchExchangeCurrencyRequest.source ) &&
                Objects.equals( this.target, searchExchangeCurrencyRequest.target ) &&
                Objects.equals( this.value, searchExchangeCurrencyRequest.value );
    }

    @Override
    public int hashCode() {
        return Objects.hash( source, target, value );
    }
}
