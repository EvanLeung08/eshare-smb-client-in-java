package com.eshare.smbj.service.impl;

import com.eshare.smbj.common.exception.NotMatchFilesException;
import com.eshare.smbj.model.*;
import com.eshare.smbj.service.SmbOperationServiceI;
import com.eshare.smbj.common.utils.SmbFileUtils;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SMB 2.0 above Operation Service
 *
 * @author Evan Leung
 */
@Slf4j
@Service
public class SmbOperationServiceImpl implements SmbOperationServiceI {

    @Override
    public boolean upload(Session session, FileUploadDTO fileUploadDTO) throws IOException {

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


                boolean isExisted = SmbFileUtils.isFileExisted(fileDownloadDTO.getRemoteFolder(), share, fileInfo);
                if (!isExisted) {
                    log.info("File {} is invalid", fileInfo.getFileName());
                    continue;
                }
                //Check file pattern
                if (!matchPattern(matchFileList, fileInfo, fileDownloadDTO.getFilePattern())) continue;

                log.info("=======>File Name:{}", fileInfo.getFileName());
                log.info("=======>File lastModifiedTime:{}", fileInfo.getLastWriteTime());

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


        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        if (matchFileList.isEmpty()) {
            throw new NotMatchFilesException("No any match files found, please check your file pattern!", null);
        }

        return true;
    }

    @Override
    public boolean rename(Session session, FileRenameDTO fileRenameDTO) throws IOException {
        List<String> matchFileList = new ArrayList<String>();
        String prefix = fileRenameDTO.getPrefix();
        String newFileName = fileRenameDTO.getNewFileName();
        String suffix = fileRenameDTO.getSuffix();
        Connection conn = null;
        try (
                DiskShare share = (DiskShare) session.connectShare(fileRenameDTO.getShareName());
        ) {
            List<FileIdBothDirectoryInformation> fileList = share.list(fileRenameDTO.getRemoteFolder());
            for (int i = 0; i < fileList.size(); i++) {
                FileIdBothDirectoryInformation fileInfo = fileList.get(i);


                boolean isExisted = SmbFileUtils.isFileExisted(fileRenameDTO.getRemoteFolder(), share, fileInfo);
                if (!isExisted) {
                    log.info("File {} is invalid", fileInfo.getFileName());
                    continue;
                }
                //Check file pattern
                if (!matchPattern(matchFileList, fileInfo, fileRenameDTO.getFilePattern())) continue;

                log.info("=======>File Name:{}", fileInfo.getFileName());
                log.info("=======>File lastModifiedTime:{}", fileInfo.getLastWriteTime());

                String remoteFilePath = fileRenameDTO.getRemoteFolder() + fileInfo.getFileName();
                if (!ObjectUtils.isEmpty(newFileName)) {
                    newFileName = fileRenameDTO.getRemoteFolder() + prefix + newFileName + suffix;
                } else {
                    newFileName = fileRenameDTO.getRemoteFolder() + prefix + fileInfo.getFileName() + suffix;
                }

                //Rename file after download successfully
                SmbFileUtils.rename(remoteFilePath, share, newFileName, true);
                log.info("=======>File {} has been renamed to {} successfully", fileInfo.getFileName(), newFileName);

            }
            //Close connection in the end
            conn = session.getConnection();


        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        if (matchFileList.isEmpty()) {
            throw new NotMatchFilesException("No any match files found, please check your file pattern!", null);
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
                boolean isExisted = SmbFileUtils.isFileExisted(fileDeleteDTO.getRemoteFolder(), share, fileInfo);
                if (!isExisted) {
                    log.info("File {} is invalid", fileInfo.getFileName());
                    continue;
                }
                //Check file pattern
                if (!matchPattern(matchFileList, fileInfo, fileDeleteDTO.getFilePattern())) continue;

                log.info("=======>File Name:{}", fileInfo.getFileName());
                log.info("=======>File lastModifiedTime:{}", fileInfo.getLastWriteTime());

                String remotePath = fileDeleteDTO.getRemoteFolder() + fileInfo.getFileName();

                //Remove ".done" file from share drive
                SmbFileUtils.remove(remotePath, share);
                log.info("=======>File {} has been remove successfully", fileInfo.getFileName());

            }
            //Close connection in the end
            conn = session.getConnection();

        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        if (matchFileList.isEmpty()) {
            throw new NotMatchFilesException("No any match files found, please check your file pattern!", null);
        }
        return true;
    }

    @Override
    public List<String> search(Session session, FileSearchDTO fileSearchDTO) throws IOException {
        List<String> matchFileList = new ArrayList<String>();
        Connection conn = null;
        try (
                DiskShare share = (DiskShare) session.connectShare(fileSearchDTO.getShareName());
        ) {
            List<FileIdBothDirectoryInformation> fileList = share.list(fileSearchDTO.getRemoteFolder());
            for (int i = 0; i < fileList.size(); i++) {
                FileIdBothDirectoryInformation fileInfo = fileList.get(i);


                boolean isExisted = SmbFileUtils.isFileExisted(fileSearchDTO.getRemoteFolder(), share, fileInfo);
                if (!isExisted) {
                    log.info("File {} is invalid", fileInfo.getFileName());
                    continue;
                }
                //Check file pattern
                if (!matchPattern(matchFileList, fileInfo, fileSearchDTO.getFilePattern())) continue;

            }
            //Close connection in the end
            conn = session.getConnection();


        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        return matchFileList;
    }

    private boolean matchPattern(List matchFileList, FileIdBothDirectoryInformation fileInfo, String filePattern) {
        // need to check file Pattern here
        Pattern pattern = Pattern.compile(filePattern);
        Matcher matcher = pattern.matcher(fileInfo.getFileName());

        if (matcher.matches()) {
            matchFileList.add(fileInfo.getFileName());
            log.info("File name is expected:{} ", fileInfo.getFileName());
            return true;
        } else {
            log.info("File name is not expected:{}, go to next file", fileInfo.getFileName());
            return false;
        }

    }

}
