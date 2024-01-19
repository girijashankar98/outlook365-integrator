package org.integrator.office365.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.integrator.office365.document.ChannelIntegrationDetails;
import org.integrator.office365.dto.MessageInfo;
import org.integrator.office365.dto.OutlookFetchedEmailMessage;
import org.integrator.office365.enums.ChannelTypeEnum;
import org.integrator.office365.enums.MessageType;
import org.integrator.office365.repo.ChannelIntegrationDetailsRepo;
import org.integrator.office365.util.ServiceUtility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MailPollingScheduler {

    @Autowired
    StreamBridge streamBridge;
    @Autowired
    ChannelIntegrationDetailsRepo integrationDetailsRepo;
    @Autowired
    ServiceUtility serviceUtility;
    @Autowired
    ModelMapper modelMapper;
    @Scheduled(fixedRate = 10000)
    void pollingMails(){

        List<ChannelIntegrationDetails>  channelIntegrationDetails = integrationDetailsRepo.findAllByChannelNameAndIntegrated("Outlook 365",true);
        for(ChannelIntegrationDetails integrationDetails: channelIntegrationDetails){
                try {
                    integrationDetails = serviceUtility.validateAccessToken(integrationDetails);
                    List<LinkedHashMap<String, Object>> outlookEmails = serviceUtility.getOutlookEmails(integrationDetails);
                    for (LinkedHashMap<String, Object> message : outlookEmails) {

                        OutlookFetchedEmailMessage outlookEmailMessage = modelMapper.map(message,OutlookFetchedEmailMessage.class);

                        MessageInfo messageInfo = new MessageInfo();
                        messageInfo.setLastMessageId(outlookEmailMessage.getId());
                        messageInfo.setTenant(integrationDetails.getTenantId());
                        messageInfo.setTo(outlookEmailMessage.getToRecipients().stream().map(outlookRecipient -> outlookRecipient.getEmailAddress().getAddress()).collect(Collectors.toList()));
                        messageInfo.setFrom(outlookEmailMessage.getFrom().getEmailAddress().getAddress());
                        messageInfo.setSubject(outlookEmailMessage.getSubject());
                        messageInfo.setContent(outlookEmailMessage.getBody().getContent());
                        messageInfo.setReceiveTime(outlookEmailMessage.getReceivedDateTime());
                        messageInfo.setChannelTypeEnum(ChannelTypeEnum.OUTLOOK);
                        messageInfo.setMessageType(MessageType.EXTERNAL);
                        messageInfo.setConversationId(outlookEmailMessage.getConversationId());

                        log.info(messageInfo.toString());


                        streamBridge.send("NgDeskReceivedMessageProducer-in-0", messageInfo);
                    }

                    integrationDetails.setLastSyncTime(Instant.now().getEpochSecond());
                    integrationDetailsRepo.save(integrationDetails);

                } catch (ParseException parsingException) {
                    log.info("Access token Parsing failed : " + parsingException.getLocalizedMessage());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

        }
    }
}
