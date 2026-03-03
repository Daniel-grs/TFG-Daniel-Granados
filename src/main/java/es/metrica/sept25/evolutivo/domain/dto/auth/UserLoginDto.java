package es.metrica.sept25.evolutivo.domain.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserLoginDto", description = "Login payload containing user (email) and password")
public class UserLoginDto {

    @Schema(description = "User email", example = "user@example.com")
    private String user;

    @Schema(description = "User password", example = "P4ssword123")
    private String password;

    public UserLoginDto() {
    }

    public UserLoginDto(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserLoginDto [user=" + user + ", password=PROTECTED]";
    }
}
