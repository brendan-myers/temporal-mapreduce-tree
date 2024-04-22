package com.brendan.temporal.workflow;

import java.time.Duration;
import java.util.Random;

import com.brendan.temporal.activity.RecordActivity;

import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

@WorkflowImpl(taskQueues = "tree-tq")
public class LeafWorkflowImpl implements LeafWorkflow {

    private RecordActivity activity = Workflow.newActivityStub(
        RecordActivity.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .build()
    );

    @Override
    public String process(NodeInput input) {
        NodeWorkflow parent = Workflow.newExternalWorkflowStub(NodeWorkflow.class,
            Workflow.getInfo().getParentWorkflowId().get());

        int offset = input.getOffset();

        while (offset < input.getOffset() + input.getLength()) {
            activity.run(offset, input.getOffset(), input.getLength());
            
            parent.putResult(1);
            
            offset++;
        }

        return "Processed " + input.getLength() + " records.";
    }
}