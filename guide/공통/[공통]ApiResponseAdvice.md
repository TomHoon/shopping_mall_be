# ApiResponseAdvice 공통 응답 가로채기(이론설명)

---

## 스프링 프레임워크 흐름
> 응답 파이프라인을 확장함
> 전체흐름
```dbn-psql
Controller
   ↓
HandlerMethodReturnValueHandler
   ↓
HttpMessageConverter
   ↓
ResponseBodyAdvice  ← 여기
   ↓
HTTP Response
```
> 만든 흐름
```dbn-psql
Controller 반환값
      ↓
ResponseBodyAdvice.beforeBodyWrite()
      ↓
HttpMessageConverter
      ↓
JSON 변환
```