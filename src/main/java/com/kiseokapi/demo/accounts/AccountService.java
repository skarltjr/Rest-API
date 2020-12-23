package com.kiseokapi.demo.accounts;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Account saveAccount(Account account) {
        String password = account.getPassword();
        account.setPassword(passwordEncoder.encode(password));
        return accountRepository.save(account);
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException(email));
        return new UserAccount(account);
    }

}
