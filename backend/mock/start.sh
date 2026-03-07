#!/bin/bash

current_path=$(pwd)

package_file="$current_path/package.json"

if [[ ! -f "$package_file" ]]; then
    npm install json-server@0.17.4 jsonwebtoken cors
fi

cp "$current_path/db.seed.json" "$current_path/db.json"

node "$current_path/server.js"