#!/bin/sh
echo "Start: Sleep 30 seconds"
sleep 30;
wait;
echo "Begin creating topics"
docker exec kafka kafka-topics --create --if-not-exists --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic report
echo "Done creating topics"