package com.brendan.temporal.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface RootWorkflow {
    @WorkflowMethod
    void run(int items, int branches) throws Exception;
}
