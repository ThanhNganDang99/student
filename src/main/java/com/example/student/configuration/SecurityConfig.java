package com.example.student.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // để phân quyền theo method
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINT={"/users","/auth/log-in","/auth/introspect","/auth/logout","/auth/refresh"};

    @Autowired
    private CustomJwtDecoder customJwtDecoder;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll()
                        .requestMatchers(HttpMethod.GET,"/users/get")
                        .hasAuthority("ROLE_ADMIN") // ẩn  để test anotation security
//                        .hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated());

        httpSecurity.oauth2ResourceServer(oath2 ->
                oath2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        //hadel error exception trả về => trường hợp error của auth nên sẽ tạo class JwtAuthenticationEntrypoint để handle
                        .authenticationEntryPoint(new JwtAuthenticationEntrypoint())
        );

        //tắt tính năng csrf
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return  httpSecurity.build();
    }

   /* @Bean
    JwtDecoder jwtDecoder(){
        SecretKey secretKey=new SecretKeySpec(signerkey.getBytes(),"HS512");
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }*/


    //convert SCOPE thành ROLE
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter= new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    // khi tạo nó là bean thì nó được lưu vào contex được sử dụng ở nhiều nơi
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
