package com.eshare.smbj.example;

import com.eshare.smbj.common.Constant;
import com.eshare.smbj.common.utils.SmbFileUtils;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class SmbFolderCreationDemo {

    public static void main(String[] args) throws IOException {
        String remoteFolder = "test/";
        String newFolder = "new";
        Connection conn = null;
        //The name of the share to connect to
        try (
                Session session = SmbFileUtils.getSession(Constant.REMOTE_HOST, Constant.ACCOUNT, Constant.PSW, Constant.DOMAIN);
                DiskShare share = (DiskShare) session.connectShare("LANdrive")
        ) {

            //create a new folder

            try {
                boolean isFolderCreated = createNewFolder(remoteFolder, newFolder, share);
                log.info("Folder:{} is created?{}", newFolder, isFolderCreated);

            } catch (Exception ex) {
                log.error("Error found when trying to create folder:", ex);
            }
            //Close connection in the end
            conn = session.getConnection();
        } finally {
            if (conn != null) {
                conn.close(true);
            }
        }
    }

    private static boolean createNewFolder(String remoteFolder, String newFolder, DiskShare share) {
        boolean isFolderExisted = false;
        List<FileIdBothDirectoryInformation> fileList = share.list(remoteFolder, "*");
        for (int i = 0; i < fileList.size(); i++) {
            FileIdBothDirectoryInformation fileInfo = fileList.get(i);

            if (newFolder.equalsIgnoreCase(fileInfo.getFileName())) {
                log.info("=====>File {} found", fileInfo.getFileName());
                isFolderExisted = true;
                break;
            }

        }
        if (!isFolderExisted) {
            String newFolderPath = remoteFolder + newFolder;
            share.mkdir(newFolderPath);
            log.info("=====>Create folder {} successfully", newFolderPath);
            return true;
        }
        return false;
    }
}
