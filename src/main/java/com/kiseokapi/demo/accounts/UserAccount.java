package com.kiseokapi.demo.accounts;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Set;

@Getter
public class UserAccount extends User {
    private Account account;

    public UserAccount(Account account) {
        super(account.getEmail(), account.getPassword(),Set.of(new SimpleGrantedAuthority("ROLE_"+account.getRoles())));
        this.account = account;
    }


}
