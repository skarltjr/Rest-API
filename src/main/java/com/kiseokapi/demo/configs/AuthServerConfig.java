package com.kiseokapi.demo.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer //인증서버설정
@RequiredArgsConstructor
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
    // 인증 토큰을 발급받을 수 있도록 하는것이 목표
    //3개의 configure 구현

    //private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    // security설정에서 빈으로 등록해놓은것을 활용 - 유저정보를 갖고있는놈 -> 유저정보를 확인하고 토큰을 발급
    private final TokenStore tokenStore;
    private final AppProperties appProperties;


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //패스워드 인코더 설정
        security.passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //client설정

       // clients.jdbc(dataSource) // db를 활용
        clients.inMemory() // 테스트 연습을 위해 인메모리로
                .withClient(appProperties.getClientId()) // 어떤 client?
                .authorizedGrantTypes("password", "refresh_token")//grant타입은 패스워드 = 패스워드 인증타입. 즉 다른 sns를 통해서가 아니라 직접 비밀번호입력으로
                //refresh 토큰은 auth토큰을 발급받을 때 같이 발급받는데 refresh토큰으로 새로운 accesstoken을 발급받는 타입
                .scopes("read","write")
                .secret(passwordEncoder.encode(appProperties.getClientSecret())) // app(myApp)의 비밀번호
                .accessTokenValiditySeconds(10 * 60) // 10분
                .refreshTokenValiditySeconds(6 * 10 * 60);

        /** basic auth 이란
         api 서버에서 데이터을 요구할때 http Authorization 헤더에 user id 와 ,
         password 을 base64 로 인코딩한 문자열을 추가하여 인증하는 형식*/
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore);
        //엔드 포인트는 클라이언트 응용 프로그램에서 서비스에 액세스 할 수있는 URL
    }
}
