package ro.colibri.legacy.session;

import java.util.Locale;

import org.apache.ofbiz.base.util.UtilProperties;

public class Messages {
    public static String nls(final String label, final Locale locale) {
        return UtilProperties.getMessage(RESOURCE_NAME, label, locale);
    }
    public static final String RESOURCE_NAME = "ColibriLegacyUiLabels";
    
    public static final String PRODUCT_SYNC_ERROR = "ProductSyncError";
    public static final String UPLOADED_FILE_MISSING = "UploadedFileDataNotFound";
}
