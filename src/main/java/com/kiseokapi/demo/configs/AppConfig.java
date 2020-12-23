package com.kiseokapi.demo.configs;

import com.kiseokapi.demo.accounts.Account;
import com.kiseokapi.demo.accounts.AccountRole;
import com.kiseokapi.demo.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // 애플리케이션 띄울 때 미리 유저하나 생성해보기
    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired AccountService accountService;


            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account kiseok = Account.builder()
                        .email("kisa0828@naver.com")
                        .password("123456789")
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();
                accountService.saveAccount(kiseok);
            }
        };
    }

}