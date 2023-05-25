package com.eshare.smbj.demo;

import com.eshare.smbj.demo.common.Constant;
import com.eshare.smbj.demo.common.SmbjCommon;
import com.eshare.smbj.demo.utils.SmbFileUtils;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class SmbjDownloadDemo extends SmbjCommon {

    public static void main(String[] args) throws IOException {
        String subFolder = "/application/test/";
        Session session = getSession(Constant.REMOTE_HOST, Constant.ACCOUNT, Constant.PSW, Constant.DOMAIN);
        //The name of the share to connect to
        try (DiskShare share = (DiskShare) session.connectShare("HK")) {
            List<FileIdBothDirectoryInformation> fileList = share.list(subFolder, "*.txt");
            for (int i = 0; i < fileList.size(); i++) {
                FileIdBothDirectoryInformation fileInfo = fileList.get(i);
                boolean isExisted = share.fileExists(subFolder + fileInfo.getFileName());
                if (!isExisted) {
                    log.info("File {} is not existed", fileInfo.getFileName());
                    continue;
                }
                log.info("=======>File Name:{}" , fileInfo.getFileName());
                log.info("=======>File lastModifiedTime:{}", fileInfo.getLastWriteTime());
                log.info("=======>Is file existed?:{}", isExisted);

                String remotePath = subFolder + fileInfo.getFileName();
                String destPath = "c:/temp/" + fileInfo.getFileName();
                String newFileName = subFolder + fileInfo.getFileName() + ".done";

                //Download file from share drive
                SmbFileUtils.download(remotePath,share,destPath);
                //Rename file after download successfully
                SmbFileUtils.rename(remotePath,share,newFileName,true);
            }
        }
    }
}
