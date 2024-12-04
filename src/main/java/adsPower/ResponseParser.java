package adsPower;

import adsPower.exceptions.APIResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class ResponseParser {
    static Response parse(String responseBody) {
        ObjectMapper jsonParser = new ObjectMapper();
        try {
            return jsonParser.readValue(responseBody, Response.class);
        } catch (JsonProcessingException e) {
            throw new APIResponseException("Unable to recognize API response: " + e.getMessage());
        }
    }
}
