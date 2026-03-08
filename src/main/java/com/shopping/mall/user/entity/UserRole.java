package com.shopping.mall.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
	name = "user_role",
	indexes = {
		@Index(name = "idx_user_role_user_id", columnList = "userId"),
		@Index(name = "idx_user_role_role_id", columnList = "roleId")
	}
)
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long roleId;

}