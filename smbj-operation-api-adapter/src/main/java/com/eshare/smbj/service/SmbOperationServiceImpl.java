package com.eshare.smbj.service;

import com.eshare.smbj.common.NotMatchFilesException;
import com.eshare.smbj.model.FileDeleteDTO;
import com.eshare.smbj.model.FileDownloadDTO;
import com.eshare.smbj.model.FileUploadDTO;
import com.eshare.smbj.serviceI.SmbOperationServiceI;
import com.eshare.smbj.utils.SmbFileUtils;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SmbOperationServiceImpl implements SmbOperationServiceI {

    @Override
    public boolean upload(Session session, FileUploadDTO fileUploadDTO) throws IOException {

        //The name of the share to connect to
        Connection conn = null;
        try (
                DiskShare share = (DiskShare) session.connectShare(fileUploadDTO.getShareName());
        ) {
            java.io.File source = new java.io.File(fileUploadDTO.getLocalFilePath());
            //Upload file to remote share drive
            SmbFileUtils.upload(source, share, fileUploadDTO.getRemoteFolder() + source.getName(), true);
            log.info("=======>File {} has been upload to {} successfully", source.getName(), fileUploadDTO.getRemoteFolder() + source.getName());
            //Close connection in the end
            conn = session.getConnection();
        } finally {
            if (conn != null) {
                conn.close(true);
            }
        }

        return true;
    }

    @Override
    public boolean download(Session session, FileDownloadDTO fileDownloadDTO) throws IOException {
        List<String> matchFileList = new ArrayList<String>();
        Connection conn = null;
        try (
                DiskShare share = (DiskShare) session.connectShare(fileDownloadDTO.getShareName());
        ) {
            List<FileIdBothDirectoryInformation> fileList = share.list(fileDownloadDTO.getRemoteFolder());
            for (int i = 0; i < fileList.size(); i++) {
                FileIdBothDirectoryInformation fileInfo = fileList.get(i);


                boolean isExisted = isFileExisted(fileDownloadDTO.getRemoteFolder(), share, fileInfo);
                if (!isExisted) {
                    log.info("File {} is invalid", fileInfo.getFileName());
                    continue;
                }
                //Check file pattern
                if (checkPattern(matchFileList, fileInfo, fileDownloadDTO.getFilePattern())) continue;

                log.info("=======>File Name:{}", fileInfo.getFileName());
                log.info("=======>File lastModifiedTime:{}", fileInfo.getLastWriteTime());
                log.info("=======>Is file existed?:{}", isExisted);

                String remoteFilePath = fileDownloadDTO.getRemoteFolder() + fileInfo.getFileName();
                String localFilePath = fileDownloadDTO.getLocalFolder() + fileInfo.getFileName();
                String newFileName = fileDownloadDTO.getRemoteFolder() + fileInfo.getFileName() + fileDownloadDTO.getFileExtension();

                //Download file from share drive
                SmbFileUtils.download(remoteFilePath, share, localFilePath);
                //Rename file after download successfully
                if (fileDownloadDTO.isNeedRename()) {
                    SmbFileUtils.rename(remoteFilePath, share, newFileName, true);
                }
                log.info("=======>File {} has been downloaded to {} successfully", fileInfo.getFileName(), localFilePath);

            }
            //Close connection in the end
            conn = session.getConnection();

            if (matchFileList.isEmpty()) {
                throw new NotMatchFilesException("No any match files found, please check your file pattern!", null);
            }

        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return true;
    }

    @Override
    public boolean delete(Session session, FileDeleteDTO fileDeleteDTO) throws IOException {

        List<String> matchFileList = new ArrayList<String>();
        Connection conn = null;
        try (
                DiskShare share = (DiskShare) session.connectShare(fileDeleteDTO.getShareName());
        ) {
            List<FileIdBothDirectoryInformation> fileList = share.list(fileDeleteDTO.getRemoteFolder(), "*");
            for (int i = 0; i < fileList.size(); i++) {
                FileIdBothDirectoryInformation fileInfo = fileList.get(i);
                boolean isExisted = isFileExisted(fileDeleteDTO.getRemoteFolder(), share, fileInfo);
                if (!isExisted) {
                    log.info("File {} is invalid", fileInfo.getFileName());
                    continue;
                }
                //Check file pattern
                if (checkPattern(matchFileList, fileInfo, fileDeleteDTO.getFilePattern())) continue;

                log.info("=======>File Name:{}", fileInfo.getFileName());
                log.info("=======>File lastModifiedTime:{}", fileInfo.getLastWriteTime());
                log.info("=======>Is file existed?:{}", isExisted);

                String remotePath = fileDeleteDTO.getRemoteFolder() + fileInfo.getFileName();

                //Remove ".done" file from share drive
                SmbFileUtils.remove(remotePath, share);
                log.info("=======>File {} has been remove successfully", fileInfo.getFileName());

            }
            //Close connection in the end
            conn = session.getConnection();
            if (matchFileList.isEmpty()) {
                throw new NotMatchFilesException("No any match files found, please check your file pattern!", null);
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return true;
    }

    private boolean checkPattern(List matchFileList, FileIdBothDirectoryInformation fileInfo, String filePattern) {
        // need to check file Pattern here
        Pattern pattern = Pattern.compile(filePattern);
        Matcher matcher = pattern.matcher(fileInfo.getFileName());

        if (matcher.matches()) {
            matchFileList.add(fileInfo.getFileName());
            log.info("File name is expected:{} ", fileInfo.getFileName());
        } else {
            log.info("File name is not expected:{}, go to next file", fileInfo.getFileName());
            return true;
        }
        return false;
    }

    private static boolean isFileExisted(String subFolder, DiskShare share, FileIdBothDirectoryInformation fileInfo) {
        boolean flag;
        try {
            flag = share.fileExists(subFolder + fileInfo.getFileName());
        } catch (Exception ex) {
            log.debug("Exception found :", ex);
            flag = false;
        }
        return flag;
    }
}
