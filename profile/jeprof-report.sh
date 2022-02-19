#!/bin/bash
jeprof --svg /tmp/heap/jeprof.$1.* >/tmp/heap/$1-report.svg 2>/dev/null
jeprof --text /tmp/heap/jeprof.$1.* >/tmp/heap/$1-report.txt 2>/dev/null