# Temporal Tree Fanout

Demonstration of fan-out using trees with Temporal workflows.

## Requirements

*(AKA What I had installed)*

* Java 1.8+
* Local Temporal Server. This is straightforward with the [Temporal CLI](https://github.com/temporalio/cli)

## Usage

Start the Temporal worker
```bash
./gradlew bootRun
```

Run the workflow
```zsh
temporal workflow start \
--type RootWorkflow \
--task-queue tree-tq \
--input 100 --input 5 # Number of elements, Number of children per node
```

## Learn more about Temporal and the Java SDK

* [Temporal Hompage](https://temporal.io/)
* [Temporal Java SDK Samples](https://github.com/temporalio/samples-java)
* [Java SDK Guide](https://docs.temporal.io/dev-guide/java)