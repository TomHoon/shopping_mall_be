package com.shopping.mall.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
	name = "role",
	indexes = {
		@Index(name = "idx_role_name", columnList = "roleName")
	}
)
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 권한 이름 (USER, SELLER, ADMIN)
	@Column(name = "role_name", nullable = false, unique = true, length = 50)
	private String roleName;

}