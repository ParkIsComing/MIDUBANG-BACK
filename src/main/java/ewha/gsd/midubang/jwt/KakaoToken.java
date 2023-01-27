package ewha.gsd.midubang.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class KakaoToken {
    private String access_token;
    private String refresh_token;
    private String token_type;

    private int expires_in;

    private String scope;
    private int refresh_token_expires_in;




    @Builder
    public KakaoToken(String access_token, String refresh_token, String token_type,int expires_in, String scope,int refresh_token_expires_in){
        this.access_token =access_token;
        this.refresh_token = refresh_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.scope = scope;
        this.refresh_token_expires_in = refresh_token_expires_in;

    }

}
