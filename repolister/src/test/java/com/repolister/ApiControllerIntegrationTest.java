package com.repolister;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ApiControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance().build();

    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("app.github-api-base-url", wireMockServer::baseUrl);
    }

    @Test
    void shouldGetReposAndFilterOutForkedReposSuccessfully() throws Exception {
        String userLogin = "user";
        String repoName1 = "repo1";
        String repoName2 = "repo2";
        String repoName3 = "repo3";

        wireMockServer.stubFor(WireMock.get("/users/"+userLogin+"/repos").willReturn(
                WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {
                                    "name": "%s",
                                    "owner":{
                                        "login": "%s"
                                    },
                                    "fork" : false
                                    },
                                
                                    {
                                    "name": "%s",
                                    "owner":{
                                        "login": "%s"
                                    },
                                    "fork" : false
                                    },
                                
                                    {
                                    "name": "%s",
                                    "owner":{
                                        "login": "%s"
                                    },
                                    "fork" : true
                                    }
                                ]
                                """.formatted(repoName1, userLogin, repoName2, userLogin, repoName3, userLogin)
                        )
        ));

        this.mvc.perform(get("/repos/{userLogin}", userLogin))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(repoName1)))
                .andExpect(jsonPath("$[0].ownerLogin", is(userLogin)))
                .andExpect(jsonPath("$[1].name", is(repoName2)))
                .andExpect(jsonPath("$[1].ownerLogin", is(userLogin)))
                .andExpect(jsonPath("$[2]").doesNotExist());
    }

    @Test
    void shouldReturn404WithStatusAndMessageWhenUserDoesNotExist() throws Exception {
        String userLogin = "user";
        String notFoundMessage = "Couldn't find user with login: " + userLogin;

        wireMockServer.stubFor(WireMock.get("/users/"+userLogin+"/repos").willReturn(
                WireMock.aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                "message" : "Not Found",
                                "status": "404"
                                }
                                """
                        )
        ));

        this.mvc.perform(get("/repos/{userLogin}", userLogin))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")));
    }



}
