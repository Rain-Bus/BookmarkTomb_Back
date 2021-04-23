echo "Restarting Bookmark Tomb..."

INSTALL_PATH=$(dirname "$0")
cd "$INSTALL_PATH" || exit

./stop.sh

echo "Stopped"

./start.sh