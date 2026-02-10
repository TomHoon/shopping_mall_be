--회원
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(100) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE seller (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  brand_name VARCHAR(255) NOT NULL,
  business_number VARCHAR(50) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_seller_user
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,

  PRIMARY KEY (user_id, role_id),

  CONSTRAINT fk_user_role_user
    FOREIGN KEY (user_id) REFERENCES user(id),
  CONSTRAINT fk_user_role_role
    FOREIGN KEY (role_id) REFERENCES role(id)
);

-- 상품, 옵션, SKU
CREATE TABLE product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  seller_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  base_price INT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  CONSTRAINT fk_product_seller
    FOREIGN KEY (seller_id) REFERENCES seller(id)
);



CREATE TABLE option_group (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,       -- color, size
  sort_order INT NOT NULL DEFAULT 0,

  CONSTRAINT fk_option_group_product
    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE option_value (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  option_group_id BIGINT NOT NULL,
  value VARCHAR(50) NOT NULL,      -- Black, M

  CONSTRAINT fk_option_value_group
    FOREIGN KEY (option_group_id) REFERENCES option_group(id)
);


CREATE TABLE sku (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  sku_code VARCHAR(100) NOT NULL UNIQUE,
  price INT NOT NULL,
  stock INT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

  CONSTRAINT fk_sku_product
    FOREIGN KEY (product_id) REFERENCES product(id)
);


CREATE TABLE sku_option (
  sku_id BIGINT NOT NULL,
  option_value_id BIGINT NOT NULL,

  PRIMARY KEY (sku_id, option_value_id),

  CONSTRAINT fk_sku_option_sku
    FOREIGN KEY (sku_id) REFERENCES sku(id),
  CONSTRAINT fk_sku_option_value
    FOREIGN KEY (option_value_id) REFERENCES option_value(id)
);
CREATE TABLE stock_history (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sku_id BIGINT NOT NULL,
  change_amount INT NOT NULL,
  reason VARCHAR(50) NOT NULL,      -- ORDER, CANCEL, ADJUST
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_stock_history_sku
    FOREIGN KEY (sku_id) REFERENCES sku(id)
);

-- 장바구니
CREATE TABLE cart (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_cart_user
    FOREIGN KEY (user_id) REFERENCES user(id)
);
CREATE TABLE cart_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  cart_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  quantity INT NOT NULL,

  CONSTRAINT fk_cart_item_cart
    FOREIGN KEY (cart_id) REFERENCES cart(id),

  CONSTRAINT fk_cart_item_sku
    FOREIGN KEY (sku_id) REFERENCES sku(id),

  CONSTRAINT uk_cart_sku
    UNIQUE (cart_id, sku_id)
);

--주문 도메인

CREATE TABLE `order` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  order_status VARCHAR(30) NOT NULL,  -- CREATED, PAID, SHIPPED, COMPLETED, CANCELED
  total_amount INT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  CONSTRAINT fk_order_user
    FOREIGN KEY (user_id) REFERENCES user(id)
);
CREATE TABLE order_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  seller_id BIGINT NOT NULL,

  price_snapshot INT NOT NULL,
  option_snapshot VARCHAR(255) NOT NULL,
  quantity INT NOT NULL,

  order_item_status VARCHAR(30) NOT NULL,
  -- ORDERED, SHIPPED, DELIVERED, CONFIRMED, CANCELED, REFUNDED

  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_order_item_order
    FOREIGN KEY (order_id) REFERENCES `order`(id),

  CONSTRAINT fk_order_item_sku
    FOREIGN KEY (sku_id) REFERENCES sku(id),

  CONSTRAINT fk_order_item_seller
    FOREIGN KEY (seller_id) REFERENCES seller(id)
);

-- 결제
CREATE TABLE payment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL UNIQUE,

  payment_method VARCHAR(30) NOT NULL,   -- CARD, TRANSFER, VIRTUAL_ACCOUNT
  payment_status VARCHAR(30) NOT NULL,   -- READY, PAID, FAILED, CANCELED

  paid_amount INT NOT NULL,
  payment_key VARCHAR(100),              -- PG사 결제키
  approved_at DATETIME,

  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_payment_order
    FOREIGN KEY (order_id) REFERENCES `order`(id)
);
-- 배송
CREATE TABLE shipment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_item_id BIGINT NOT NULL UNIQUE,

  carrier VARCHAR(50) NOT NULL,      -- CJ, LOTTE, HANJIN
  tracking_no VARCHAR(100),
  shipment_status VARCHAR(30) NOT NULL,
  -- READY, SHIPPED, DELIVERED

  shipped_at DATETIME,
  delivered_at DATETIME,

  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_shipment_order_item
    FOREIGN KEY (order_item_id) REFERENCES order_item(id)
);

