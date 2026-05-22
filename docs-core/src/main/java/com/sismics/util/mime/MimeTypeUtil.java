package com.sismics.util.mime;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to check MIME types.
 *
 * @author bgamard
 */
public class MimeTypeUtil {
    private static final Map<String, String> EXTENSION_MAP = new HashMap<>();

    static {
        EXTENSION_MAP.put("png", MimeType.IMAGE_PNG);
        EXTENSION_MAP.put("jpg", MimeType.IMAGE_JPEG);
        EXTENSION_MAP.put("jpeg", MimeType.IMAGE_JPEG);
        EXTENSION_MAP.put("gif", MimeType.IMAGE_GIF);
        EXTENSION_MAP.put("zip", MimeType.APPLICATION_ZIP);
        EXTENSION_MAP.put("pdf", MimeType.APPLICATION_PDF);
        EXTENSION_MAP.put("odt", MimeType.OPEN_DOCUMENT_TEXT);
        EXTENSION_MAP.put("docx", MimeType.OFFICE_DOCUMENT);
        EXTENSION_MAP.put("pptx", MimeType.OFFICE_PRESENTATION);
        EXTENSION_MAP.put("xlsx", MimeType.OFFICE_SHEET);
        EXTENSION_MAP.put("txt", MimeType.TEXT_PLAIN);
        EXTENSION_MAP.put("csv", MimeType.TEXT_CSV);
        EXTENSION_MAP.put("webm", MimeType.VIDEO_WEBM);
        EXTENSION_MAP.put("mp4", MimeType.VIDEO_MP4);
    }

    /**
     * Try to guess the MIME type of a file.
     * 
     * @param file File to inspect
     * @param name File name
     * @return MIME type
     * @throws IOException e
     */
    public static String guessMimeType(Path file, String name) throws IOException {
        // Prefer extension-based detection for known types (platform-independent)
        if (name != null) {
            int dot = name.lastIndexOf('.');
            if (dot >= 0) {
                String ext = name.substring(dot + 1).toLowerCase();
                String mapped = EXTENSION_MAP.get(ext);
                if (mapped != null) {
                    return mapped;
                }
            }
        }

        String mimeType = Files.probeContentType(file);

        if (mimeType == null && name != null) {
            mimeType = URLConnection.getFileNameMap().getContentTypeFor(name);
        }

        if (mimeType == null) {
            return MimeType.DEFAULT;
        }

        return mimeType;
    }
    
    /**
     * Get a file extension linked to a MIME type.
     * 
     * @param mimeType MIME type
     * @return File extension
     */
    public static String getFileExtension(String mimeType) {
        switch (mimeType) {
            case MimeType.APPLICATION_ZIP:
                return "zip";
            case MimeType.IMAGE_GIF:
                return "gif";
            case MimeType.IMAGE_JPEG:
                return "jpg";
            case MimeType.IMAGE_PNG:
                return "png";
            case MimeType.APPLICATION_PDF:
                return "pdf";
            case MimeType.OPEN_DOCUMENT_TEXT:
                return "odt";
            case MimeType.OFFICE_DOCUMENT:
                return "docx";
            case MimeType.TEXT_PLAIN:
                return "txt";
            case MimeType.TEXT_CSV:
                return "csv";
            case MimeType.VIDEO_MP4:
                return "mp4";
            case MimeType.VIDEO_WEBM:
                return "webm";
            default:
                return "bin";
        }
    }
}
