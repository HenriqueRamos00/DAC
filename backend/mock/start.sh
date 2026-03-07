#!/bin/bash

current_path=$(pwd)

npm install json-server@0.17.4 jsonwebtoken cors

cp "$current_path/db.seed.json" "$current_path/db.json"

node "$current_path/server.js"