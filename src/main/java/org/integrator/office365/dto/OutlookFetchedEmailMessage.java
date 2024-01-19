package org.integrator.office365.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.integrator.office365.enums.ChannelTypeEnum;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutlookFetchedEmailMessage {

    private String id;

    private OutlookRecipient from;

    private List<OutlookRecipient> toRecipients;

    private String subject;

    private String conversationId;

    private Body body;

    private String receivedDateTime;

    private ChannelTypeEnum channelTypeEnum;
}
