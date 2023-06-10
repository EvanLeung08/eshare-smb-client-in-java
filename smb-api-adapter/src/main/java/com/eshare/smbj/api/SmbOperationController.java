package com.eshare.smbj.api;


import com.eshare.smbj.common.Constant;
import com.eshare.smbj.model.*;
import com.eshare.smbj.service.impl.SmbLegacyV1Support;
import com.eshare.smbj.service.AuthenticationServiceI;
import com.eshare.smbj.service.SmbOperationServiceI;
import com.hierynomus.smbj.session.Session;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SMB CRUD Operation Controller
 *
 * @Author Evan Leung
 */
@Slf4j
@RestController
@Api(value = "SMB Operations", description = "Operations related to SMB files")
public class SmbOperationController {

    private final AuthenticationServiceI authenticationServiceI;

    private final SmbOperationServiceI smbOperationServiceI;

    private final SmbLegacyV1Support smbLegacyV1Support;

    public SmbOperationController(AuthenticationServiceI authenticationServiceI, SmbOperationServiceI smbOperationServiceI, SmbLegacyV1Support smbLegacyV1Support) {
        this.authenticationServiceI = authenticationServiceI;
        this.smbOperationServiceI = smbOperationServiceI;
        this.smbLegacyV1Support = smbLegacyV1Support;
    }

