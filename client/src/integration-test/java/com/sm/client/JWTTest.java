package com.sm.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sm.client.utils.JwtTokenUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLOutput;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JWTTest {
//    {
//        "alg": "HS256",
//            "kid": "F56BZ5WVSG",
//            "typ": "JWT"
//    }
    public static void main(String[] args) throws JsonProcessingException {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg","HS256");
        header.put("kid","F56BZ5WVSG");
        header.put("typ","JWT");

       // header.put("","");
        Map<String, Object> payload = new LinkedHashMap<>();
//        {
//            "iss": "8WZ9BH3AR3",
//                "iat": 1602168405,
//                "exp": 1619114078.719,
//                "origin": "http://localhost:4200"
//        }
        payload.put("iss","8WZ9BH3AR3");
        payload.put("iat",1602168405l);

        payload.put("origin","http://localhost:4200");
        payload.put("exp",new BigDecimal(1619114078.719).round(new MathContext(13)));
        System.out.println(new ObjectMapper().writeValueAsString(payload));
       String token =  Jwts.builder().setClaims(payload).setHeader(header)
            //   .setIssuedAt(new Date(1602168405L))
//                .setExpiration(new Date(System.currentTimeMillis() + 10000 * 1000))
                .signWith(SignatureAlgorithm.HS256, "F56BZ5WVSG".getBytes()).compact();

        System.out.println(token);

        //Object h = Jwts.parser().setSigningKey( "1234567").isSigned("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.QFHowH84hH4yWs9agTAs1xpas-lePeZore-hKMtzq2Y");
       // System.out.println(h);
    }
}
