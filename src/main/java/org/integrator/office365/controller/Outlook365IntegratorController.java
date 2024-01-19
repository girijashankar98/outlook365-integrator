package org.integrator.office365.controller;

import org.integrator.office365.service.IntegratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/v1/integrate")
public class Outlook365IntegratorController {

    @Autowired
    IntegratorService integratorService;

    @GetMapping("/{integrationId}")
    RedirectView loginEndpoint(@PathVariable String integrationId , @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client){
        return new RedirectView(integratorService.saveChannelIntegrationDetail(integrationId,client));
    }

}
