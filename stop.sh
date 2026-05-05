#!/bin/bash
set -e

if [ "$1" = "--volumes" ]; then
  echo "Parando containers e removendo volumes..."
  docker compose down -v
else
  echo "Parando containers..."
  docker compose down
fi
