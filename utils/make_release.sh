#!/bin/sh

set -eux

URL="https://github.com/Friedchicken-42/progettoIngegneriaSoftware.git"
NAME="ecodigify"
DIR="/tmp/$NAME"

[ -d "$DIR" ] && rm -rf "$DIR"

git clone --filter=blob:none "$URL" "$DIR"

cd "$DIR"

rm ./LICENSE
rm -rf ./utils \
    ./.git*

zip -r "$HOME/Downloads/scratchdevs.zip" .

echo "ALL DONE"
