package com.shopping.mall.user.entity;

import com.shopping.mall.user.dto.UserProfileUpdateRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(
	name = "user",
	indexes = {
		@Index(name = "idx_user_email", columnList = "email")
	}
)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus status;

    @CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

    @LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

    public void updateUser(UserProfileUpdateRequestDto requestDto) {
        if (requestDto.name() != null) this.name = requestDto.name();
    }

    public void deleteUser() {
        this.status = UserStatus.INACTIVE;
    }
}

