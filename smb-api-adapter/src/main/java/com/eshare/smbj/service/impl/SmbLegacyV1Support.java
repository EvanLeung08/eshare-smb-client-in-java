package com.eshare.smbj.service.impl;

import com.eshare.smbj.common.exception.NotMatchFilesException;
import com.eshare.smbj.model.*;
import jcifs.smb.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compatible with SMB1.0 Operation
 *
 * @author Evan Leung
 */
@Slf4j
@Component
public class SmbLegacyV1Support {

    public boolean upload(FileUploadDTO fileUploadDTO) throws IOException {
        String remoteFolder = fileUploadDTO.getRemoteFolder();
        String uploadFile = fileUploadDTO.getLocalFilePath();
        String remoteHost = fileUploadDTO.getRemoteHost();
        String account = fileUploadDTO.getAccount();
        String psw = fileUploadDTO.getPassword();
        String domain = fileUploadDTO.getDomain();

        File source = new File(fileUploadDTO.getLocalFilePath());
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, account, psw);
        String smbUrl = "smb://" + remoteHost + "/" + fileUploadDTO.getShareName() + "/" + remoteFolder + source.getName();
        SmbFile smbFile = new SmbFile(smbUrl, auth);


        try (FileInputStream fis = new FileInputStream(source);
             SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFile)) {

            final byte[] b = new byte[16 * 1024];
            int read;
            while ((read = fis.read(b, 0, b.length)) > 0) {
                smbfos.write(b, 0, read);
            }
            log.info("=======>File {} has been upload to {} successfully", uploadFile, fileUploadDTO.getRemoteFolder() + source.getName());
        }
        return true;
    }

    public boolean download(FileDownloadDTO fileDownloadDTO) throws IOException {
        List<String> matchFileList = new ArrayList<String>();
        String subFolder = fileDownloadDTO.getRemoteFolder();
        String remoteHost = fileDownloadDTO.getRemoteHost();
        String account = fileDownloadDTO.getAccount();
        String psw = fileDownloadDTO.getPassword();
        String domain = fileDownloadDTO.getDomain();

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, account, psw);
        String smbUrl = "smb://" + remoteHost + "/" + fileDownloadDTO.getShareName() + "/" + subFolder;
        SmbFile smbFolder = new SmbFile(smbUrl, auth);

        SmbFile[] files = smbFolder.listFiles(new SmbFilenameFilter() {
            @Override
            public boolean accept(SmbFile dir, String name) throws SmbException {
                return !name.endsWith(fileDownloadDTO.getFileExtension());
            }
        });

        for (SmbFile file : files) {
            String fileName = file.getName();
            log.info("=======>File Name:{}", fileName);
            log.info("=======>File lastModifiedTime:{}", file.getLastModified());
            log.info("=======>Is file existed?:{}", file.exists());

            //Check file pattern
            if (!matchPattern(matchFileList, file, fileDownloadDTO.getFilePattern())) continue;

            String localFilePath = fileDownloadDTO.getLocalFolder() + fileName;
            String newFileName = fileName + fileDownloadDTO.getFileExtension();

            // Download file from share drive
            try (SmbFileInputStream sfis = new SmbFileInputStream(file);
                 FileOutputStream fos = new FileOutputStream(localFilePath)) {
                byte[] b = new byte[16 * 1024];
                int read;
                while ((read = sfis.read(b, 0, b.length)) > 0) {
                    fos.write(b, 0, read);
                }
                log.info("=======>File {} has been downloaded to {} successfully", fileName, localFilePath);
            }

            // Rename file after download successfully
            SmbFile newFile = new SmbFile(file.getParent() + newFileName, auth);
            file.renameTo(newFile);
            log.info("=======>File {} has been renamed to {} successfully", fileName, newFileName);
        }

        if (matchFileList.isEmpty()) {
            throw new NotMatchFilesException("No any match files found, please check your file pattern!", null);
        }

        return true;
    }


    public boolean rename(FileRenameDTO fileRenameDTO) throws IOException {
        List<String> matchFileList = new ArrayList<String>();
        String subFolder = fileRenameDTO.getRemoteFolder();
        String remoteHost = fileRenameDTO.getRemoteHost();
        String account = fileRenameDTO.getAccount();
        String psw = fileRenameDTO.getPassword();
        String domain = fileRenameDTO.getDomain();
        String prefix = fileRenameDTO.getPrefix();
        String newFileName = fileRenameDTO.getNewFileName();
        String suffix = fileRenameDTO.getSuffix();

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, account, psw);
        String smbUrl = "smb://" + remoteHost + "/" + fileRenameDTO.getShareName() + "/" + subFolder;
        SmbFile smbFolder = new SmbFile(smbUrl, auth);

        SmbFile[] files = smbFolder.listFiles();

        for (SmbFile file : files) {
            String fileName = file.getName();
            log.info("=======>File Name:{}", fileName);
            log.info("=======>File lastModifiedTime:{}", file.getLastModified());
            log.info("=======>Is file existed?:{}", file.exists());

            //Check file pattern
            if (!matchPattern(matchFileList, file, fileRenameDTO.getFilePattern())) continue;


            if (!ObjectUtils.isEmpty(newFileName)) {
                newFileName = prefix + newFileName + suffix;
            } else {
                newFileName = prefix + fileName + suffix;
            }
            // Rename file after download successfully
            SmbFile newFile = new SmbFile(file.getParent() + newFileName, auth);
            file.renameTo(newFile);
            log.info("=======>File {} has been renamed to {} successfully", fileName, newFileName);
        }

        if (matchFileList.isEmpty()) {
            throw new NotMatchFilesException("No any match files found, please check your file pattern!", null);
        }

        return true;
    }

    public boolean delete(FileDeleteDTO fileDeleteDTO) throws IOException {
        List<String> matchFileList = new ArrayList<String>();
        String subFolder = fileDeleteDTO.getRemoteFolder();
        String remoteHost = fileDeleteDTO.getRemoteHost();
        String account = fileDeleteDTO.getAccount();
        String psw = fileDeleteDTO.getPassword();
        String domain = fileDeleteDTO.getDomain();

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, account, psw);
        String smbUrl = "smb://" + remoteHost + "/" + fileDeleteDTO.getShareName() + "/" + subFolder;
        SmbFile smbFolder = new SmbFile(smbUrl, auth);

        SmbFile[] files = smbFolder.listFiles();

        for (SmbFile file : files) {
            String fileName = file.getName();
            log.info("=======>File Name:{}", fileName);
            log.info("=======>File lastModifiedTime:{}", file.getLastModified());
            log.info("=======>Is file existed?:{}", file.exists());
            //Check file pattern
            if (!matchPattern(matchFileList, file, fileDeleteDTO.getFilePattern())) continue;

            // Remove ".done" file from share drive
            file.delete();
            log.info("=======>File {} has been removed successfully", fileName);
        }
        if (matchFileList.isEmpty()) {
            throw new NotMatchFilesException("No any match files found, please check your file pattern!", null);
        }
        return true;
    }

    public List<String> search(FileSearchDTO fileSearchDTO) throws IOException {
        List<String> matchFileList = new ArrayList<String>();
        String subFolder = fileSearchDTO.getRemoteFolder();
        String remoteHost = fileSearchDTO.getRemoteHost();
        String account = fileSearchDTO.getAccount();
        String psw = fileSearchDTO.getPassword();
        String domain = fileSearchDTO.getDomain();

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, account, psw);
        String smbUrl = "smb://" + remoteHost + "/" + fileSearchDTO.getShareName() + "/" + subFolder;
        SmbFile smbFolder = new SmbFile(smbUrl, auth);

        SmbFile[] files = smbFolder.listFiles();

        for (SmbFile file : files) {
            String fileName = file.getName();
            log.info("=======>File Name:{}", fileName);
            log.info("=======>File lastModifiedTime:{}", file.getLastModified());
            log.info("=======>Is file existed?:{}", file.exists());
            //Check file pattern
            if (!matchPattern(matchFileList, file, fileSearchDTO.getFilePattern())) continue;
        }
        return matchFileList;
    }

    private boolean matchPattern(List matchFileList, SmbFile file, String filePattern) {
        // need to check file Pattern here
        Pattern pattern = Pattern.compile(filePattern);
        Matcher matcher = pattern.matcher(file.getName());

        if (matcher.matches()) {
            matchFileList.add(file.getName());
            log.info("File name is expected:{} ", file.getName());
            return true;
        } else {
            log.info("File name is not expected:{}, go to next file", file.getName());
            return false;
        }

    }

}
