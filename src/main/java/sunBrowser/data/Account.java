package sunBrowser.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
    @JsonProperty("account_id")
    private String accountId;
    @JsonProperty("timezone_name")
    private String timezoneName;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("account_status")
    private long accountStatus;
    private Long threshold;
}
