package org.integrator.office365.service;

import org.integrator.office365.document.ChannelIntegrationDetails;
import org.integrator.office365.repo.ChannelIntegrationDetailsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IntegratorServiceImpl implements IntegratorService {

    @Autowired
    ChannelIntegrationDetailsRepo integrationDetailsRepo;

    @Value("${redirect-url.success}")
    private String redirectSuccess;

    @Value("${redirect-url.error}")
    private String redirectError;

    @Override
    public String saveChannelIntegrationDetail(String integrationId, OAuth2AuthorizedClient client) {
        Optional<ChannelIntegrationDetails> integrationDetails = integrationDetailsRepo.findById(integrationId);
        Optional<ChannelIntegrationDetails> details = integrationDetailsRepo.findByIntegratedId(client.getPrincipalName());
        if(details.isPresent()){
            return redirectError.replace("tenant",integrationDetails.get().getTenantId());
        }else {
            if (integrationDetails.isPresent()) {
                integrationDetails.get().setIntegratedId(client.getPrincipalName());
                integrationDetails.get().setAccessToken(client.getAccessToken().getTokenValue());
                integrationDetails.get().setRefreshToken(client.getRefreshToken().getTokenValue());
                integrationDetails.get().setIntegrated(true);
                integrationDetailsRepo.save(integrationDetails.get());
                return redirectSuccess.replace("tenant", integrationDetails.get().getTenantId());
            }
        }
        return redirectError.replace("tenant",integrationDetails.get().getTenantId());
    }
}
