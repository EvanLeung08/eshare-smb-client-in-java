package com.eshare.smbj.api;


import com.eshare.smbj.model.FileDeleteDTO;
import com.eshare.smbj.model.FileDownloadDTO;
import com.eshare.smbj.model.FileUploadDTO;
import com.eshare.smbj.serviceI.AuthenticationServiceI;
import com.eshare.smbj.serviceI.SmbOperationServiceI;
import com.hierynomus.smbj.session.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Evan Leung
 */
@Slf4j
@RestController
public class SmbOperationController {

    private final AuthenticationServiceI authenticationServiceI;

    private final SmbOperationServiceI smbOperationServiceI;

    public SmbOperationController(AuthenticationServiceI authenticationServiceI, SmbOperationServiceI smbOperationServiceI) {
        this.authenticationServiceI = authenticationServiceI;
        this.smbOperationServiceI = smbOperationServiceI;
    }


    @PostMapping("/smb/upload")
    public ResponseEntity<Object> upload(@RequestBody FileUploadDTO fileUploadDTO) {
        Map<String, String> result = new HashMap<>();
        boolean isSuccess = false;
        try {
            final Session session = authenticationServiceI.connectAndVerify(fileUploadDTO.getRemoteHost(), fileUploadDTO.getAccount(), fileUploadDTO.getPassword(), fileUploadDTO.getDomain());
            isSuccess = smbOperationServiceI.upload(session, fileUploadDTO);
        } catch (Exception ex) {
            log.error("Error found in upload, parameters:{},err:", fileUploadDTO.toString(), ex);
            result.put("ErrorMsg", ex.getLocalizedMessage());
        } finally {

            result.put("Status", isSuccess ? "Success" : "Falure");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/smb/download")
    public ResponseEntity<Object> download(@RequestBody FileDownloadDTO fileDownloadDTO) {
        Map<String, String> result = new HashMap<>();
        boolean isSuccess = false;
        try {
            final Session session = authenticationServiceI.connectAndVerify(fileDownloadDTO.getRemoteHost(), fileDownloadDTO.getAccount(), fileDownloadDTO.getPassword(), fileDownloadDTO.getDomain());
            isSuccess = smbOperationServiceI.download(session, fileDownloadDTO);
        } catch (Exception ex) {
            log.error("Error found in upload, parameters:{},err:", fileDownloadDTO.toString(), ex);
            result.put("ErrorMsg", ex.getLocalizedMessage());
        } finally {

            result.put("Status", isSuccess ? "Success" : "Falure");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @DeleteMapping("/smb/delete")
    public ResponseEntity<Object> delete(@RequestBody FileDeleteDTO fileDeleteDTO) {
        Map<String, String> result = new HashMap<>();
        boolean isSuccess = false;
        try {
            final Session session = authenticationServiceI.connectAndVerify(fileDeleteDTO.getRemoteHost(), fileDeleteDTO.getAccount(), fileDeleteDTO.getPassword(), fileDeleteDTO.getDomain());
            isSuccess = smbOperationServiceI.delete(session, fileDeleteDTO);
        } catch (Exception ex) {
            log.error("Error found in upload, parameters:{},err:", fileDeleteDTO.toString(), ex);
            result.put("ErrorMsg", ex.getLocalizedMessage());
        } finally {

            result.put("Status", isSuccess ? "Success" : "Falure");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
