package com.sm.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "UserSessions")
public class SmUserSession implements Serializable {

    @Id
    @Column(name = "ID_USER_SESSION")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUserSession;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "V_TOKEN")
    private String token;

    @Column(name = "V_REFRESH_TOKEN")
    private String refreshToken;

    @Column(name = "V_SESSION_TYPE")
    private String sessionType;

    @Column(name = "N_TTL")
    private Long ttl;


    @Column(name = "DT_CREATED")
    private Date dtCreated;

    @Column(name = "B_CLOSED")
    private Boolean closed;

    public Long getIdUserSession() {
        return idUserSession;
    }

    public void setIdUserSession(Long idUserSession) {
        this.idUserSession = idUserSession;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public Date getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(Date dtCreated) {
        this.dtCreated = dtCreated;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public Boolean getClosed() {
        return closed == null ? false : closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }
}
