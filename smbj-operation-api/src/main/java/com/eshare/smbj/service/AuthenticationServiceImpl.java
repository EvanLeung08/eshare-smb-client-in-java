package com.eshare.smbj.service;

import com.eshare.smbj.common.AuthenticationException;
import com.eshare.smbj.common.Constant;
import com.eshare.smbj.serviceI.AuthenticationServiceI;
import com.eshare.smbj.utils.SmbFileUtils;
import com.hierynomus.smbj.session.Session;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationServiceI {


    @Override
    public Session connectAndVerify(String remoteHost, String account, String password, String domain) {
        Session session;
        try {
            session = SmbFileUtils.getSession(Constant.REMOTE_HOST, Constant.ACCOUNT, Constant.PSW, Constant.DOMAIN);
        } catch (Exception e) {
            throw new AuthenticationException("Failed to connect remote folder through SMB protocol", e);
        }
        return session;
    }
}
