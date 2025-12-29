package com.repolister;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    private final RepoService repoService;

    public ApiController(RepoService repoService) {
        this.repoService = repoService;
    }

    @GetMapping("/repos/{userLogin}")
    public ResponseEntity<Repo[]> getUserRepos(@PathVariable String userLogin) {
        return ResponseEntity.ok(repoService.getUserFilteredRepos(userLogin));
    }
}
