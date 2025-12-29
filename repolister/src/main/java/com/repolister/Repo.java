package com.repolister;

public record Repo(String name, String ownerLogin)
{
    public boolean isOwner(String userLogin) {
        return ownerLogin.equals(userLogin);
    }

    public record RepoBase(String name, Owner owner){

        public static Repo toRepo(RepoBase repoBase) {
            return new Repo(repoBase.name, repoBase.getOwnerLogin());
        }

        public String getOwnerLogin() {
            return owner.login;
        }

        private record Owner(String login){}
    }
}
