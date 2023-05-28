package com.eshare.jcifs.example;

import com.eshare.smbj.common.Constant;
import jcifs.smb.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class JcifsDownloadDemo {

    public static void main(String[] args) throws IOException {
        String subFolder = "test/";
        String remoteHost = Constant.REMOTE_HOST;
        String account = Constant.ACCOUNT;
        String psw = Constant.PSW;
        String domain = Constant.DOMAIN;

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, account, psw);
        String smbUrl = "smb://" + remoteHost + "/LANdrive/" + subFolder;
        SmbFile smbFolder = new SmbFile(smbUrl, auth);

        SmbFile[] files = smbFolder.listFiles(new SmbFilenameFilter() {
            @Override
            public boolean accept(SmbFile dir, String name) throws SmbException {
                return !name.endsWith(".done");
            }
        });

        for (SmbFile file : files) {
            String fileName = file.getName();
            log.info("=======>File Name:{}", fileName);
            log.info("=======>File lastModifiedTime:{}", file.getLastModified());
            log.info("=======>Is file existed?:{}", file.exists());

            String destPath = "/Users/evan/Downloads/" + fileName;
            String newFileName = fileName + ".done";

            // Download file from share drive
            try (SmbFileInputStream sfis = new SmbFileInputStream(file);
                 FileOutputStream fos = new FileOutputStream(destPath)) {
                byte[] b = new byte[16 * 1024];
                int read;
                while ((read = sfis.read(b, 0, b.length)) > 0) {
                    fos.write(b, 0, read);
                }
                log.info("=======>File {} has been downloaded to {} successfully", fileName, destPath);
            }

            // Rename file after download successfully
            SmbFile newFile = new SmbFile(file.getParent() + newFileName, auth);
            file.renameTo(newFile);
            log.info("=======>File {} has been renamed to {} successfully", fileName, newFileName);
        }
    }
}
