*This repository is DEPRECATED! The development continues in the [ATMOSPHERE project](https://github.com/eubr-atmosphere/tma-framework-e/tree/master/development/executor)*

# tma-execute

This project aims to:
* Consume the items from a topic queue;
* Perform the adaption defined in the item of the queue.

## Prerequisites

This component requires the software available in [tma-utils](https://github.com/joseadp/tma-utils).

## Installation

This is a simple module to execute the plan from TMA.

To build the jar, you should run the following command on the worker node:
```sh
sh build.sh
```

To deploy the pod in the cluster, you should run the following command on the master node:

```sh
kubectl create -f tma-execute.yaml
```
