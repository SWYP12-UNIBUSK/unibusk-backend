#!/bin/bash
set -Eeuo pipefail

BASE_DIR="$HOME/deployment/prod"
NGINX_DIR="$BASE_DIR/nginx"
COMPOSE="$BASE_DIR/docker/docker-compose.yml"
STATE_FILE="$BASE_DIR/.active"

MAX_RETRY=30
INTERVAL=5
WARMUP_DELAY=10
APP_ENV_FILE="$BASE_DIR/.env"

log() { echo "[$(date +"%T")] $1"; }

rollback() {
  log "ROLLBACK triggered"

  sudo cp "$NGINX_DIR/unibusk-$CURRENT.conf" /etc/nginx/conf.d/default.conf
  sudo nginx -t
  sudo nginx -s reload

  docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" up -d $CURRENT

  docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" rm -f $NEXT || true

  # 상태 파일 복원
  echo "$CURRENT" > "$STATE_FILE"

  exit 1
}

trap rollback ERR

cd "$BASE_DIR"

[ ! -f "$APP_ENV_FILE" ] && echo "❌ $APP_ENV_FILE not found" && exit 1

set -a; source "$APP_ENV_FILE"; set +a

# 상태 파일로 현재 active 환경 판단 (없으면 docker ps로 fallback)
if [ -f "$STATE_FILE" ]; then
  CURRENT=$(cat "$STATE_FILE")
else
  if docker ps --filter "name=unibusk-blue" --filter "status=running" | grep unibusk-blue >/dev/null; then
    CURRENT="blue"
  else
    CURRENT="green"
  fi
fi

if [ "$CURRENT" = "blue" ]; then
  NEXT="green"
  PORT=8082
else
  NEXT="blue"
  PORT=8081
fi

log "Deploy: $CURRENT → $NEXT (image tag: ${DEPLOY_SHA:-latest})"

export IMAGE_TAG=${DEPLOY_SHA:-latest}

# 배포 전 기존 NEXT 컨테이너 삭제
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" rm -f $NEXT || true

# 이미지 pull 및 컨테이너 실행
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" pull $NEXT
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" up -d $NEXT

# 헬스체크
COUNT=0
until curl -sf http://127.0.0.1:$PORT/api/actuator/health >/dev/null; do
  COUNT=$((COUNT+1))
  [ $COUNT -ge $MAX_RETRY ] && { log "$NEXT unhealthy after $MAX_RETRY retries"; exit 1; }
  log "Waiting for $NEXT to be healthy... ($COUNT/$MAX_RETRY)"
  sleep $INTERVAL
done

log "$NEXT healthy — waiting ${WARMUP_DELAY}s for warmup..."
sleep $WARMUP_DELAY

# Nginx 교체
sudo cp "$NGINX_DIR/unibusk-$NEXT.conf" /etc/nginx/conf.d/default.conf
sudo nginx -t
sudo nginx -s reload

# 상태 파일 업데이트
echo "$NEXT" > "$STATE_FILE"

# 기존 컨테이너 정리 (graceful shutdown 대기)
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" stop $CURRENT
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" rm -f $CURRENT

log "Deploy success: $NEXT live (image: ${DEPLOY_SHA:-latest})"
