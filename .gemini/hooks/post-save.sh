#!/bin/bash

# Gemini가 전달한 파일 경로를 환경 변수나 인자로 받는다고 가정합니다.
# 인자가 없다면 git status에서 방금 수정된 파일을 찾습니다.
TARGET_FILE=${1:-$(git diff --name-only | head -n 1)}

# 프로젝트 루트 디렉토리 설정 (IntelliJ 프로젝트 루트)
PROJECT_DIR=$(git rev-parse --show-toplevel)

case "$TARGET_FILE" in
  *.java|*.kts|*.json|*.yaml|*.yml|*.md|*.properties|*.xml|*.sql)
    echo "✨ [Gemini Hook] Running spotlessApply on: $TARGET_FILE"
    cd "$PROJECT_DIR" || exit 1

    # -PspotlessFiles를 사용하면 전체 프로젝트가 아닌 특정 파일만 빠르게 포맷팅할 수 있습니다.
    ./gradlew spotlessApply -PspotlessFiles="$TARGET_FILE" -q 2>/dev/null
    ;;
esac