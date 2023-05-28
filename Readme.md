

# Getting Started

An API adapter provides some base operation for new learner to study how to use SMBJ and Jcifs in your project.
> 
- Smbj only supports SMB2 above version
- Jcifs only supports SMB1 

# Features:
- Supports SMB2.0 above CRUD operation by default
- Supports SMB1.0 CRUD operation by input ``smbVersion:1.0`` field in api request parameters


# smbj-api-adapter
## Core Flow
> Below diagram is generated by ChatGPT

![](doc/plantUML/smbj-operation-api-adapter/smb-api-operation-flow.png)

## API Doc
http://localhost:8080/swagger-ui/index.html
![](doc/swagger/swagger-ui.png)

# How To Run

## 1.SMB Upload
Change the value based on your actual needs

For SMB1.0 , please add one more parameter ``"smbVersion":"1.0"``
```json
{
    "remoteHost": "192.168.50.69",
    "shareName": "LANdrive",
    "domain": null,
    "account": "user",
    "password": "123456",
    "remoteFolder": "test/",
    "localFilePath": "/Users/evan/Downloads/Untitled video (4).mp4"
}

```
![](doc/swagger/smb-upload.png)

### **File in remote folder**

![](doc/swagger/uploadedFIle.png) 

## 2.SMB Download
Change the value based on your actual needs

For SMB1.0 , please add one more parameter ``"smbVersion":"1.0"``
```json
{
    "remoteHost": "192.168.50.69",
    "shareName": "LANdrive",
    "domain": null,
    "account": "user",
    "password": "123456",
    "remoteFolder": "test/",
    "localFolder": "/Users/evan/Downloads/",
    "filePattern": ".*\\.mp4$",
    "fileExtension": ".d",
    "needRename": true
}
```
![](doc/swagger/smb-download.png)
### **File in local folder**
> The file will be renamed to ".d" after download successfully by default
![](doc/swagger/downloadedFIle.png)
> The file has been downloaded to local storage and updated the modified time

![](doc/swagger/downloadedFIle2.png)

## 3.SMB Delete
Change the value based on your actual needs

For SMB1.0 , please add one more parameter ``"smbVersion":"1.0"``
```json
{
    "remoteHost": "192.168.50.69",
    "shareName": "LANdrive",
    "domain": null,
    "account": "user",
    "password": "123456",
    "remoteFolder": "test/",
    "filePattern": "*"
}
```
![](doc/swagger/smb-delete.png)

### **File has been removed from remote folder**
> The file has been removed from remote folder

![](doc/swagger/removedFile.png)


## 4.SMB Search
Change the value based on your actual needs

For SMB1.0 , please add one more parameter ``"smbVersion":"1.0"``
```json
{
     "remoteHost": "192.168.50.69",
     "shareName": "LANdrive",
     "domain": null,
     "account": "user",
     "password": "123456",
     "remoteFolder": "test/",
     "filePattern": ".*\\.mp4$"
 }
```
![](doc/swagger/smb-search.png)

### **File list in the response**
> The file will show in API response
![](doc/swagger/searchedFile.png)
