package com.eshare.smbj.demo;

import com.eshare.smbj.demo.common.Constant;
import com.eshare.smbj.demo.common.SmbjCommon;
import com.eshare.smbj.demo.utils.SmbFileUtils;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SmbjUploadDemo extends SmbjCommon {

    public static void main(String[] args) throws IOException {
        String remoteFolder = "/application/test/";
        String uploadFile = "test.txt";
        Session session = getSession(Constant.REMOTE_HOST, Constant.ACCOUNT, Constant.PSW, Constant.DOMAIN);
        //The name of the share to connect to
        try (DiskShare share = (DiskShare) session.connectShare("HK")) {
            java.io.File source = new java.io.File("c:/temp/" + uploadFile);
            //Upload file to remote share drive
            try {
                SmbFileUtils.upload(source, share, remoteFolder + uploadFile, true);
            } catch (Exception ex) {
                log.error("Error found when trying to upload file:", ex);
            }
        }
    }
}
