package com.nasa.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NasaApiApplicationTests {

	@Test
	public void contextLoads() throws Exception {
		findTop10NearestAsteroidByDate();
	}

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	private void performAndVerify(String url) throws Exception {
		MvcResult result = mockMvc.perform(get(url)
				.contentType("application/json;charset=UTF-8")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		Assert.assertNotNull(result);
	}

	public void findTop10NearestAsteroidByDate() throws Exception {
		String NASA_FEED_URL_FORMAT = "/api/nasa/top-10-nearest-asteroids?start-date=%s&end-date=%s";
		String nasaFeedUrl = String.format(NASA_FEED_URL_FORMAT, "2015-09-07", "2015-09-08");

		performAndVerify(nasaFeedUrl);
	}
}
