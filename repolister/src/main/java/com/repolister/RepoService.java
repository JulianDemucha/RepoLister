package com.repolister;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

@Service
public class RepoService {
    private final GitHubApiClient apiClient;

    public RepoService(GitHubApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Repo[] getUserFilteredRepos(String userLogin) {
        Repo.RepoBase[] repos = apiClient.getUserReposBase(userLogin);

        return Arrays.stream(Objects.requireNonNull(repos))
                .filter(repo -> !repo.fork())
                .map(Repo.RepoBase::toRepo)
                .toArray(Repo[]::new);
    }
}
