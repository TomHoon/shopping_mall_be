package com.shopping.mall.guide;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GuideUserControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	void createUser() throws Exception {
		String body = """
        {
          "name": "tom",
          "email": "tom@test.com"
        }
        """;

		mockMvc.perform(post("/api/guide/users")
										.contentType(MediaType.APPLICATION_JSON)
										.content(body))
						.andDo(result -> {
							System.out.println("TEST 결과 : ");
							System.out.println(result.getResponse().getContentAsString());
						})
						.andDo(print())
						.andExpect(status().isOk());
	}
}
