package com.repolister;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GitHubApiClient {
    private final RestClient restClient;
    public GitHubApiClient(@Value("${app.github-api-base-url}") String githubApiBaseUrl ) {
        this.restClient = RestClient.builder()
                .baseUrl(githubApiBaseUrl)
                .build();
    }

    public Repo.RepoBase[] getUserReposBase(String userLogin) {
        return restClient.get()
                .uri("/users/{userLogin}/repos", userLogin)
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND, ((req, res) -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND ,"Couldn't find user with login: " + userLogin);
                }))
                .body(Repo.RepoBase[].class);
    }


}
