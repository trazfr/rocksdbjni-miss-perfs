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
    echo "docker.env does not exists. Regerenate it."
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

docker-compose build
docker-compose up
