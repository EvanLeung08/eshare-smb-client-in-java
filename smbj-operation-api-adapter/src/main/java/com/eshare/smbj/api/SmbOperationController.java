package com.eshare.smbj.api;


import com.eshare.smbj.model.FileDeleteDTO;
import com.eshare.smbj.model.FileDownloadDTO;
import com.eshare.smbj.model.FileSearchDTO;
import com.eshare.smbj.model.FileUploadDTO;
import com.eshare.smbj.serviceI.AuthenticationServiceI;
import com.eshare.smbj.serviceI.SmbOperationServiceI;
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

    public SmbOperationController(AuthenticationServiceI authenticationServiceI, SmbOperationServiceI smbOperationServiceI) {
        this.authenticationServiceI = authenticationServiceI;
        this.smbOperationServiceI = smbOperationServiceI;
    }

    @Operation(summary = "Upload a file to the server")
    @ApiResponse(responseCode = "200", description = "File uploaded successfully")
    @PostMapping("/smb/upload")
    public ResponseEntity<Object> upload(
            @ApiParam(value = "FileUploadDTO object containing details of the file to be uploaded", required = true)
            @RequestBody FileUploadDTO fileUploadDTO) {
        Map<String, String> result = new HashMap<>();
        boolean isSuccess = false;
        try {
            final Session session = authenticationServiceI.connectAndVerify(fileUploadDTO.getRemoteHost(), fileUploadDTO.getAccount(), fileUploadDTO.getPassword(), fileUploadDTO.getDomain());
            isSuccess = smbOperationServiceI.upload(session, fileUploadDTO);
        } catch (Exception ex) {
            log.error("Error found in upload, parameters:{},err:", fileUploadDTO.toString(), ex);
            result.put("ErrorMsg", ex.getLocalizedMessage());
        } finally {

            result.put("Status", isSuccess ? "Success" : "Failure");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Download files from the server")
    @ApiResponse(responseCode = "200", description = "Files downloaded successfully")
    @PostMapping("/smb/download")
    public ResponseEntity<Object> download(
            @ApiParam(value = "FileDownloadDTO object containing details of the file to be downloaded", required = true)
            @RequestBody FileDownloadDTO fileDownloadDTO) {
        Map<String, String> result = new HashMap<>();
        boolean isSuccess = false;
        try {
            final Session session = authenticationServiceI.connectAndVerify(fileDownloadDTO.getRemoteHost(), fileDownloadDTO.getAccount(), fileDownloadDTO.getPassword(), fileDownloadDTO.getDomain());
            isSuccess = smbOperationServiceI.download(session, fileDownloadDTO);
        } catch (Exception ex) {
            log.error("Error found in download, parameters:{},err:", fileDownloadDTO.toString(), ex);
            result.put("ErrorMsg", ex.getLocalizedMessage());
        } finally {

            result.put("Status", isSuccess ? "Success" : "Falure");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Delete  files from the server")
    @ApiResponse(responseCode = "200", description = "Files deleted successfully")
    @DeleteMapping("/smb/delete")
    public ResponseEntity<Object> delete(
            @ApiParam(value = "FileDeleteDTO object containing details of the file to be deleted", required = true)
            @RequestBody FileDeleteDTO fileDeleteDTO) {
        Map<String, String> result = new HashMap<>();
        boolean isSuccess = false;
        try {
            final Session session = authenticationServiceI.connectAndVerify(fileDeleteDTO.getRemoteHost(), fileDeleteDTO.getAccount(), fileDeleteDTO.getPassword(), fileDeleteDTO.getDomain());
            isSuccess = smbOperationServiceI.delete(session, fileDeleteDTO);
        } catch (Exception ex) {
            log.error("Error found in delete, parameters:{},err:", fileDeleteDTO.toString(), ex);
            result.put("ErrorMsg", ex.getLocalizedMessage());
        } finally {

            result.put("Status", isSuccess ? "Success" : "Failure");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Search files from the server")
    @ApiResponse(responseCode = "200", description = "File list response  successfully")
    @PostMapping("/smb/search")
    public ResponseEntity<Object> search(
            @ApiParam(value = "FileSearchDTO object containing details of the file to be search", required = true)
            @RequestBody FileSearchDTO fileSearchDTO) {
        Map<String, Object> result = new HashMap<>();
        List<String> matchFileList = null;
        try {
            final Session session = authenticationServiceI.connectAndVerify(fileSearchDTO.getRemoteHost(), fileSearchDTO.getAccount(), fileSearchDTO.getPassword(), fileSearchDTO.getDomain());
            matchFileList = smbOperationServiceI.search(session, fileSearchDTO);
        } catch (Exception ex) {
            log.error("Error found in search, parameters:{},err:", fileSearchDTO.toString(), ex);
            result.put("ErrorMsg", ex.getLocalizedMessage());
        } finally {
            result.put("Status", matchFileList != null && !matchFileList.isEmpty() ? "Success" : "Failure");
            result.put("FileList", matchFileList);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
