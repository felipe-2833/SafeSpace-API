package br.com.fiap.safespace.model;

public record Token(
    String token,
    Long expiration,
    String type,
    String role
) {

}
