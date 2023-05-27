package com.eshare.smbj.serviceI;

import com.eshare.smbj.model.FileDeleteDTO;
import com.eshare.smbj.model.FileDownloadDTO;
import com.eshare.smbj.model.FileUploadDTO;
import com.hierynomus.smbj.session.Session;

import java.io.IOException;

public interface SmbOperationServiceI {

    boolean upload(Session session, FileUploadDTO fileUploadDTO) throws IOException;

    boolean download(Session session, FileDownloadDTO fileDownloadDTO) throws IOException;

    boolean delete(Session session, FileDeleteDTO fileDeleteDTO) throws IOException;
}
