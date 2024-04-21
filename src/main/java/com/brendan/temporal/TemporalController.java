package com.brendan.temporal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.temporal.client.WorkflowClient;

@Controller
public class TemporalController {
    @Autowired 
    @SuppressWarnings("unused")
    private WorkflowClient client;
}
