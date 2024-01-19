package org.integrator.office365.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.graph.models.BodyType;
import org.integrator.office365.document.ChannelIntegrationDetails;
import org.integrator.office365.dto.*;
import org.integrator.office365.repo.ChannelIntegrationDetailsRepo;
import org.integrator.office365.util.ServiceUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Optional;

@Service
public class MessageSenderService {

    @Autowired
    ServiceUtility serviceUtility;



    @Autowired
    ChannelIntegrationDetailsRepo integrationDetailsRepo;

    public void sendMessage(String msg) throws JsonProcessingException, ParseException, URISyntaxException {

        ObjectMapper objectMapper = new ObjectMapper();
        MessageInfo messageInfo = objectMapper.readValue(msg, MessageInfo.class);

        OutlookSendEmailMessage outlookSendEmailMessage = new OutlookSendEmailMessage();

        outlookSendEmailMessage.setBody(new Body(messageInfo.getContent(), BodyType.HTML.name()));
        outlookSendEmailMessage.setToRecipients(Collections.singletonList(new OutlookRecipient(new EmailAddress("",messageInfo.getTo().get(0)))));
        outlookSendEmailMessage.setSubject(messageInfo.getSubject());
        Optional<ChannelIntegrationDetails> channelIntegrationDetails = integrationDetailsRepo.findByTenantId(messageInfo.getTenant());

        if(channelIntegrationDetails.isPresent()){
            ChannelIntegrationDetails integrationDetails = serviceUtility.validateAccessToken(channelIntegrationDetails.get());

            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.set("message",objectMapper.valueToTree(outlookSendEmailMessage));

            serviceUtility.postOutlookEmails(integrationDetails.getAccessToken(),messageInfo.getLastMessageId(),requestNode.toPrettyString());
        }
    }
}
