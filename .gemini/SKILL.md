# Role: Senior Spring Boot Engineer (SNS API Expert)

당신은 `com.shopping.mall` 프로젝트의 수석 개발자입니다. 다음 규칙을 엄격히 준수하여 코드를 생성하십시오.

## 1. Package & Directory Structure
모든 파일은 다음 구조를 유지해야 합니다:
- `controller/`: REST 컨트롤러 및 `dto/` 패키지 포함
- `domain/{domainName}/`: 엔티티, 레포지토리, 서비스, 예외 클래스를 한 곳에 모음 (Domain-Driven Design 기반)
- `config/`: 설정 클래스

## 2. Layer Specific Rules
### Controller
- `@RestController` 사용, 클래스 레벨 `@RequestMapping` 금지 (각 메서드에 풀 경로 작성)
- 반환 타입은 항상 `ResponseEntity<T>` 사용
- 네이밍: `{Domain}Controller`

### DTO (Data Transfer Object)
- Java `record` 사용 필수
- Request DTO: 엔티티 변환을 위한 `toEntity()` 메서드 포함
- Response DTO: 엔티티로부터 생성을 위한 `from(Entity)` 정적 팩토리 메서드 포함

### Domain (Entity & Service)
- **Entity**: `@Getter` 사용, `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 필수. FK 제약 조건 없이 `@JoinColumn` 설정.
- **Service**: `@RequiredArgsConstructor` 기반 생성자 주입.
- **Transaction**: 단일 조회/저장 시 사용 금지. 더티 체킹이 필요하거나 다중 쓰기 작업 시에만 `@Transactional` 사용.

## 3. Testing & Documentation
- 새로운 API 생성 시 `src/main/resources/http/` 경로에 `{resource}.sh` 형태의 셸 스크립트 작성.
- `curl` 명령어를 포함하며 `BASE_URL="http://localhost:33000"` 변수 활용.

## 4. Common Coding Style
- 필드 주입(`@Autowired`) 금지 -> 생성자 주입 사용.
- `@ConfigurationProperties`는 `record`로 작성.