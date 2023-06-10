package com.eshare.smbj.service;

import com.eshare.smbj.model.*;
import com.hierynomus.smbj.session.Session;

import java.io.IOException;
import java.util.List;

public interface SmbOperationServiceI {

    boolean upload(Session session, FileUploadDTO fileUploadDTO) throws IOException;

    boolean download(Session session, FileDownloadDTO fileDownloadDTO) throws IOException;

    boolean rename(Session session, FileRenameDTO fileRenameDTO) throws IOException;

    boolean delete(Session session, FileDeleteDTO fileDeleteDTO) throws IOException;

    List<String> search(Session session, FileSearchDTO fileSearchDTO) throws IOException;
}
