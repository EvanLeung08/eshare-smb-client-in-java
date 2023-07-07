package com.eshare.smbj.service.impl;

import com.eshare.smbj.common.exception.AuthenticationException;
import com.eshare.smbj.common.Constant;
import com.eshare.smbj.service.AuthenticationServiceI;
import com.eshare.smbj.common.utils.SmbFileUtils;
import com.hierynomus.smbj.session.Session;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationServiceI {


    @Override
    public Session connectAndVerify(String remoteHost, String account, String password, String domain) {
        Session session;
        try {
            session = SmbFileUtils.getSession(remoteHost, account, password, domain);
        } catch (Exception e) {
            throw new AuthenticationException("Failed to connect remote folder through SMB protocol", e);
        }
        return session;
    }
}
