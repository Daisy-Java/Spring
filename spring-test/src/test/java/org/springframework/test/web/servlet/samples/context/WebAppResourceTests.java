/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.web.servlet.samples.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests dependent on access to resources under the web application root directory.
 *
 * @author Rossen Stoyanchev
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration("src/test/resources/META-INF/web-resources")
@ContextHierarchy({
	@ContextConfiguration("root-context.xml"),
	@ContextConfiguration("servlet-context.xml")
})
public class WebAppResourceTests {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysExpect(status().isOk()).build();
	}

	// TilesConfigurer: resources under "/WEB-INF/**/tiles.xml"

	@Test
	public void tilesDefinitions() throws Exception {
		this.mockMvc.perform(get("/"))
			.andExpect(forwardedUrl("/WEB-INF/layouts/standardLayout.jsp"));
	}

	// Resources served via <mvc:resources/>

	@Test
	public void resourceRequest() throws Exception {
		this.mockMvc.perform(get("/resources/Spring.js"))
			.andExpect(content().contentType("application/javascript"))
			.andExpect(content().string(containsString("Spring={};")));
	}

	// Forwarded to the "default" servlet via <mvc:default-servlet-handler/>

	@Test
	public void resourcesViaDefaultServlet() throws Exception {
		this.mockMvc.perform(get("/unknown/resource"))
			.andExpect(handler().handlerType(DefaultServletHttpRequestHandler.class))
			.andExpect(forwardedUrl("default"));
	}

}
