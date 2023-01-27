package ewha.gsd.midubang.jwt;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
    private String grantType;
    private String accessToken;
    private Long tokenExpiresIn;
    private String refreshToken;
}
