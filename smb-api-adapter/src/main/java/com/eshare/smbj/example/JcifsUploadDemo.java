package com.eshare.smbj.example;

import com.eshare.smbj.common.Constant;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
public class JcifsUploadDemo {

    public static void main(String[] args) throws IOException {
        String remoteFolder = "test/";
        String uploadFile = "test.jpeg";
        String remoteHost = Constant.REMOTE_HOST;
        String account = Constant.ACCOUNT;
        String psw = Constant.PSW;
        String domain = Constant.DOMAIN;

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, account, psw);
        String smbUrl = "smb://" + remoteHost + "/LANdrive/" + remoteFolder + uploadFile;
        SmbFile smbFile = new SmbFile(smbUrl, auth);
        File source = new File("/Users/evan/Downloads/" + uploadFile);

        try (FileInputStream fis = new FileInputStream(source);
             SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFile)) {

            final byte[] b = new byte[16 * 1024];
            int read;
            while ((read = fis.read(b, 0, b.length)) > 0) {
                smbfos.write(b, 0, read);
            }
            log.info("=======>File {} has been upload to {} successfully", uploadFile, "share" + remoteFolder);
        } catch (Exception ex) {
            log.error("Error found when trying to upload file:", ex);
        }
    }
}