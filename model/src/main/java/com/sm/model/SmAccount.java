package com.sm.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Accounts")
public class SmAccount {

//    ID_ACCOUNT INTEGER Primary key AUTOINCREMENT,
//    V_LOGIN varchar(32),
//    DT_CREATED datetime,
//    V_EMAIL varchar(128),
//    V_FIRST_NAME varchar(64),
//    V_LAST_NAME varchar(64),
//    N_LIFE_TIME_CHARGE numeric(10),
//    B_DELETED numeric(1)

    @Id
    @Column(name = "ID_ACCOUNT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAccount;

    @Column(name = "V_LOGIN")
    private String login;

    @Column(name = "V_PASSWORD")
    private String password;


    @Column(name = "V_EMAIL")
    private String email;

    @Column(name = "V_FIRST_NAME")
    private String firstName;

    @Column(name = "V_LAST_NAME")
    private String lastName;

    @Column(name = "N_LIFE_TIME_CHARGE")
    private long lifeTimeCharge;

    @Column(name = "DT_CREATED")
    private Date dtCreated;

    @Column(name = "B_DELETED")
    private Boolean deleted;

    public Long getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(Long idAccount) {
        this.idAccount = idAccount;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getLifeTimeCharge() {
        return lifeTimeCharge;
    }

    public void setLifeTimeCharge(long lifeTimeCharge) {
        this.lifeTimeCharge = lifeTimeCharge;
    }

    public Date getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(Date dtCreated) {
        this.dtCreated = dtCreated;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
