#!/bin/sh -e

cd "$(dirname -- "$(readlink -f "$0")")"

usage() {
    echo "Usage: $0 [--api-key <API_KEY> --site <SITE>]"
    echo
    echo "On the first run, you must provide the API_KEY and SITE"
    exit 1
}

SITE="datadoghq.com"
while [ "$#" -ne 0 ]; do
    case "$1" in
    --api-key)
        API_KEY="$2"
        shift
        ;;
    --site)
        SITE="$2"
        shift
        ;;
    *)
        echo "Unknown parameter passed: $1"
        usage
        ;;
    esac
    shift
done

if [ ! -e docker.env ]; then
    echo "docker.env does not exists. Regenerate it."
    if [ -z "$API_KEY" ] || [ -z "$SITE" ]; then
        usage
    fi
fi

if [ -n "$API_KEY" ] && [ -n "$SITE" ]; then
    cat <<EOF >docker.env
DD_API_KEY=$API_KEY
DD_SITE=$SITE
EOF
fi

COMPOSE=
if docker-compose version >/dev/null 2>&1; then
    COMPOSE=docker-compose
elif podman-compose version >/dev/null 2>&1; then
    COMPOSE=podman-compose
else
    echo "Please install docker-compose or podman-compose"
fi

$COMPOSE build
$COMPOSE up
