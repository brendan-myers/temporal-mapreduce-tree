package com.brendan.temporal.workflow;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface NodeWorkflow {
    @WorkflowMethod
    String run(NodeInput input);

    @SignalMethod
    void putResult(Integer result);
}
