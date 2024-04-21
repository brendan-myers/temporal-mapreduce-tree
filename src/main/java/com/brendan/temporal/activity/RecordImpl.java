package com.brendan.temporal.activity;

import java.time.Duration;
import java.util.Random;

import org.springframework.stereotype.Component;

import io.temporal.activity.Activity;
import io.temporal.spring.boot.ActivityImpl;

@Component
@ActivityImpl(taskQueues = "tree-tq")
public class RecordImpl implements RecordActivity {

    @Override
    public void run(Integer recordId, Integer offset, Integer length) {
        int random = (new Random()).nextInt(3000);
        try {
            Thread.sleep(random);
        } catch (Exception e) {
            throw Activity.wrap(e);
        }

        System.out.println(
            "[" + Activity.getExecutionContext().getInfo().getWorkflowId() + "] " + 
            offset + ".." + (offset + length) + 
            " : " + recordId);
    }
    
}
