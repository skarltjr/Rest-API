package com.kiseokapi.demo.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER) /**  Entity가 아닌 단순한 형태의 객체 집합을 정의하고 관리하는 방법이다.*/
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles = new HashSet<>();


}
