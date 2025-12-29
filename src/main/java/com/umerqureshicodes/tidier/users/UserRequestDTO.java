package com.umerqureshicodes.tidier.users;

public record UserRequestDTO (
        String username,
        String password,
        String email
) {

}
