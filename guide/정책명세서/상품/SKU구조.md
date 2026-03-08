# SKU 구조 설명 (상품 옵션과 재고 관리)

이 문서는 쇼핑몰에서 사용하는 **SKU 구조**를 이해하기 쉽게 설명하기 위한 문서이다.
특히 다음 세 개의 테이블 관계를 중심으로 설명한다.

```
option_value
sku
sku_option
```

---

# 1. SKU란 무엇인가

SKU는 **Stock Keeping Unit**의 약자로
**실제로 판매 및 재고 관리가 되는 최소 단위 상품**을 의미한다.

예시 상품

```
상품: 스키니 바지
```

옵션

```
Color: Black, Red
Size: M, L
```

사용자가 선택할 수 있는 조합

```
Black M
Black L
Red M
Red L
```

이 **각 조합 하나하나가 SKU**이다.

```
SKU
 ├ Black M
 ├ Black L
 ├ Red M
 └ Red L
```

즉 SKU는

```
옵션 조합 + 재고 + 가격
```

을 가진 **실제 판매 단위 상품**이다.

---

# 2. SKU 테이블 역할

테이블

```
sku
```

예시 데이터

| id | sku_code    | price | stock |
| -- | ----------- | ----- | ----- |
| 1  | SKU-BLACK-M | 10000 | 10    |
| 2  | SKU-BLACK-L | 10000 | 5     |
| 3  | SKU-RED-M   | 10000 | 7     |
| 4  | SKU-RED-L   | 10000 | 3     |

설명

```
각 SKU는
옵션 조합 하나를 의미한다
```

그리고 SKU는

```
재고 관리
가격 관리
```

의 기준이 된다.

---

# 3. option_value 테이블

옵션 값 테이블

```
option_value
```

예시 데이터

| id | option_group | value |
| -- | ------------ | ----- |
| 1  | Color        | Black |
| 2  | Color        | Red   |
| 3  | Size         | M     |
| 4  | Size         | L     |

설명

```
옵션 값 자체를 저장하는 테이블
```

예

```
Black
Red
M
L
```

---

# 4. sku_option 테이블

SKU와 옵션을 연결하는 테이블

```
sku_option
```

예시 데이터

| sku_id | option_value_id |
| ------ | --------------- |
| 1      | 1               |
| 1      | 3               |
| 2      | 1               |
| 2      | 4               |

설명

```
SKU는 여러 옵션 값의 조합이기 때문에
중간 테이블이 필요하다
```

---

# 5. SKU 조합이 만들어지는 과정

예

```
SKU 1 = Black M
```

DB 구조

```
sku
id = 1
```

```
sku_option
sku_id = 1
option_value_id = 1 (Black)

sku_id = 1
option_value_id = 3 (M)
```

즉

```
Black + M
```

조합이 된다.

---

# 6. 전체 구조

```
option_value
 ├ Black
 ├ Red
 ├ M
 └ L
```

```
sku_option
 ├ SKU1 → Black
 ├ SKU1 → M
 ├ SKU2 → Black
 ├ SKU2 → L
```

```
sku
 ├ SKU1 → Black M
 ├ SKU2 → Black L
 ├ SKU3 → Red M
 └ SKU4 → Red L
```

---

# 7. 왜 이런 구조를 사용하는가

이 구조의 장점

```
옵션 조합을 유연하게 생성 가능
옵션 개수가 늘어나도 구조 변경 없음
재고 관리 단위 분리
```

예

옵션 추가

```
Material: Cotton
```

이 경우에도

```
sku_option
```

테이블에 조합만 추가하면 된다.

---

# 8. 정리

```
option_value
→ 옵션 값 저장
```

```
sku
→ 실제 판매 상품
```

```
sku_option
→ SKU와 옵션 조합 연결
```

전체 구조

```
option_value
      │
      │
sku_option
      │
      │
     sku
```

즉

```
옵션 조합 → SKU 생성 → SKU 기준 재고 관리
```
