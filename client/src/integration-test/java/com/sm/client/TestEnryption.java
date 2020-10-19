package com.sm.client;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.registry.AlgorithmRegistry;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.salt.SaltGenerator;
import org.jasypt.salt.ZeroSaltGenerator;
import org.junit.Test;

import java.util.Base64;

public class TestEnryption {
    //    {
//        "iss": "8WZ9BH3AR3",
//            "iat": 1602168405,
//            "origin": "http://localhost:4200",
//            "exp": 1619114078.719
//    }
//{
//    "alg": "HS256",
//        "kid": "F56BZ5WVSG",
//        "typ": "JWT"
//}

    @Test
    public void testEnc() {
        String p = "3gkfwnxertqfusd4pfsez@a";
        System.out.println(AlgorithmRegistry.getAllPBEAlgorithms()); //PBEWithMD5AndDES
//        for(Object a:AlgorithmRegistry.getAllPBEAlgorithms()) {
//            System.out.println("--------------------------------------------------------------");
//            JasyptPBEStringEncryptionCLI.main(new String[]{"password=pass", "algorithm="+a, "input=513874746428-663v507o7i96n3p5vnm2l1e2tf2ldm09.apps.googleusercontent.com"}); //"algorithm=PBEWithMD5AndTripleDES",
//        }

        String s = "";
        JasyptPBEStringEncryptionCLI.main(new String[]{"password=" + p, "ivGeneratorClassName=" + RandomIvGenerator.class.getName(),
                "algorithm=PBEWITHSHA1ANDRC4_128",
                "input=" + s}); //"algorithm=PBEWithMD5AndTripleDES",


        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWITHSHA1ANDRC4_128");
        encryptor.setPassword(p);
        encryptor.setIvGenerator(new RandomIvGenerator());
        System.out.println("+++"+encryptor.encrypt(s));

        System.out.println(encryptor.decrypt("zWL9gRiKzMEick+CwhXYtQA6g0dwe26Zpq5eZvGoShDZreGj0b1PMNhGI1ATdD6GUdwnDeRMsrAJfPZn43HE"));
    }


    @Test
    public void test22() {
        StandardPBEByteEncryptor encryptor = new StandardPBEByteEncryptor();
        encryptor.setAlgorithm("PBEWITHSHA1ANDRC4_128");
        encryptor.setPassword("test");

        byte[] data = encryptor.encrypt("apps.googleusercontent.com".getBytes());

        System.out.println(new String(Base64.getEncoder().encode(data)));


        // byte[] decr= encryptor.decrypt(Base64.getDecoder().decode("xncnQ6CLhoDt+pIj82ujI1SZthmsogCAztbDvK5JS/H6tMqokV60UP2+HL5cYL2fjs2wh17FPeo1cfq30aLgp/3OyJ9OQxSZmPq0Wir1CCo="));

        byte[] decr = encryptor.decrypt(Base64.getDecoder().decode("f/rXszeO9g54XXwGcApSSG4KsqiRy4gsPeYQfHyEEkoauT+yJp8AigbqP+CMHq12F/b1nwizrNEaET+Ml/0i519KRIZ1Ts1P55gq+oXIc8s="));


        System.out.println(new String(decr));
    }

    @Test
    public void test33() {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setAlgorithm("PBEWITHSHA1ANDRC4_128");
        config.setPassword("test");
        config.setPoolSize(1);
        config.setStringOutputType("base64");
        // RandomSaltGenerator sault = new RandomSaltGenerator();
        // config.setSaltGenerator(sault);
        config.setIvGenerator(new RandomIvGenerator());
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(config);
        encryptor.initialize();
//        encryptor.setAlgorithm("PBEWITHSHA1ANDRC4_128");
//        encryptor.setPassword("pass");
//        encryptor.setPoolSize(1);

        String s = encryptor.encrypt(".apps.googleusercontent.com");
        System.out.println(s);
        System.out.println(encryptor.decrypt("penuoNmgcho5OwCC70WUKt5JmdxCksR69VZ8oO1eCFZd9md3LkMtGO4CY+3ZTTFWAvNGCzTBpxYLONS5yQQSNbhVKbMvQKACHgoscMRgLdoqDMU9MdyV8fCkuVG6GqPq"));
        // System.out.println(encryptor.decrypt("f/rXszeO9g54XXwGcApSSG4KsqiRy4gsPeYQfHyEEkoauT+yJp8AigbqP+CMHq12F/b1nwizrNEaET+Ml/0i519KRIZ1Ts1P55gq+oXIc8s="));
    }


}
