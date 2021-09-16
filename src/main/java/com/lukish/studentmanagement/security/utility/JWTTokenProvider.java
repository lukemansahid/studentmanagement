package com.lukish.studentmanagement.security.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lukish.studentmanagement.domain.UserPrincipal;
//import com.lukish.studentmanagement.security.constant.SecurityConstant;
import static com.lukish.studentmanagement.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    /*Create the token with the following : token provider, the Audience, issued date,
    the unique identity of user (Subject), the list of claims or authorities,
    token time to expire, and sign the token with an algorithm with the secret key */

    //this method create and returns a Json Web Token
    public String generateJwtToken(UserPrincipal userPrincipal){

        // get the claims or authorities first before generate token
        String[] claims = this.getClaimsFromUser(userPrincipal);

        return JWT.create()
                .withIssuer(TOKEN_PROVIDER)
                .withAudience(TOKEN_FOR_ADMIN)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES,claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(String jwtToken){

        String[] claimsFromToken = this.getClaimsFromToken(jwtToken); //get claims

        return stream(claimsFromToken).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    // this method authenticate the user
    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPasswordAuthToken = new
                UsernamePasswordAuthenticationToken(username, null, authorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }

    public boolean isTokenValid(String username, String token) {
        com.auth0.jwt.interfaces.JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
    }

    public String getSubject(String token) {
        com.auth0.jwt.interfaces.JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    private boolean isTokenExpired(com.auth0.jwt.interfaces.JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    // A helper method that return a list of authorities from the authenticated user.
    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {

        List<String> authorities = new ArrayList<>();

        for(GrantedAuthority grantedAuthority : userPrincipal.getAuthorities()){
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }

    // A helper method that return claims from the token
    private String[] getClaimsFromToken(String jwtToken) {

        JWTVerifier verifier = this.getJWTVerifier();

        return verifier.verify(jwtToken).getClaim(AUTHORITIES).asArray(String.class);
    }

    // helper method that verifies the token
    private JWTVerifier getJWTVerifier() {

        JWTVerifier verifier;

        try{
            Algorithm algorithm = Algorithm.HMAC512(secret);

            verifier = JWT.require(algorithm).withIssuer(TOKEN_PROVIDER).build();

        }catch (JWTVerificationException e){
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }
}