    @Operation(summary = "Upload a file to remote server")
    @ApiResponse(responseCode = Constant.SUCCESS_CODE, description = "File uploaded successfully")
    @PostMapping("/smb/upload")
    public ResponseEntity<Object> upload(
            @ApiParam(value = "FileUploadDTO object containing details of the file to be uploaded", required = true)
            @RequestBody FileUploadDTO fileUploadDTO) {
        Map<String, String> result = new HashMap<>();
        boolean isSuccess = false;

        try {
            if (Constant.LEGACY_VERSION.equalsIgnoreCase(fileUploadDTO.getSmbVersion())) {
                isSuccess = smbLegacyV1Support.upload(fileUploadDTO);
            } else {
                final Session session = authenticationServiceI.connectAndVerify(fileUploadDTO.getRemoteHost(), fileUploadDTO.getAccount(), fileUploadDTO.getPassword(), fileUploadDTO.getDomain());
                isSuccess = smbOperationServiceI.upload(session, fileUploadDTO);
            }
        } catch (Exception ex) {
            log.error("Error found in upload, parameters:{},err:", fileUploadDTO.toString(), ex);
            result.put("ErrorMsg", ex.getLocalizedMessage());
        } finally {

            result.put("Status", isSuccess ? "Success" : "Failure");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Download files from remote server")
    @ApiResponse(responseCode = Constant.SUCCESS_CODE, description = "Files downloaded successfully")
    @PostMapping("/smb/download")
    public ResponseEntity<Object> download(
            @ApiParam(value = "FileDownloadDTO object containing details of the file to be downloaded", required = true)
            @RequestBody FileDownloadDTO fileDownloadDTO) {
        Map<String, String> result = new HashMap<>();
        boolean isSuccess = false;
        try {
            if (Constant.LEGACY_VERSION.equalsIgnoreCase(fileDownloadDTO.getSmbVersion())) {
                isSuccess = smbLegacyV1Support.download(fileDownloadDTO);
            } else {
                final Session session = authenticationServiceI.connectAndVerify(fileDownloadDTO.getRemoteHost(), fileDownloadDTO.getAccount(), fileDownloadDTO.getPassword(), fileDownloadDTO.getDomain());
                isSuccess = smbOperationServiceI.download(session, fileDownloadDTO);
            }
        } catch (Exception ex) {
            log.error("Error found in download, parameters:{},err:", fileDownloadDTO.toString(), ex);
            result.put("ErrorMsg", ex.getLocalizedMessage());
        } finally {

            result.put("Status", isSuccess ? "Success" : "Falure");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Delete files from remote server")
    @ApiResponse(responseCode = Constant.SUCCESS_CODE, description = "Files deleted successfully")
    @DeleteMapping("/smb/delete")
    public ResponseEntity<Object> delete(
            @ApiParam(value = "FileDeleteDTO object containing details of the file to be deleted", required = true)
            @RequestBody FileDeleteDTO fileDeleteDTO) {
        Map<String, String> result = new HashMap<>();
        boolean isSuccess = false;
        try {
            if (Constant.LEGACY_VERSION.equalsIgnoreCase(fileDeleteDTO.getSmbVersion())) {
                isSuccess = smbLegacyV1Support.delete(fileDeleteDTO);
            } else {
                final Session session = authenticationServiceI.connectAndVerify(fileDeleteDTO.getRemoteHost(), fileDeleteDTO.getAccount(), fileDeleteDTO.getPassword(), fileDeleteDTO.getDomain());
                isSuccess = smbOperationServiceI.delete(session, fileDeleteDTO);
            }
        } catch (Exception ex) {
            log.error("Error found in delete, parameters:{},err:", fileDeleteDTO.toString(), ex);
            result.put("ErrorMsg", ex.getLocalizedMessage());
        } finally {

            result.put("Status", isSuccess ? "Success" : "Failure");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Search files from remote server")
    @ApiResponse(responseCode = Constant.SUCCESS_CODE, description = "File list response  successfully")
    @PostMapping("/smb/search")
    public ResponseEntity<Object> search(
            @ApiParam(value = "FileSearchDTO object containing details of the file to be search", required = true)
            @RequestBody FileSearchDTO fileSearchDTO) {
        Map<String, Object> result = new HashMap<>();
        List<String> matchFileList = null;
        try {
            if (Constant.LEGACY_VERSION.equalsIgnoreCase(fileSearchDTO.getSmbVersion())) {
                matchFileList = smbLegacyV1Support.search(fileSearchDTO);
            } else {
                final Session session = authenticationServiceI.connectAndVerify(fileSearchDTO.getRemoteHost(), fileSearchDTO.getAccount(), fileSearchDTO.getPassword(), fileSearchDTO.getDomain());
                matchFileList = smbOperationServiceI.search(session, fileSearchDTO);
            }
        } catch (Exception ex) {
            log.error("Error found in search, parameters:{},err:", fileSearchDTO.toString(), ex);
            result.put("ErrorMsg", ex.getLocalizedMessage());
        } finally {
            result.put("Status", matchFileList != null && !matchFileList.isEmpty() ? "Success" : "Failure");
            result.put("FileList", matchFileList);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Rename files from remote server")
    @ApiResponse(responseCode = Constant.SUCCESS_CODE, description = "Files downloaded successfully")
    @PostMapping("/smb/rename")
    public ResponseEntity<Object> rename(
            @ApiParam(value = "FileRenamedDTO object containing details of the file to be reanamed", required = true)
            @RequestBody FileRenameDTO fileRenameDTO) {
        Map<String, String> result = new HashMap<>();
        boolean isSuccess = false;
        try {
            if (Constant.LEGACY_VERSION.equalsIgnoreCase(fileRenameDTO.getSmbVersion())) {
                isSuccess = smbLegacyV1Support.rename(fileRenameDTO);
            } else {
                final Session session = authenticationServiceI.connectAndVerify(fileRenameDTO.getRemoteHost(), fileRenameDTO.getAccount(), fileRenameDTO.getPassword(), fileRenameDTO.getDomain());
                isSuccess = smbOperationServiceI.rename(session, fileRenameDTO);
            }
        } catch (Exception ex) {
            log.error("Error found in rename, parameters:{},err:", fileRenameDTO.toString(), ex);
            result.put("ErrorMsg", ex.getLocalizedMessage());
        } finally {

            result.put("Status", isSuccess ? "Success" : "Falure");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
