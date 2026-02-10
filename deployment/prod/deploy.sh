#!/bin/bash
set -Eeuo pipefail

BASE_DIR="$HOME/deployment/prod"
NGINX_DIR="$BASE_DIR/nginx"
COMPOSE="$BASE_DIR/docker/docker-compose.yml"

MAX_RETRY=15
INTERVAL=3
APP_ENV_FILE="/opt/unibusk/env/prod.env"

log() {
  echo "[$(date +"%T")] $1"
}

CURRENT=""
NEXT=""

rollback() {
  trap - ERR
  log "ROLLBACK triggered"

  if [ -z "$CURRENT" ]; then
      log "CURRENT not set, cannot rollback nginx config"
      exit 1
  fi

  sudo cp "$NGINX_DIR/unibusk-$CURRENT.conf" /etc/nginx/conf.d/default.conf
  sudo nginx -t && sudo nginx -s reload || log "nginx reload failed during rollback"

  docker compose -f "$COMPOSE" up -d $CURRENT

  exit 1
}

trap rollback ERR

cd "$BASE_DIR"

if [ ! -f "$APP_ENV_FILE" ]; then
  echo "❌ $APP_ENV_FILE not found"
  exit 1
fi

if docker ps --filter "name=unibusk-blue" --filter "status=running" | grep -q unibusk-blue; then
    CURRENT="blue"
    NEXT="green"
    PORT=8082
elif docker ps --filter "name=unibusk-green" --filter "status=running" | grep -q unibusk-green; then
    CURRENT="green"
    NEXT="blue"
    PORT=8081
else
    log "No running container found. Initial deployment — rollback disabled."
    CURRENT=""
    NEXT="blue"
    PORT=8081
fi

log "Deploy: $CURRENT → $NEXT"

export IMAGE_TAG=${IMAGE_TAG:-latest}

docker compose -f "$COMPOSE" pull $NEXT
docker compose -f "$COMPOSE" up -d $NEXT

sleep 10

COUNT=0
until curl -sf http://127.0.0.1:$PORT/actuator/health >/dev/null; do
  COUNT=$((COUNT+1))
  [ $COUNT -ge $MAX_RETRY ] && exit 1
  sleep $INTERVAL
done

log "$NEXT healthy"

sudo cp "$NGINX_DIR/unibusk-$NEXT.conf" /etc/nginx/conf.d/default.conf
sudo nginx -t
sudo nginx -s reload

if [ -n "$CURRENT" ]; then
    docker compose -f "$COMPOSE" stop $CURRENT || true
    docker compose -f "$COMPOSE" rm -f $CURRENT || true
fi

log "Deploy success: $NEXT live"
