package com.scistor.tab.auth.licensing;

import net.nicholaswilliams.java.licensing.*;
import net.nicholaswilliams.java.licensing.encryption.PasswordProvider;
import net.nicholaswilliams.java.licensing.encryption.PublicKeyDataProvider;
import net.nicholaswilliams.java.licensing.exception.KeyNotFoundException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author Wei Xing
 */
public class LicenseValidator {

    public LicenseValidator() {
        LicenseManagerProperties.setLicenseProvider(new MyLicenseProvider());
        LicenseManagerProperties.setPublicKeyDataProvider(new MyPublicKeyDataProvider());
        LicenseManagerProperties.setPublicKeyPasswordProvider(new MyPublicKeyPasswordProvider());
        LicenseManagerProperties.setLicensePasswordProvider(new MyLicensePasswordProvider());
    }

    public License decryptAndVerifyLicense(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        StringWriter writer = null;
        while (true) {
            String line = reader.readLine();
            if (line.startsWith("------ END")) {
                break;
            }
            if (writer != null) {
                writer.append(line);
            }
            if (line.trim().isEmpty()) {
                writer = new StringWriter(1024);
            }
        }

        byte[] licenseData = Base64.decodeBase64(writer.toString());
        SignedLicense signedLicense = (SignedLicense) new ObjectSerializer().readObject(SignedLicense.class, licenseData);
        return LicenseManager.getInstance().decryptAndVerifyLicense(signedLicense);
    }

    public static class MyLicenseProvider implements LicenseProvider {

        @Override
        public SignedLicense getLicense(Object context) {
            return null;
        }
    }

    public static class MyPublicKeyDataProvider implements PublicKeyDataProvider {

        @Override
        public byte[] getEncryptedPublicKeyData() throws KeyNotFoundException {
            try {
                return IOUtils.toByteArray(MyPublicKeyDataProvider.class.getResourceAsStream("/public.key"));
            } catch (IOException e) {
                throw new KeyNotFoundException(e);
            }
        }
    }

    public static class MyPublicKeyPasswordProvider implements PasswordProvider {

        @Override
        public char[] getPassword() {
            return "c1692690-9c14-4a5c-86d6-31bbe8dcbae9".toCharArray();
        }
    }

    public static class MyLicensePasswordProvider implements PasswordProvider {

        @Override
        public char[] getPassword() {
            return "3ea661a7-3e7a-4af4-9d76-d99f1dc0fa3d".toCharArray();
        }
    }
}
