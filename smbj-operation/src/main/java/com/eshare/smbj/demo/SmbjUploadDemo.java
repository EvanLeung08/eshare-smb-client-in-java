package com.eshare.smbj.demo;

import com.eshare.smbj.demo.common.Constant;
import com.eshare.smbj.demo.common.SmbjCommon;
import com.eshare.smbj.demo.utils.SmbFileUtils;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SmbjUploadDemo extends SmbjCommon {

    public static void main(String[] args) throws IOException {
        String remoteFolder = "test/";
        String uploadFile = "test.jpeg";
        Connection conn = null;
        //The name of the share to connect to
        try (
                Session session = getSession(Constant.REMOTE_HOST, Constant.ACCOUNT, Constant.PSW, Constant.DOMAIN);
                DiskShare share = (DiskShare) session.connectShare("LANdrive")
        ) {
            java.io.File source = new java.io.File("/Users/evan/Downloads/" + uploadFile);
            //Upload file to remote share drive
            try {
                SmbFileUtils.upload(source, share, remoteFolder + uploadFile, true);
                log.info("=======>File {} has been upload to {} successfully", uploadFile, "share" + remoteFolder);
            } catch (Exception ex) {
                log.error("Error found when trying to upload file:", ex);
            }
            //Close connection in the end
            conn = session.getConnection();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
