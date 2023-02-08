package ewha.gsd.midubang.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="member")
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_id;

    @Column(length = 50, unique = true)
    private String email;

    @Column(length = 200)
    private String password; // null for kakao login

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Record> recordList = new ArrayList<>();



    @Builder
    public Member(String email, String password){
        this.email=email;
        this.password=password;
    }

    //refreshToken db에 저장할 경우 관련 코드 추가


//    public List<String> getRoleList() {
//        if(this.roles.length()>0) {
//            return Arrays.asList(this.roles.split(","));
//        }
//        return new ArrayList<>();
//    }

}
