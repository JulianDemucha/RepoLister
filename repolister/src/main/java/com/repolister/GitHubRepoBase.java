package com.repolister;

public record GitHubRepoBase(String name, Owner owner)
{
    public boolean isOwner(String userLogin) {
        return getOwnerLogin().equals(userLogin);
    }

    private record Owner(String login){}

    public String getOwnerLogin() {
        return owner.login;
    }
}
