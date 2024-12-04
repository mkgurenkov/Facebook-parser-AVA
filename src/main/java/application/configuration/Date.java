package application.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Date {
    public String mode;
    @JsonProperty("date_preset")
    public String datePreset;
    @JsonProperty("time_range")
    public TimeRange timeRange;

    public String getValue() {
        if (mode.equals("date_preset")) {
            return datePreset;
        } else {
            return "{since:'" + timeRange.since + "',until:'" + timeRange.until + "'}";
        }
    }
}
