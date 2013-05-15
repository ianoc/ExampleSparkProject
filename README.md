
Simple project to get started with spark


Content can go in src/main/scala/com/example/Driver.scala

MASTER="mesos://" sbt/sbt/run will run with a master specified
-- Currently that will presume this code is running also on a node where it can query the mesos status page(on localhost) for info on the cluster.

QUICKSTART:

sbt/sbt assembly
sbt/sbt run

Should show a lot of logs to the output screen but the important line is the true.

It will have parallelize a simple list and brought it back to the master again.
