package com.eshare.smbj.example;

import com.eshare.smbj.common.Constant;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFilenameFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JcifsHousekeepingDemo {

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
            public boolean accept(SmbFile dir, String name) {
                return name.endsWith(".done");
            }
        });

        for (SmbFile file : files) {
            String fileName = file.getName();
            log.info("=======>File Name:{}", fileName);
            log.info("=======>File lastModifiedTime:{}", file.getLastModified());
            log.info("=======>Is file existed?:{}", file.exists());

            // Remove ".done" file from share drive
            file.delete();
            log.info("=======>File {} has been removed successfully", fileName);
        }
    }
}
