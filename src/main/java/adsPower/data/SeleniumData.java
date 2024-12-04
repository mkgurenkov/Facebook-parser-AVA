package adsPower.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeleniumData {
    @JsonProperty("debug_port")
    private int debugPort;
    @JsonProperty("webdriver")
    private String pathToWebDriver;
}
