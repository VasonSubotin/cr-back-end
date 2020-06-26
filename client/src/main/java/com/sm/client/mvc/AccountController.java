package com.sm.client.mvc;

import com.sm.client.services.SecurityService;
import com.sm.dao.AccountsDao;
import com.sm.dao.CommonDao;
import com.sm.model.SmAccount;
import com.sm.model.SmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class AccountController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AccountsDao accountsDao;

    @RequestMapping(value = "/accountInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmAccount getSelfUser(HttpServletRequest request) throws Exception {
        return securityService.getAccount();
    }

    @RequestMapping(value = "/accountInfo", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmAccount deleteSelfUser(HttpServletRequest request) throws Exception {
        return accountsDao.deleteAccountById(securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/accountInfo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmAccount saveAccount(HttpServletRequest request, @RequestBody SmAccount smAccount) throws Exception {
        //updating account
        SmAccount current = securityService.getAccount();
        current.setEmail(smAccount.getEmail());
        current.setFirstName(smAccount.getFirstName());
        current.setLastName(smAccount.getLastName());
        current.setPassword(smAccount.getPassword());
        return accountsDao.saveAccount(current);
    }

    @RequestMapping(value = "/accounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmAccount> getUserAccounts(HttpServletRequest request) throws Exception {
        return accountsDao.getAllAccounts();
    }

    @RequestMapping(value = "/accounts/{account_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmAccount getUserAccount(HttpServletRequest request, @PathVariable("account_id") long accountId) throws Exception {
        return accountsDao.getAccountById(accountId);
    }
}
