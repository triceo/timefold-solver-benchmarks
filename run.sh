#!/bin/bash
sudo -i sysctl kernel.perf_event_paranoid=1
sudo -i sysctl kernel.kptr_restrict=0
nohup taskset -c 0,2,4 java -jar target/benchmarks.jar > target/nohup.out 2>&1 &