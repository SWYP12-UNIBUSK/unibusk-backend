#!/bin/bash
set -Eeuo pipefail

BASE_DIR="$HOME/deployment/prod"
NGINX_DIR="$BASE_DIR/nginx"
COMPOSE="$BASE_DIR/docker/docker-compose.yml"
STATE_FILE="$BASE_DIR/.state"

MAX_RETRY=30
INTERVAL=5
WARMUP_DELAY=10
APP_ENV_FILE="$BASE_DIR/.env"

CURRENT=""
NEXT=""

log() { echo "[$(date +"%T")] $1"; }

load_state() {
  if [ -f "$STATE_FILE" ]; then
    source "$STATE_FILE"
    CURRENT=${ACTIVE:-}
  fi
}

save_state() {
  local active=$1
  local sha=$2
  local tmp
  tmp=$(mktemp "$BASE_DIR/.state.XXXXXX")
  echo "ACTIVE=$active" > "$tmp"
  echo "SHA=$sha" >> "$tmp"
  mv "$tmp" "$STATE_FILE"
}

rollback() {
  log "ROLLBACK triggered"

  if [ -z "${CURRENT:-}" ]; then
    log "CURRENT not defined, cannot rollback"
    exit 1
  fi

  if [ -f "$STATE_FILE" ]; then
    source "$STATE_FILE"
    PREV_SHA=${SHA:-}
    if [ -n "$PREV_SHA" ]; then
      log "Rolling back to image: $PREV_SHA"
      export IMAGE_TAG=$PREV_SHA
      docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" pull "$CURRENT"
      docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" up -d "$CURRENT"
    else
      log "No previous SHA found, restarting CURRENT as-is"
      docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" up -d "$CURRENT"
    fi
  else
    log "No state file found, restarting CURRENT as-is"
    docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" up -d "$CURRENT"
  fi

  # 롤백 헬스체크
  ROLLBACK_PORT=8081
  [ "$CURRENT" = "green" ] && ROLLBACK_PORT=8082
  COUNT=0
  until curl -sf http://127.0.0.1:$ROLLBACK_PORT/api/actuator/health >/dev/null; do
    COUNT=$((COUNT+1))
    [ $COUNT -ge $MAX_RETRY ] && { log "$CURRENT rollback unhealthy, giving up"; break; }
    log "Waiting for $CURRENT to be healthy... ($COUNT/$MAX_RETRY)"
    sleep $INTERVAL
  done

  log "$CURRENT healthy — waiting ${WARMUP_DELAY}s for warmup..."
  sleep $WARMUP_DELAY

  sudo cp "$NGINX_DIR/unibusk-$CURRENT.conf" /etc/nginx/conf.d/default.conf
  sudo nginx -t
  if pgrep -x nginx >/dev/null 2>&1; then
    sudo nginx -s reload
  else
    log "nginx not running — starting instead of reload"
    sudo nginx
  fi

  docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" rm -f "$NEXT" || true

  save_state "$CURRENT" "${PREV_SHA:-}"

  exit 1
}

trap rollback ERR

cd "$BASE_DIR"

[ ! -f "$APP_ENV_FILE" ] && echo "❌ $APP_ENV_FILE not found" && exit 1

set -a; source "$APP_ENV_FILE"; set +a

: "${DEPLOY_SHA:?DEPLOY_SHA must be set for production deploys}"

# 상태 파일로 현재 active 환경 판단 (없으면 docker ps로 fallback)
load_state

if [ -z "${CURRENT:-}" ]; then
  if docker ps --filter "name=unibusk-blue" --filter "status=running" | grep unibusk-blue >/dev/null; then
    CURRENT="blue"
  elif docker ps --filter "name=unibusk-green" --filter "status=running" | grep unibusk-green >/dev/null; then
    CURRENT="green"
  else
    CURRENT="green"
    log "Initial deployment detected, starting with blue"
  fi
fi

if [ "$CURRENT" = "blue" ]; then
  NEXT="green"
  PORT=8082
else
  NEXT="blue"
  PORT=8081
fi

log "Deploy: $CURRENT → $NEXT (image tag: $DEPLOY_SHA)"

export IMAGE_TAG="$DEPLOY_SHA"

# 배포 전 기존 NEXT 컨테이너 삭제
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" rm -f "$NEXT" || true

# 이미지 pull 및 컨테이너 실행
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" pull "$NEXT"
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" up -d "$NEXT"

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
if pgrep -x nginx >/dev/null 2>&1; then
  sudo nginx -s reload
else
  log "nginx not running — starting instead of reload"
  sudo nginx
fi

# 기존 컨테이너 정리 (graceful shutdown 대기)
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" stop -t 35 "$CURRENT"
docker compose -f "$COMPOSE" --env-file "$APP_ENV_FILE" rm -f "$CURRENT"

# 이미지 정리 (실행 중 + 직전 SHA 1개 보존)
log "Cleaning up unused images..."

# 변수 유효성 검증 (DEPLOY_SHA와 동일한 패턴)
: "${DOCKER_USERNAME:?DOCKER_USERNAME must be set}"
: "${IMAGE_NAME:?IMAGE_NAME must be set}"

ACTIVE_IMAGES=$(docker ps --format '{{.Image}}' | sort -u)

KEEP_IMAGES=$(docker images "${DOCKER_USERNAME}/${IMAGE_NAME}" \
  --format "{{.CreatedAt}} {{.Repository}}:{{.Tag}}" | \
  awk '!/:latest$/' | \
  sort -r | \
  head -2 | \
  awk '{print $NF}')

docker images "${DOCKER_USERNAME}/${IMAGE_NAME}" \
  --format "{{.Repository}}:{{.Tag}}" | \
  awk '!/:latest/' | \
  while read -r img_ref; do
    if echo "$KEEP_IMAGES" | grep -Fq "$img_ref"; then
      log "Keeping image: $img_ref"
    elif echo "$ACTIVE_IMAGES" | grep -Fq "$img_ref"; then
      log "Keeping active image: $img_ref"
    else
      log "Removing old image: $img_ref"
      docker rmi -f "$img_ref" 2>/dev/null || true
    fi
  done

docker image prune -f

save_state "$NEXT" "$DEPLOY_SHA"

log "Deploy success: $NEXT live (image: $DEPLOY_SHA)"