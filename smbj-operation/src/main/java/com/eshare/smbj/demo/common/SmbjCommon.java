package com.eshare.smbj.demo.common;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;

import java.io.IOException;

public class SmbjCommon {

    /**
     * Get session
     *
     * @param remoteHost remote host
     * @param account    account
     * @param password   password
     * @param domain     account domain
     * @return SMB Session
     * @throws IOException
     */
    protected static Session getSession(String remoteHost, String account, String password, String domain) throws IOException {
        SmbConfig config = SmbConfig.builder()
                //automatically choose latest supported smb version
                .withMultiProtocolNegotiate(true)
                .withSigningRequired(true)
                //must enable
                .withEncryptData(true)
                .build();
        SMBClient client = new SMBClient(config);
        Connection conn = client.connect(remoteHost);
        AuthenticationContext ac = new AuthenticationContext(account, password.toCharArray(), domain);
        return conn.authenticate(ac);
    }
}
