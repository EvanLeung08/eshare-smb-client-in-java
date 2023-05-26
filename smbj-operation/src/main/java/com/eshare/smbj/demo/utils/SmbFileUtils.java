package com.eshare.smbj.demo.utils;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.io.InputStreamByteChunkProvider;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import com.hierynomus.smbj.utils.SmbFiles;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.util.EnumSet;

/**
 * SMB Utils
 * @author Evan Leung
 */
public class SmbFileUtils extends SmbFiles {


    /**
     * Copies local file to a destination path on the share
     *
     * @param share     the share
     * @param destPath  the path to write to
     * @param source    the local File
     * @param overwrite true/false to overwrite existing file
     * @return the actual number of bytes that was written to the file
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static int upload(java.io.File source, DiskShare share, String destPath, boolean overwrite) throws IOException {

        return copy(source, share, destPath, overwrite);
    }

    /**
     * Download a file from the share
     *
     * @param sourcePath the source File read from share drive
     * @param share      the share
     * @param destPath   the path to write to
     * @return the actual number of bytes that was written to the file
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static int download(String sourcePath, DiskShare share, String destPath) throws IOException {
        try (InputStream in = share.openFile(sourcePath,
                EnumSet.of(AccessMask.GENERIC_READ),
                null, SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null).getInputStream();
             OutputStream out = new FileOutputStream((destPath))
        ) {
            byte[] buffer = new byte[10240];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return bytesRead;
        }
    }

    /**
     * Rename file
     *
     * @param sourcePath the source File read from share drive
     * @param share the share
     * @param newFilePath the new file name
     * @param overwrite overwirte if exists
     * @return the actual number of bytes that was written to the file
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static int rename(String sourcePath, DiskShare share, String newFilePath, Boolean overwrite) throws IOException {
        int r = 0;
        try (File sourceFile = share.openFile(sourcePath,
                EnumSet.of(AccessMask.DELETE),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null)) {
            try (InputStream in = sourceFile.getInputStream()) {
                if (!ObjectUtils.isEmpty(newFilePath) && in != null) {
                    try (File newFile = share.openFile(newFilePath,
                            EnumSet.of(AccessMask.GENERIC_WRITE),
                            EnumSet.of(FileAttributes.FILE_ATTRIBUTE_NORMAL),
                            EnumSet.of(SMB2ShareAccess.FILE_SHARE_WRITE),
                            overwrite ? SMB2CreateDisposition.FILE_OVERWRITE_IF : SMB2CreateDisposition.FILE_OPEN,
                            EnumSet.noneOf(SMB2CreateOptions.class))) {
                        r = newFile.write(new InputStreamByteChunkProvider(in));
                    }
                }
            }
            //Delete the source file after rename
            sourceFile.deleteOnClose();
        }
        return r;
    }

    /**
     * remove file from share drive
     * @param sourcePath the file path read from
     * @param share share drive
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static void remove(String sourcePath, DiskShare share) {
        try(File sourceFile = share.openFile(sourcePath,
                EnumSet.of(AccessMask.DELETE),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null)){
            sourceFile.deleteOnClose();
        }
    }

}
