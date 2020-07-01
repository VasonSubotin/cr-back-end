package com.sm.dao;

import com.sm.model.SmAccount;

import javax.transaction.Transactional;
import java.util.List;

public interface AccountsDao {
    List<SmAccount> getAllAccounts();

    SmAccount getAccountByLogin(String login);

    SmAccount getAccountById(Long id);

    SmAccount saveAccount(SmAccount smAccount);

    @org.springframework.transaction.annotation.Transactional(readOnly = false)
    SmAccount updateAccount(SmAccount smAccount);

    @Transactional
    SmAccount deleteAccountById(Long id);
}
