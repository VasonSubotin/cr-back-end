package com.sm.client.mvc;

import com.sm.client.services.SecurityService;
import com.sm.client.services.UserDetailsServiceImpl;
import com.sm.dao.AccountsDao;
import com.sm.dao.CommonDao;
import com.sm.model.SmAccount;
import com.sm.model.SmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
public class AccountController {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AccountsDao accountsDao;

    @RequestMapping(value = "/accountInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmAccount getSelfUser(HttpServletRequest request) throws Exception {
        SmAccount smAccount = securityService.getAccount();
        SmAccount ret = new SmAccount();
        ret.setEmail(smAccount.getEmail());
        ret.setFirstName(smAccount.getFirstName());
        ret.setLastName(smAccount.getLastName());
        // ret.setPassword(smAccount.getPassword());
        ret.setLogin(smAccount.getLogin());
        ret.setIdAccount(smAccount.getIdAccount());
        ret.setLifeTimeCharge(smAccount.getLifeTimeCharge());
        ret.setDtCreated(smAccount.getDtCreated());

        return ret;
    }

    @RequestMapping(value = "/accountInfo", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmAccount deleteSelfUser(HttpServletRequest request) throws Exception {
        return accountsDao.deleteAccountById(securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/accountInfo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmAccount saveAccount(HttpServletRequest request, @RequestBody SmAccount smAccount) throws Exception {
        smAccount.setLogin(securityService.getAccount().getLogin());
        return userDetailsService.updateUser(smAccount);
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
