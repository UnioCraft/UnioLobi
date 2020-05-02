package me.uniodex.uniolobi.utils.packages.pool;

public class PoolCredentialPackage {

    private String username;
    private String password;

    public PoolCredentialPackage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
