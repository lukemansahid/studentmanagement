package com.lukish.studentmanagement.security.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lukish.studentmanagement.domain.UserPrincipal;
//import com.lukish.studentmanagement.security.constant.SecurityConstant;
import static com.lukish.studentmanagement.security.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    //this method returns a Json Web Token
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

        //Create the token with the following :
        // token provider, the Audience, issued date, the unique identity of user (Subject),
        // the list of claims or authorities,token time to be expire,
        // and sign the token with an algorithm with the secret key
    }

    // this method return a list of authorities from the authenticated user.
    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {

        List<String> authorities = new ArrayList<>();

     //   GrantedAuthority auths = (GrantedAuthority) userPrincipal.getAuthorities();

        for(GrantedAuthority grantedAuthority : userPrincipal.getAuthorities()){
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }


    public List<GrantedAuthority> getAuthorities(String jwtToken){

        String[] claimsFromToken = this.getClaimsFromToken(jwtToken); //get claims

        return stream(claimsFromToken).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    // this method return claims from the token
    private String[] getClaimsFromToken(String jwtToken) {

        JWTVerifier verifier = this.getJWTVerifier();

        return verifier.verify(jwtToken).getClaim(AUTHORITIES).asArray(String.class);
    }

    // this method verifies the token
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
