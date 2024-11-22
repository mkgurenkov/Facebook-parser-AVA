package adsPower.responseParser;

import adsPower.exceptions.APIResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseParser {
    public static Response parse(String responseBody) {
        ObjectMapper jsonParser = new ObjectMapper();
        try {
            return jsonParser.readValue(responseBody, Response.class);
        } catch (JsonProcessingException e) {
            throw new APIResponseException("Unable to recognize API response: " + e.getMessage());
        }
    }
}
