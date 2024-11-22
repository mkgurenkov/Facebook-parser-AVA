package adsPower.responseParser;


import lombok.Data;
import java.util.Map;

@Data
public class Response {
    private int code;
    private Map<String, Object> data;
    private String msg;

}
