package com.eshare.smbj.common.utils;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import com.hierynomus.smbj.utils.SmbFiles;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;


/**
 * SMB Utils
 *
 * @author Evan Leung
 */
@Slf4j
public class SmbFileUtils extends SmbFiles {


    /**
     * Get sessionø
     *
     * @param remoteHost remote host
     * @param account    account
     * @param password   password
     * @param domain     account domain
     * @return SMB Session
     * @throws IOException
     */
    public static Session getSession(String remoteHost, String account, String password, String domain) throws IOException {
        SmbConfig config = SmbConfig.builder()
                //automatically choose latest supported smb version
                .withMultiProtocolNegotiate(true)
                .withSigningRequired(false)
                .withTimeout(40, TimeUnit.SECONDS)
                .withReadTimeout(100, TimeUnit.SECONDS)
                .withWriteTimeout(100, TimeUnit.SECONDS)
                .withTransactTimeout(100, TimeUnit.SECONDS)
                //must enable
                .withEncryptData(true)
                .build();
        SMBClient client = new SMBClient(config);
        Connection conn = client.connect(remoteHost);
        AuthenticationContext ac = new AuthenticationContext(account, password.toCharArray(), domain);
        return conn.authenticate(ac);
    }


    /**
     * Copies local file to a destination path on the share
     *
     * @param share     the share
     * @param destPath  the path to write to
     * @param source    the local File
     * @param overwrite true/false to overwrite existing file
     * @return the actual number of bytes that was written to the file
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static int upload(java.io.File source, DiskShare share, String destPath, boolean overwrite) throws IOException {

        return copy(source, share, destPath, overwrite);
    }

    /**
     * Download a file from the share
     *
     * @param sourcePath the source File read from share drive
     * @param share      the share
     * @param destPath   the path to write to
     * @return the actual number of bytes that was written to the file
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static long download(String sourcePath, DiskShare share, String destPath) throws IOException {
        long totalBytesRead = 0;
        try (InputStream in = share.openFile(sourcePath,
                EnumSet.of(AccessMask.GENERIC_READ),
                null, SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null).getInputStream();
             OutputStream out = new FileOutputStream((destPath))
        ) {
            byte[] buffer = new byte[10240];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
        }
        return totalBytesRead;
    }


    /**
     * Rename file
     *
     * @param sourcePath  the source File read from share drive
     * @param share       the share
     * @param newFilePath the new file name
     * @param overwrite   overwirte if exists
     * @return the actual number of bytes that was written to the file
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static int rename(String sourcePath, DiskShare share, String newFilePath, Boolean overwrite) throws IOException {
        int r = 0;
        try (File sourceFile = share.openFile(sourcePath,
                EnumSet.of(AccessMask.DELETE),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null)) {
            //rename files
            sourceFile.rename(newFilePath, overwrite);
        }
        return r;
    }

    /**
     * remove file from share drive
     *
     * @param sourcePath the file path read from
     * @param share      share drive
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static void remove(String sourcePath, DiskShare share) {
        try (File sourceFile = share.openFile(sourcePath,
                EnumSet.of(AccessMask.DELETE),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null)) {
            sourceFile.deleteOnClose();
        }
    }

    /**
     * Check if current file is existed
     *
     * @param remoteFolder remote folder
     * @param share        share name
     * @param fileInfo     file information
     * @return
     */
    public static boolean isFileExisted(String remoteFolder, DiskShare share, FileIdBothDirectoryInformation fileInfo) {
        boolean flag;
        try {
            flag = share.fileExists(remoteFolder + fileInfo.getFileName());
        } catch (Exception ex) {
            log.debug("Exception found :", ex);
            flag = false;
        }
        return flag;
    }


}
