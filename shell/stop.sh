#! /bin/bash

echo "Stopping Bookmark Tomb..."

PID_ARR=$(ps -aux | grep -E "java -jar bookmark_tomb.*.jar" | grep -v grep | awk '{print $2}' | xargs echo -n)
if [ ${#PID_ARR[@]} -ne 0 ] && [ "$PID_ARR" != "" ]; then
  for PID in $PID_ARR; do
    echo -e "Stopping PID: \e[34m$PID\e[0m"
    kill "$PID"
  done
  else
    echo -e "\e[33mNo progress ran on this machine! Please run start.sh to start.\e[0m"
fi

STOP_TIME=1
while [ $STOP_TIME -lt 30 ]; do
  PID_ARR=$(ps -aux | grep -E "java -jar bookmark_tomb.*.jar" | grep -v grep | awk '{print $2}' | xargs echo -n)
  if [ ${#PID_ARR[@]} -ne 0 ] && [ "$PID_ARR" != "" ]; then
    sleep 1
    ((STOP_TIME++))
  else
    break
  fi
done

if [ "$STOP_TIME" -eq 30 ]; then
  PID_ARR=$(ps -aux | grep -E "java -jar bookmark_tomb.*.jar" | grep -v grep | awk '{print $2}' | xargs echo -n)
  for PID in $PID_ARR; do
    echo -e "\e[31mForce stop PID: $PID\e[0m"
    kill -9 "$PID"
  done
fi