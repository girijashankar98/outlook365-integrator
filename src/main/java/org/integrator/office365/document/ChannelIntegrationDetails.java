package org.integrator.office365.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.integrator.office365.enums.ChannelTypeEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("channel_integration_details")
public class ChannelIntegrationDetails{

    @Id
    private String id;
    @Field("tenant_id")
    private String tenantId;
    @Field("access_token")
    private String accessToken;
    @Field("refresh_token")
    private String refreshToken;
    @Field("channel_type")
    private ChannelTypeEnum channelType;
    @Field("channel_name")
    private String channelName;
    @Field("channel_id")
    private String channelId;

    @Field("integrated_id")
    private String integratedId;

    private boolean integrated;
    @Field("last_sync_time")
    private long lastSyncTime;
}
