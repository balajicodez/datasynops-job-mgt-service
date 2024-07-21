package com.datasynops.job.utils;

import java.io.File;

public class FileUtils {

    private static String getFileType(String imageFile) {
        String[] parts = imageFile.split("\\.");
        String fileExtension = parts[parts.length - 1];
        if (fileExtension.contains("jpeg") || fileExtension.contains("jpg")
                || fileExtension.contains("png") || fileExtension.contains("bmp")) {
            return "IMAGE";
        } else if (fileExtension.contains("mp4")) {
            return "VIDEO";
        } else if (fileExtension.contains("mp3")) {
            return "AUDIO";
        }
        return "";
    }


    private static String extractPhoneNumber(File file) {
        String phNumber = file.getName().substring(0, file.getName().indexOf("-"));
        System.out.println(" raw num" + phNumber);
        phNumber = phNumber.replaceAll("\\.", "").replaceAll("E9", "");
        if (phNumber.length() == 8) {
            phNumber = phNumber.concat("00");
        } else if (phNumber.length() == 9) {
            phNumber = phNumber.concat("0");
        } else if (phNumber.length() == 7) {
            phNumber = phNumber.concat("000");
        } else if (phNumber.length() == 6) {
            phNumber = phNumber.concat("0000");
        }

        System.out.println(" curated num" + phNumber);
        return phNumber;
    }
}
