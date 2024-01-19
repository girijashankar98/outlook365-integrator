package org.integrator.office365.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.integrator.office365.enums.ChannelTypeEnum;
import org.integrator.office365.enums.MessageType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageInfo {

    private String lastMessageId;

    private String from;

    private String conversationId;

    private String tenant;

    private List<String> to;

    private String subject;

    private String content;

    private String receiveTime;

    private MessageType messageType;

    private ChannelTypeEnum channelTypeEnum;

}
