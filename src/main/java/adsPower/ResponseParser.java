package adsPower;

import adsPower.exceptions.APIResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

class ResponseParser {
    static Response parse(String responseBody) {
        ObjectMapper jsonParser = new ObjectMapper();
        try {
            return jsonParser.readValue(responseBody, Response.class);
        } catch (JsonProcessingException e) {
            throw new APIResponseException("Unable to recognize API response: " + Objects.requireNonNullElse(e.getMessage(), "no system message"));
        }
    }
}
