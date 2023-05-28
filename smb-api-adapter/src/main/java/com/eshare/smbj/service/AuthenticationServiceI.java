package com.eshare.smbj.service;

import com.hierynomus.smbj.session.Session;

public interface AuthenticationServiceI {


    Session connectAndVerify(String remoteHost, String account, String password, String domain);

}
