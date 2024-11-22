package adsPower.responseConverter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Group {
    @JsonProperty("group_id")
    private long groupId;
    @JsonProperty("group_name")
    private String groupName;
    private String remark;
}
