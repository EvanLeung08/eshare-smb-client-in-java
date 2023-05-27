package com.eshare.smbj.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileDownloadDTO extends ConnectionDTO {

    String remoteFolder;
    String localFolder;
    String filePattern;
    String fileExtension = ".done";
    boolean needRename = true;
}
