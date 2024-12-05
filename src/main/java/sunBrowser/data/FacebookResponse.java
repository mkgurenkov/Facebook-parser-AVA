package sunBrowser.data;

import lombok.Data;

import java.util.Map;

@Data
public class FacebookResponse {
    private Object data;
    private Map<String, Object>  paging;
    private String FbTraceId;
    private String wwwRequestId;
}
