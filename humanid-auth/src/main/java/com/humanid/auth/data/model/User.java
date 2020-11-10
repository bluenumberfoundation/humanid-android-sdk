package com.humanid.auth.data.model;

import androidx.annotation.NonNull;

public class User {

    private String userHash;

    private String exchangeToken;

    public User(@NonNull String userHash, @NonNull String exchangeToken) {
        this.userHash = userHash;
        this.exchangeToken = exchangeToken;
    }

    @NonNull
    public String getUserHash() {
        return userHash;
    }

    @NonNull
    public String getExchangeToken() {
        return exchangeToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return userHash.equals(that.userHash);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (userHash == null ? 0 : userHash.hashCode());

        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userHash='" + userHash + '\'' +
                "exchangeToken='" + exchangeToken +
                '}';
    }
}