-- 교환/환불
CREATE TABLE refund (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_item_id BIGINT NOT NULL UNIQUE,

  refund_amount INT NOT NULL,
  refund_status VARCHAR(30) NOT NULL,  -- REQUESTED, APPROVED, COMPLETED, REJECTED
  reason VARCHAR(255),

  requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at DATETIME,

  CONSTRAINT fk_refund_order_item
    FOREIGN KEY (order_item_id) REFERENCES order_item(id)
);
CREATE TABLE exchange (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_item_id BIGINT NOT NULL UNIQUE,

  exchange_status VARCHAR(30) NOT NULL, -- REQUESTED, SHIPPED, COMPLETED, REJECTED
  reason VARCHAR(255),

  requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at DATETIME,

  CONSTRAINT fk_exchange_order_item
    FOREIGN KEY (order_item_id) REFERENCES order_item(id)
);
CREATE TABLE claim (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_item_id BIGINT NOT NULL,

  claim_type VARCHAR(30) NOT NULL,   -- REFUND, EXCHANGE, DISPUTE
  status VARCHAR(30) NOT NULL,
  description TEXT,

  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_claim_order_item
    FOREIGN KEY (order_item_id) REFERENCES order_item(id)
);

-- 쿠폰

CREATE TABLE coupon (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  coupon_type VARCHAR(30) NOT NULL,      -- PLATFORM, SELLER, PRODUCT
  discount_type VARCHAR(20) NOT NULL,    -- RATE, FIXED
  discount_value INT NOT NULL,
  max_discount_amount INT,
  seller_id BIGINT,                      -- SELLER 쿠폰일 때만 사용
  valid_from DATETIME NOT NULL,
  valid_to DATETIME NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_coupon_seller
    FOREIGN KEY (seller_id) REFERENCES seller(id)
);
CREATE TABLE coupon_issue (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  coupon_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  used_at DATETIME,

  CONSTRAINT fk_coupon_issue_coupon
    FOREIGN KEY (coupon_id) REFERENCES coupon(id),

  CONSTRAINT fk_coupon_issue_user
    FOREIGN KEY (user_id) REFERENCES user(id)
);
CREATE TABLE discount_payer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  payer_type VARCHAR(20) NOT NULL,  -- PLATFORM, SELLER
  seller_id BIGINT,

  CONSTRAINT fk_discount_payer_seller
    FOREIGN KEY (seller_id) REFERENCES seller(id)
);
CREATE TABLE coupon_usage (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  coupon_issue_id BIGINT NOT NULL,
  order_item_id BIGINT NOT NULL,
  discount_amount INT NOT NULL,
  discount_payer_id BIGINT NOT NULL,

  CONSTRAINT fk_coupon_usage_issue
    FOREIGN KEY (coupon_issue_id) REFERENCES coupon_issue(id),

  CONSTRAINT fk_coupon_usage_order_item
    FOREIGN KEY (order_item_id) REFERENCES order_item(id),

  CONSTRAINT fk_coupon_usage_payer
    FOREIGN KEY (discount_payer_id) REFERENCES discount_payer(id)
);

-- 정산
CREATE TABLE settlement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  seller_id BIGINT NOT NULL,

  period_start DATE NOT NULL,
  period_end DATE NOT NULL,

  total_amount INT NOT NULL,
  settlement_status VARCHAR(30) NOT NULL, -- READY, HOLD, COMPLETED
  settled_at DATETIME,

  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_settlement_seller
    FOREIGN KEY (seller_id) REFERENCES seller(id)
);
CREATE TABLE settlement_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  settlement_id BIGINT NOT NULL,
  order_item_id BIGINT NOT NULL UNIQUE,

  base_amount INT NOT NULL,        -- price_snapshot * quantity
  discount_amount INT NOT NULL,    -- 셀러 부담 쿠폰 합계
  commission_amount INT NOT NULL,  -- 플랫폼 수수료
  settled_amount INT NOT NULL,     -- 최종 정산 금액

  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_settlement_item_settlement
    FOREIGN KEY (settlement_id) REFERENCES settlement(id),

  CONSTRAINT fk_settlement_item_order_item
    FOREIGN KEY (order_item_id) REFERENCES order_item(id)
);

-- 리뷰 적립금
CREATE TABLE review (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_item_id BIGINT NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,

  rating INT NOT NULL,
  content TEXT NOT NULL,

  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_review_order_item
    FOREIGN KEY (order_item_id) REFERENCES order_item(id),

  CONSTRAINT fk_review_user
    FOREIGN KEY (user_id) REFERENCES user(id)
);
CREATE TABLE review_image (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  review_id BIGINT NOT NULL,
  image_url VARCHAR(255) NOT NULL,

  CONSTRAINT fk_review_image_review
    FOREIGN KEY (review_id) REFERENCES review(id)
);
CREATE TABLE review_reward (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  review_id BIGINT NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,

  reward_amount INT NOT NULL,
  reward_status VARCHAR(30) NOT NULL, -- PENDING, GRANTED, REVOKED
  granted_at DATETIME,

  CONSTRAINT fk_review_reward_review
    FOREIGN KEY (review_id) REFERENCES review(id),

  CONSTRAINT fk_review_reward_user
    FOREIGN KEY (user_id) REFERENCES user(id)
);
CREATE TABLE point_ledger (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,

  amount INT NOT NULL,              -- + 적립 / - 차감
  reason_type VARCHAR(30) NOT NULL, -- REVIEW, ORDER, REFUND
  ref_id BIGINT,                    -- review_id, order_id 등

  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_point_ledger_user
    FOREIGN KEY (user_id) REFERENCES user(id)
);
