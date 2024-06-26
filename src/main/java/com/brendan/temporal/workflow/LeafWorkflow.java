package com.brendan.temporal.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface LeafWorkflow {
    @WorkflowMethod
    String process(NodeInput input);
}
