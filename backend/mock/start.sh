#!/bin/bash

current_path=$(pwd)

cp "$current_path/db.seed.json" "$current_path/db.json"

node "$current_path/server.js"