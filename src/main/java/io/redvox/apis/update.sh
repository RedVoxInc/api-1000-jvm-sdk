#!/usr/bin/env bash

set -o nounset
set -o errexit
set -o xtrace

curl -O -J https://raw.githubusercontent.com/RedVoxInc/redvox-api-1000/master/src/redvox_api_m/redvox_api_m.proto
curl -O -J https://raw.githubusercontent.com/RedVoxInc/redvox-api-1000/master/src/generated/java/io/redvox/apis/RedvoxApiM.java
