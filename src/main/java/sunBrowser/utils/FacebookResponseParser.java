package sunBrowser.utils;

import sunBrowser.data.FacebookResponse;
import sunBrowser.exceptions.FacebookAPIResponseException;
import java.util.Map;
import java.util.Objects;

public class FacebookResponseParser {
    public static FacebookResponse parse(Map<String, Object> jsonFacebookResponse) {
        try {
            FacebookResponse facebookResponse = new FacebookResponse();
            facebookResponse.setData(Objects.requireNonNull(jsonFacebookResponse.get("data")));
            facebookResponse.setPaging((Map<String, Object>) jsonFacebookResponse.get("paging"));
            facebookResponse.setFbTraceId((String) Objects.requireNonNull(jsonFacebookResponse.get("__fb_trace_id__")));
            facebookResponse.setWwwRequestId((String) Objects.requireNonNull(jsonFacebookResponse.get("__www_request_id__")));
            return facebookResponse;
        } catch (NullPointerException | ClassCastException e) {
            throw new FacebookAPIResponseException("Unable to recognize API response: " + Objects.requireNonNullElse(e.getMessage(), "no system message"));
        }
    }
}
