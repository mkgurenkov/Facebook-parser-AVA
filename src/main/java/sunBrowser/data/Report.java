package sunBrowser.data;

import lombok.Data;

import java.util.List;

@Data
public class Report {
    private String name;
    private String[] headers;
    private List<List<String>> rows;
    public boolean isEmpty() {
        return rows.isEmpty();
    }
}
