#!/bin/bash

set -e

./mvnw spotless:apply

./mvnw clean verify

RED='\033[0;31m'
GREEN='\033[0;32m'
NO_COLOUR='\033[0m'

PUSH="
    ____  __  _______ __  __
   / __ \/ / / / ___// / / /
  / /_/ / / / /\__ \/ /_/ /
 / ____/ /_/ /___/ / __  /
/_/    \____//____/_/ /_/

"

FAIL="
 _______    ___       __   __
|   ____|  /   \     |  | |  |
|  |__    /  ^  \    |  | |  |
|   __|  /  /_\  \   |  | |  |
|  |    /  _____  \  |  | |  |----.
|__|   /__/     \__\ |__| |_______|

"

if [ -n "$(git status --porcelain)" ];
then
  echo -e "${RED}${FAIL}\n\n !!! You have uncommited changes. Should these be committed?${NO_COLOUR} !!!\n"
  exit 1
else
  echo -e "${GREEN}${PUSH}${NO_COLOUR}\n"
fi