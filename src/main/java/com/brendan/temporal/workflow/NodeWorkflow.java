package com.brendan.temporal.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface NodeWorkflow {
    @WorkflowMethod
    Void run(NodeInput input);
}
