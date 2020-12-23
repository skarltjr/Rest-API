package com.kiseokapi.demo.accounts;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUserName() {
        //given
        Account account = Account.builder()
                .email("kisa0828@naver.com")
                .password("123456789")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.saveAccount(account);
        //when
        UserDetailsService accountService = this.accountService;
        UserDetails kiseok = accountService.loadUserByUsername(account.getEmail());

        //then
        assertThat(passwordEncoder.matches("123456789", kiseok.getPassword()));
    }

    @Test
    @Description("loadUserByUsername으로 불러오기 실패했을 때")
    public void findByUserNameFail() {
        String email = "아무거나@naver.com";
        try {
            accountService.loadUserByUsername(email);
            fail("계정찾기를 실패했습니다");
        } catch (UsernameNotFoundException e) {
            assertThat(e.getMessage()).containsSequence(email);
        }
    }
}