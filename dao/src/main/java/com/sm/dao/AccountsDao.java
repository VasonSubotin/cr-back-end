package com.sm.dao;

import com.sm.model.SmAccount;

import javax.transaction.Transactional;
import java.util.List;

public interface AccountsDao {
    List<SmAccount> getAllAccounts();

    SmAccount getAccountByLogin(String login);

    SmAccount getAccountById(Long id);

    SmAccount saveAccount(SmAccount smAccount);

    @Transactional
    SmAccount deleteAccountById(Long id);
}
