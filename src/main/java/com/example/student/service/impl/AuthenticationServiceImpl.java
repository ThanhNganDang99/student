package com.example.student.service.impl;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.example.student.dto.request.AuthenticationDtoRequest;
import com.example.student.dto.request.IntrospectRequestDto;
import com.example.student.dto.response.AuthenticationDtoResponse;
import com.example.student.dto.response.IntrospectResponseDto;
import com.example.student.entity.InvalidatedToken;
import com.example.student.entity.User;
import com.example.student.exception.CustomException;
import com.example.student.exception.ErrorCode;
import com.example.student.repository.InvalidatedTokenRepository;
import com.example.student.repository.UserRepository;
import com.example.student.service.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
// dùng này thì bỏ Autowired
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;


    // generate Encryption keys link: https://generate-random.org/encryption-key-generator?count=1&bytes=32&cipher=aes-256-cbc&string=&password=
    @NonFinal
    @Value("${jwt.signerkey}") // @value dùng để đọc biến từ yml
    protected String SIGNER_KEY ;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected  long VALID_DURATION;


    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected  long REFRESH_DURATION;

    @Override
    public AuthenticationDtoResponse authenticate(AuthenticationDtoRequest request) {
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated)
            throw new CustomException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);

        return AuthenticationDtoResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    // check coi token có hợp lệ và hết hạn chưa
    @Override
    public IntrospectResponseDto introspect(IntrospectRequestDto requestDto) throws JOSEException, ParseException {
        var token = requestDto.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (CustomException e) {
            isValid = false;
        }
        return IntrospectResponseDto.builder()
                .valid(isValid)
                .build();
    }

    @Override
    public void logout(IntrospectRequestDto requestDto) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(requestDto.getToken(), true);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (CustomException e) {
            log.info("Token already expired");
        }

    }

    @Override
    public AuthenticationDtoResponse refreshToken(IntrospectRequestDto requestDto) throws JOSEException, ParseException {
        var signedJWT = verifyToken(requestDto.getToken(), true);

        var jit= signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .expiryTime(expiryTime)
                .id(jit)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
        var username= signedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHENTICATED));
        var token = generateToken(user);
        return  AuthenticationDtoResponse.builder()
                .authenticated(true)
                .token(token)
                .build();
    }

    /*  JWT sẽ có phần
    + header
    + payload
    + signature dùng để ký token
    vào trang web phân tích token jwt.io
    */
    private String generateToken(User user) {
        //header muốn sử dụng thuật toán gì
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        //claims -> payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("student.com") // thường là domain hoặc đăt gì cũng được
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                )) // thời gian hết hạn
                .jwtID(UUID.randomUUID().toString())
                .claim("scope",buildScope(user))
                .build();
        //create payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject. sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token ", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
            if (!CollectionUtils.isEmpty(user.getRoles()))
                user.getRoles().forEach(role -> {
                    stringJoiner.add("ROLE_" + role.getName());
                    if (!CollectionUtils.isEmpty(role.getPermission()))
                        role.getPermission().forEach(permission -> stringJoiner.add(permission.getName()));
                });
        return stringJoiner.toString();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                // tìm hiểu ChronoUnit về Date => hay
                // lấy ngày IsssueTime + REFRESH_DURATION
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                .plus(REFRESH_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) throw new CustomException(ErrorCode.UNAUTHENTICATED);
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new CustomException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;
    }
}
