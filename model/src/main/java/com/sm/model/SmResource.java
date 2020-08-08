package com.sm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Resources")
public class SmResource implements Serializable {

    @Id
    @Column(name = "ID_RESOURCE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idResource;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "V_EXTERNAL_RESOURCE_ID")
    private String externalResourceId;

    @Column(name = "V_VENDOR")
    private String vendor;

    @Column(name = "V_MODEL")
    private String model;

    @Column(name = "POLICY_ID")
    private Long policyId;

    @Column(name = "RESOURCE_TYPE_ID")
    private Long resourceTypeId;

    @Column(name = "V_GROUP_ID")
    private String groupId;

    @Column(name = "N_POWER")
    private Long power;

    @Column(name = "N_CAPACITY")
    private Long capacity;

    @JsonProperty("chargeby_time")
    @Column(name = "N_CHARGE_BY_TIME")
    private Long nChargeByTime;

    @Column(name = "DT_UPDATED")
    private Date dtUpdated;

    @Column(name = "DT_CREATED")
    private Date dtCreated;

    @Column(name = "B_DELETED")
    private Boolean deleted;

    public Long getIdResource() {
        return idResource;
    }

    public void setIdResource(Long idResource) {
        this.idResource = idResource;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getExternalResourceId() {
        return externalResourceId;
    }

    public void setExternalResourceId(String externalResourceId) {
        this.externalResourceId = externalResourceId;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Long getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(Long resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Long getPower() {
        return power;
    }

    public void setPower(Long power) {
        this.power = power;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Date getDtUpdated() {
        return dtUpdated;
    }

    public void setDtUpdated(Date dtUpdated) {
        this.dtUpdated = dtUpdated;
    }

    public Date getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(Date dtCreated) {
        this.dtCreated = dtCreated;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public Long getnChargeByTime() {
        return nChargeByTime;
    }

    public void setnChargeByTime(Long nChargeByTime) {
        this.nChargeByTime = nChargeByTime;
    }
}
