package com.brendan.temporal.workflow;

import java.util.ArrayList;
import java.util.List;

import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

@WorkflowImpl(taskQueues = "tree-tq")
public class NodeWorkflowImpl implements NodeWorkflow {
    @Override
    public Void run(NodeInput input) {
        // Do we need another layer of nodes?
        if (input.getMaxChildren() * input.getMaxItemsPerLeaf() < input.getLength()) {
            // if (input.getDepth() == input.getMaxDepth()) {
            //     throw new Exception("Max depth exceeded");
            // }

            createNodes(input);

        } else {
            createLeaves(input);
        }

        return null;
    }

    private void createNodes(NodeInput input) {
        int i = 0;
        int offset = 0;
        
        List<Promise<Void>> promises = new ArrayList<>();
        
        // Split the remaining items amongst maxChildren
        while (i < input.getMaxChildren()) {
            int length = input.getLength() / input.getMaxChildren();

            // Add any extra records to the first child
            if (i == 0) {
                length += input.getLength() % input.getMaxChildren();
            }

            NodeInput childNode = new NodeInput(offset, length, input.getDepth()+1, 
                input.getMaxItemsPerLeaf(), input.getMaxChildren(), input.getMaxDepth());

            NodeWorkflow node = Workflow.newChildWorkflowStub(
                NodeWorkflow.class,
                ChildWorkflowOptions.newBuilder()
                    .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
                    .build());

            promises.add(Async.function(node::run, childNode));

            i++;
            offset += length;
        }

        Promise.allOf(promises).get();
    }

    private void createLeaves(NodeInput input) {
        List<Promise<Void>> promises = new ArrayList<>();

        int offset = input.getOffset();

        while (offset < input.getOffset() + input.getLength()) {
            int length = input.getMaxItemsPerLeaf();
            if (offset + length > input.getOffset() + input.getLength()) {
                length = input.getOffset() + input.getLength() - offset;
            }

            NodeInput leafNode = new NodeInput(offset, length, input.getDepth()+1, 
                input.getMaxItemsPerLeaf(), input.getMaxChildren(), input.getMaxDepth());

            LeafWorkflow leaf = Workflow.newChildWorkflowStub(
                LeafWorkflow.class,
                ChildWorkflowOptions.newBuilder()
                    .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
                    .build()
            );

            promises.add(Async.function(leaf::process, leafNode));

            // use the minimum number of leaf nodes, but each with the max items.
            // alternative: evenly distribute across maxChildren leaves.
            offset += input.getMaxItemsPerLeaf();
        }

        Promise.allOf(promises).get();
    }
}