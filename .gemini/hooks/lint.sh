#!/bin/bash

# 1. 인자로 전달된 파일 경로 확인
FILE_PATH=$1

if [ -z "$FILE_PATH" ]; then
    echo "⚠️ 파일 경로가 전달되지 않았습니다."
    exit 1
fi

# 2. 프로젝트 루트로 이동
cd "$(git rev-parse --show-toplevel)" || exit 1

# 3. 대상 확장자 확인 및 Spotless 실행
case "$FILE_PATH" in
    *.java|*.kts|*.json|*.yaml|*.yml|*.md)
        echo "🎨 [post_write] Spotless applying to: $FILE_PATH"
        ./gradlew spotlessApply -PspotlessFiles="$FILE_PATH" -q 2>/dev/null
        ;;
esac

exit 0