package com.eshare.smbj.example;

import com.eshare.smbj.common.Constant;
import com.eshare.smbj.utils.SmbFileUtils;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SmbjDownloadDemo {

    public static void main(String[] args) throws IOException {
        String subFolder = "test/";
        Connection conn = null;
        //The name of the share to connect to
        try (
                Session session = SmbFileUtils.getSession(Constant.REMOTE_HOST, Constant.ACCOUNT, Constant.PSW, Constant.DOMAIN);
                DiskShare share = (DiskShare) session.connectShare("LANdrive")

        ) {
            List<FileIdBothDirectoryInformation> fileList = share.list(subFolder);
            for (int i = 0; i < fileList.size(); i++) {
                FileIdBothDirectoryInformation fileInfo = fileList.get(i);


                boolean isExisted = isFileExisted(subFolder, share, fileInfo);
                if (!isExisted) {
                    log.info("File {} is invalid", fileInfo.getFileName());
                    continue;
                }
                log.info("=======>File Name:{}", fileInfo.getFileName());
                log.info("=======>File lastModifiedTime:{}", fileInfo.getLastWriteTime());
                log.info("=======>Is file existed?:{}", isExisted);

                String remotePath = subFolder + fileInfo.getFileName();
                String destPath = "/Users/evan/Downloads/" + fileInfo.getFileName();
                String newFileName = subFolder + fileInfo.getFileName() + ".done";

                //Download file from share drive
                SmbFileUtils.download(remotePath, share, destPath);
                //Rename file after download successfully
                SmbFileUtils.rename(remotePath, share, newFileName, true);
                log.info("=======>File {} has been downloaded to {} successfully", fileInfo.getFileName(), destPath);

            }
            //Close connection in the end
            conn = session.getConnection();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private static boolean isFileExisted(String subFolder, DiskShare share, FileIdBothDirectoryInformation fileInfo) {
        boolean flag ;
        try {
            flag = share.fileExists(subFolder + fileInfo.getFileName());
        } catch (Exception ex) {
            log.debug("Exception found :", ex);
            flag = false;
        }
        return flag;
    }
}
