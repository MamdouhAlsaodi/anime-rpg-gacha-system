#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

# Hyprland/Wayland + XWayland can show Java Swing as a blank white surface
# unless Java is told it is running under a non-reparenting window manager.
export _JAVA_AWT_WM_NONREPARENTING=1

# Disable XRender pipeline on this setup; it avoids blank/late Swing repaints.
exec java -Dsun.java2d.xrender=false -cp out client.GachaClientApp
