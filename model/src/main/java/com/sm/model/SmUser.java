package com.sm.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Accounts")
public class SmUser implements Serializable {
    @Id
    @Column(name = "ID_USER")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column(name = "V_LOGIN")
    private String login; // string whih wil be used as login

    @Column(name = "V_USERNAME")
    private String userName;  //- full userName will be shown in gui after login

    @Column(name = "V_AUTH_TYPE")
    private AuthType authType; //  - type of authrization (google, local, etc )

    @Column(name = "BL_PASSWORD")
    private byte[] passHash;// - hash of password will be used for local auth only

    @Column(name = "V_AUTH_LINK")
    private String authLink;  //- linke for remote authrization (if type of authrization is google)

    //private List<String> permissions; // some permissions

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public byte[] getPassHash() {
        return passHash;
    }

    public void setPassHash(byte[] passHash) {
        this.passHash = passHash;
    }

    public String getAuthLink() {
        return authLink;
    }

    public void setAuthLink(String authLink) {
        this.authLink = authLink;
    }

//    public List<String> getPermissions() {
//        return permissions;
//    }
//
//    public void setPermissions(List<String> permissions) {
//        this.permissions = permissions;
//    }

    public static enum AuthType {
        GOOGLE("Google authrization"),
        LOCAL("Local authrization");

        private String decription;

        AuthType(String decription) {
            this.decription = decription;
        }
    }
}
