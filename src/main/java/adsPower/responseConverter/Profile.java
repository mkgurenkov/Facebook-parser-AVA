package adsPower.responseConverter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Profile {
    @JsonProperty("fbcc_proxy_acc_id")
    private long FbccProxyAccId;
    @JsonProperty("ipchecker")
    private String ipChecker;
    @JsonProperty("fakey")
    private String fakey;
    @JsonProperty("serial_number")
    private long serialNumber;
    @JsonProperty("user_id")
    private String userId;
    private String name;
    @JsonProperty("group_id")
    private long groupId;
    @JsonProperty("group_name")
    private String groupName;
    @JsonProperty("domain_name")
    private String domainName;
    private String username;
    private String remark;
    @JsonProperty("sys_app_cate_id")
    private long sysAppCateId;
    @JsonProperty("created_time")
    private long createdTime;
    private String ip;
    @JsonProperty("ip_country")
    private String ipCountry;
    private String password;
    @JsonProperty("last_open_time")
    private long lastOpenTime;
}
