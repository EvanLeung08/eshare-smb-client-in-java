package com.eshare.smbj.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileSearchDTO extends ConnectionDTO {

    String remoteFolder;
    String filePattern;
}
