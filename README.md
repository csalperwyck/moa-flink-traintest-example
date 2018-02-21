# Streaming Machine Learning with Flink and MOA #
This project combines:
- Flink as a stream engine: https://flink.apache.org/
- MOA as stream machine learning library: https://moa.cms.waikato.ac.nz/

## Data ##
The data is generated using the MOA RandomRBF generator.

This stream of data is then split into 2 streams using random sampling:
- Train: to build an incremental Decision Tree (Hoeffding tree)
- Test: to evaluate the performance of the classifier

## Performance evaluation ##
The evaluation of the performance is done using [Flink aggregate windows function](https://ci.apache.org/projects/flink/flink-docs-master/dev/stream/operators/windows.html#aggregatefunction), which computes the performance incrementally.

## Model update ##
The model is updated periodically using the [Flink CoFlatMapFunction](https://ci.apache.org/projects/flink/flink-docs-release-1.4/api/java/org/apache/flink/streaming/api/functions/co/CoFlatMapFunction.html). 

The documentation states exactly what we want to do:

> An example for the use of connected streams would be to apply rules that change over time onto elements of a stream. One of the connected streams has the rules, the other stream the elements to apply the rules to. The operation on the connected stream maintains the current set of rules in the state. It may receive either a rule update (from the first stream) and update the state, or a data element (from the second stream) and apply the rules in the state to the element. The result of applying the rules would be emitted.

A decision tree can be seen as a set of rules, so it fits perfectly with their example :-).

To avoid sending a new model at each new learned example we put a parameter (here 1,000 examples) to send the update less frequently.

## Output ##
The performance of the model is periodically output, each time 10,000 examples are tested.

```
Connected to JobManager at Actor[akka://flink/user/jobmanager_1#-511692578] with leader session id 2639af1e-4498-4bd9-a48b-673fa21529f5.
02/20/2018 16:53:28	Job execution switched to status RUNNING.
02/20/2018 16:53:28	Source: Custom Source -> Process(1/1) switched to SCHEDULED 
02/20/2018 16:53:28	Co-Flat Map(1/1) switched to SCHEDULED 
02/20/2018 16:53:28	TriggerWindow(GlobalWindows(), AggregatingStateDescriptor{serializer=org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer@463338d7, aggFunction=moa.flink.traintest.PerformanceFunction@5f2050f6}, PurgingTrigger(CountTrigger(10000)), AllWindowedStream.aggregate(AllWindowedStream.java:475)) -> Sink: Unnamed(1/1) switched to SCHEDULED 
02/20/2018 16:53:28	Source: Custom Source -> Process(1/1) switched to DEPLOYING 
02/20/2018 16:53:28	Co-Flat Map(1/1) switched to DEPLOYING 
02/20/2018 16:53:28	TriggerWindow(GlobalWindows(), AggregatingStateDescriptor{serializer=org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer@463338d7, aggFunction=moa.flink.traintest.PerformanceFunction@5f2050f6}, PurgingTrigger(CountTrigger(10000)), AllWindowedStream.aggregate(AllWindowedStream.java:475)) -> Sink: Unnamed(1/1) switched to DEPLOYING 
02/20/2018 16:53:28	Source: Custom Source -> Process(1/1) switched to RUNNING 
02/20/2018 16:53:28	TriggerWindow(GlobalWindows(), AggregatingStateDescriptor{serializer=org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer@463338d7, aggFunction=moa.flink.traintest.PerformanceFunction@5f2050f6}, PurgingTrigger(CountTrigger(10000)), AllWindowedStream.aggregate(AllWindowedStream.java:475)) -> Sink: Unnamed(1/1) switched to RUNNING 
02/20/2018 16:53:28	Co-Flat Map(1/1) switched to RUNNING 
0.8958
0.9244
0.9271
0.9321
0.9342
0.9345
0.9398
0.937
0.9386
0.9415
0.9396
0.9426
0.9429
0.9427
0.9454
...
```

The performance of our model increase over time, as expected for on incremental machine learning algorithm! 

You can check the [OzaBag project](https://github.com/csalperwyck/moa-flink-ozabag-example) to improve performances using online bagging and parallelize the computation using Flink.
