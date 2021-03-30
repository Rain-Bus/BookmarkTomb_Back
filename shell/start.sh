#!/bin/bash

function confirm() {
  if [ "Y" == "$1" ] || [ "y" == "$1" ]; then
    return 1
  elif [ "N" == "$1" ] || [ "n" == "$1" ]; then
    return 0
  else
    read -r -p "Input error, please in input again:[y/n]": CONFIRM
    confirm "$CONFIRM"
    return $?
  fi
}

function archiveFile() {
  read -r -p "Archive the jar ""$1"'? [y/n]:' CONFIRM
  confirm "$CONFIRM"
  if [ $? -eq 1 ]; then
    mv "$1" "$1"".old"
    echo -e "\e[32m Archived $1\e[0m"
  else
    exit
  fi
}

PID_ARR=$(ps -aux | grep -E "java -jar bookmark_tomb.*.jar" | grep -v grep | awk '{print $2}' | xargs echo -n)
if [ ${#PID_ARR[@]} -ne 0 ] && [ "$PID_ARR" != "" ]; then
  echo -e "\e[33mSystem has been running, please run stop.sh or restart.sh\e[0m"
  echo -e "Running PID: \e[33m$PID_ARR\e[0m"
  exit
fi

INSTALL_PATH=$(dirname "$0")
cd "$INSTALL_PATH" || exit

USER_HOME=${HOME}
SYSTEM_HOME="$USER_HOME/.bookmark_tomb"
CONF_PATH="$SYSTEM_HOME/conf.json"
LOG_PATH="$SYSTEM_HOME/bookmark_tomb.log"
INSTALL_PATH=$(dirname "$0")
cd "$INSTALL_PATH" || exit

if [ ! -d "$SYSTEM_HOME" ]; then
  echo "Created conf directory: $SYSTEM_HOME"
  mkdir "$SYSTEM_HOME"
fi
if [ ! -e "$CONF_PATH" ]; then
  echo "Created configure file : $CONF_PATH"
  touch "$CONF_PATH"
fi
if [ ! -e "$LOG_PATH" ]; then
  echo "Created log file: $LOG_PATH"
  touch "$LOG_PATH"
fi

CURRENT_JAR=0
for JAR_FILE in bookmark_tomb*.jar; do
  if [ -e "$JAR_FILE" ]; then
    JAR_ARR[$CURRENT_JAR]=$JAR_FILE
    ((CURRENT_JAR=CURRENT_JAR + 1))
  fi
done

if [ ${#JAR_ARR[@]} -eq 0 ]; then
  echo -e "\e[31mError: Can't find jar in install directory!"
  echo "The jar name must begin bookmark_tomb and end with .jar."
  exit

elif [ ${#JAR_ARR[@]} -gt 1 ]; then
  OLD_INDEX=0
  CURRENT_INDEX=1
  while [ $CURRENT_INDEX -lt "${#JAR_ARR[*]}" ]; do
      if [ "$(stat -c %W "${JAR_ARR[$OLD_INDEX]}")" -lt "$(stat -c %W "${JAR_ARR[$CURRENT_INDEX]}")" ]; then
        archiveFile "${JAR_ARR[$OLD_INDEX]}"
        ((OLD_INDEX = CURRENT_INDEX, CURRENT_INDEX++))
      else
        archiveFile "${JAR_ARR[$CURRENT_INDEX]}"
        ((CURRENT_INDEX++))
      fi
  done
fi

if [ -e "$CONF_PATH" ]; then
  SERVER_PORT="$(grep serverPort "$CONF_PATH" | grep -Eo "[0-9]+")"
  if [ -n "$SERVER_PORT" ] && ((SERVER_PORT > 0 && SERVER_PORT <= 65535)); then
    SERVER_PORT_PARAM="--server.port=$SERVER_PORT"
  fi
fi

RUN_JAR=$(ls -t bookmark_tomb*.jar)

nohup java -jar "$RUN_JAR" "$SERVER_PORT_PARAM" >> "$LOG_PATH" 2>&1 &

PID=$(ps -aux | grep -E "java -jar bookmark_tomb.*.jar" | grep -v grep | head -n 1 | awk '{print $2}' | xargs echo -n)
echo "Staring Bookmark Tomb..."
echo -e "PID: \e[32m$PID\e[0m"