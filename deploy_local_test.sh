#!/bin/bash

ENV_VARS_FILE="env_test.json"
PORT=3000

sam local start-api -p $PORT --env-vars $ENV_VARS_FILE
