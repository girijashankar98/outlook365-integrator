package org.integrator.office365.controller;

import com.azure.core.annotation.QueryParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OutlookHandlerController {
    @GetMapping("/login/oauth2/code/")
    void tokenEncpoint(@QueryParam("code") String code, @QueryParam("state") String state, @QueryParam("session_state") String ss ){
        System.out.println("Code           :"+code);
        System.out.println("State          :"+state);
        System.out.println("Session State  :"+ss);
    }
}
