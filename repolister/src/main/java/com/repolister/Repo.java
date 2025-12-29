package com.repolister;

public record Repo(String name, String ownerLogin)
{

    public record RepoBase(String name, Owner owner, boolean fork){

        public static Repo toRepo(RepoBase repoBase) {
            return new Repo(repoBase.name, repoBase.getOwnerLogin());
        }

        private String getOwnerLogin() {
            return owner.login;
        }

        private record Owner(String login){}
    }
}
