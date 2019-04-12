package org.databaseliner.output;

import javax.xml.bind.DatatypeConverter;

public class RawToHexOutputter implements SQLOutputter {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    public String asSqlString(Object fieldObject) {
        if (fieldObject != null && fieldObject instanceof byte[]) {
            byte[] raw = (byte[]) fieldObject;

            return String.format("HEXTORAW('%s')", bytesToHex(raw));
        }
        return "NULL";
    }

    @Override
    public String asPlaceholder(String tableName, String columnName, String rowSelector, Object fieldObject, String outputDirectory) {
        return asSqlString(fieldObject);
    }

    @Override
    public boolean shouldOutputPlaceholder() {
        return false;
    }
}
