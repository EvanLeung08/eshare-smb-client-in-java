package com.eshare.smbj.demo.common;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;

import java.io.IOException;

public class SmbjCommon {

    private static Session session = null;


    protected static Session getSession(String remoteHost, String account, String password, String domain) throws IOException {
        if (session == null) {
            SmbConfig config = SmbConfig.builder()
                    //automatically choose latest supported smb version
                    .withMultiProtocolNegotiate(true)
                    .withSigningRequired(true)
                    //must enable
                    .withEncryptData(true)
                    .build();
            SMBClient client = new SMBClient(config);
            try (Connection conn = client.connect(remoteHost)) {
                AuthenticationContext ac = new AuthenticationContext(account, password.toCharArray(), domain);
                session = conn.authenticate(ac);
            }

        }
        return session;
    }
}
