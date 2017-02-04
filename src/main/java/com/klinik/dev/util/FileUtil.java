package com.klinik.dev.util;

import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.UUID;

/**
 * Created by khairulimam on 03/02/17.
 */
public class FileUtil {

    public static final String DIR_SEPARATOR = FileSystems.getDefault().getSeparator();
    public static final String PROFILE_PICTURE_LOCATION = String.format("%s%sklinik-foto%s", System.getProperty("user.home"), DIR_SEPARATOR, DIR_SEPARATOR);
    public static final FileChooser.ExtensionFilter ALLOWED_IMAGE = new FileChooser.ExtensionFilter("File gambar", "*.png", "*.jpg", "*.jpeg");

    public static File generateFileToUploadFolder(String extension) {
        String filename = String.format("%s%s.%s",
                PROFILE_PICTURE_LOCATION,
                UUID.randomUUID().toString(),
                extension);
        return new File(filename);
    }

    public static void uploadFile(File src, File destination) {
        try {
            FileUtils.copyFile(src, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
