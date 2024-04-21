package com.brendan.temporal.workflow;

import org.springframework.beans.factory.annotation.Value;

import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

@WorkflowImpl(taskQueues = "tree-tq")
public class RootWorkflowImpl implements RootWorkflow {
    @Value("${tree.maxItemsPerLeaf}")
    private Integer maxItemsPerLeaf;

    @Value("${tree.maxChildren}")
    private Integer maxChildren;

    @Value("${tree.maxDepth}")
    private Integer maxDepth;

    @Override
    public void run(int items, int branches) throws Exception {
        NodeInput input = new NodeInput(0, items, 0, 
            maxItemsPerLeaf, maxChildren, maxDepth);

        NodeWorkflow node = Workflow.newChildWorkflowStub(
            NodeWorkflow.class,
            ChildWorkflowOptions.newBuilder()
                .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
                .build()
        );
        
        Promise<Void> child = Async.function(node::run, input);
        child.get();
    }
}