package com.brendan.temporal.workflow;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

@WorkflowImpl(taskQueues = "tree-tq")
public class NodeWorkflowImpl implements NodeWorkflow {
    @Value("${tree.maxItemsPerLeaf}")
    private Integer maxItemsPerLeaf;

    @Value("${tree.maxChildren}")
    private Integer maxChildren;

    @Value("${tree.maxDepth}")
    private Integer maxDepth;

    private List<Integer> results = new ArrayList<Integer>();
    private Integer numChildren = 0;

    @Override
    public String run(NodeInput input) {
        // Is this the root node?
        if (input.getDepth() == 0) {
            input = new NodeInput(0, input.getLength(), 0, 
                maxItemsPerLeaf, maxChildren, maxDepth);
        }

        // Do we need another layer of nodes?
        if (input.getMaxChildren() * input.getMaxItemsPerLeaf() < input.getLength()) {
            if (input.getDepth() == input.getMaxDepth()) {
                throw Workflow.wrap(new Exception("Max depth exceeded"));
            }

            createNodes(input);

        } else {
            createLeaves(input);
        }

        Workflow.await(() -> results.size() >= numChildren);

        // reduce the results
        Integer result = results.stream().reduce(0, (x,y) -> x+y);

        if (input.getDepth() > 0) {
            NodeWorkflow parent = Workflow.newExternalWorkflowStub(NodeWorkflow.class,
                Workflow.getInfo().getParentWorkflowId().get());
            
            parent.putResult(result);
        }

        return "Processed " + result + " records.";
    }

    @Override
    public void putResult(Integer result) {
        results.add(result);
    }

    private void createNodes(NodeInput input) {
        int offset = input.getOffset();
        
        List<Promise<String>> promises = new ArrayList<>();
        
        // Split the remaining items amongst maxChildren
        while (offset < input.getOffset() + input.getLength()) {
            int length = input.getLength() / input.getMaxChildren();

            // Add any extra records to the first child
            if (offset == input.getOffset()) {
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

            numChildren++;

            offset += length;
        }

        Promise.allOf(promises).get();
    }

    private void createLeaves(NodeInput input) {
        List<Promise<String>> promises = new ArrayList<>();

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

            numChildren++;

            // use the minimum number of leaf nodes, but each with the max items.
            // alternative: evenly distribute across maxChildren leaves.
            offset += input.getMaxItemsPerLeaf();
        }

        Promise.allOf(promises).get();
    }
}