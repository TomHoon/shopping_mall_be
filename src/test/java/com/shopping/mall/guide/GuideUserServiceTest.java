package com.shopping.mall.guide;

import com.shopping.mall.guide.entity.GuideUser;
import com.shopping.mall.guide.repository.GuideUserRepository;
import com.shopping.mall.guide.service.GuideUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuideUserServiceTest {

	@Mock
	GuideUserRepository guideUserRepository;

	@InjectMocks
	GuideUserService guideUserService;

	@Test
	void createUser() {

		GuideUser user = new GuideUser("tom", "tom@test.com");

		given(guideUserRepository.save(any(GuideUser.class)))
						.willReturn(user);

		Long id = guideUserService.createUser("tom", "tom@test.com");

		then(guideUserRepository).should().save(any(GuideUser.class));
	}
}