package com.eshare.smbj.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConnectionDTO {
    protected String remoteHost;
    protected String shareName;
    protected String domain;
    protected String account;
    protected String password;
    protected String smbVersion="2.0";
}
