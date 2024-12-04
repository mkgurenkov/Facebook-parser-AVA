package sunBrowser;

import com.fasterxml.jackson.databind.ObjectMapper;
import sunBrowser.data.Account;
import sunBrowser.data.WebUrl;
import sunBrowser.exceptions.FacebookResponseConvertingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class FacebookResponseConverter {
    static List<Account> toAccounts(FacebookResponse facebookResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Account> accounts = new ArrayList<>();
            List<Object> list = (List<Object>) facebookResponse.getData();
            for (Object v : list) {
                accounts.add(objectMapper.convertValue(v, Account.class));
            }
            return accounts;
        } catch (ClassCastException | IllegalArgumentException e) {
            throw new FacebookResponseConvertingException("Unable to convert facebook response to List<Account>: " + e.getMessage());
        }
    }

    static List<WebUrl> toWebUrls(FacebookResponse facebookResponse) {
        try {
            List<WebUrl> webUrls = new ArrayList<>();
            List<Object> data = (List<Object>) facebookResponse.getData();
            for (Object element : data) {
                String adId = (String) Objects.requireNonNull(((Map<String, Object>) element).get("id"));
                Map<String, Object> creative = (Map<String, Object>) Objects.requireNonNull(((Map<String, Object>) element).get("creative"));
                String webSiteUrl = (String) find((Map<String, Object>) creative.get("object_story_spec"), "link");
                if (webSiteUrl == null) {
                    webSiteUrl = (String) find((Map<String, Object>) creative.get("asset_feed_spec"), "website_url");
                }
                WebUrl webUrl = new WebUrl();
                webUrl.setAdId(adId);
                webUrl.setValue(webSiteUrl);

                webUrls.add(webUrl);
            }
            return webUrls;
        } catch (ClassCastException | NullPointerException e) {
            throw new FacebookResponseConvertingException("Unable to convert facebook response to List<WebUrl>: " + e.getMessage());
        }
    }

    private static Object find(Map<String, Object> json, String field) {
        if (json == null) {return null;}
        if (json.get(field) != null) {
            return json.get(field);
        }

        for (Map.Entry<String, Object> entry : json.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Map) {
                Object result = find((Map<String, Object>) value, field);
                if (result != null) {
                    return result;
                }
            }

            if (value instanceof List) {
                for (Object el : (List<Object>) value) {
                    if (el instanceof Map) {
                        Object result = find((Map<String, Object>) el, field);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }

        return null;
    }
}
