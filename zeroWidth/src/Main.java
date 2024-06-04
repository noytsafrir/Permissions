public class Main {

    static final char ZWNJ_A = '\u2060'; // Zero Width No-Break Space
    static final char ZWNJ_B = '\u200C'; // Zero Width Non-Joiner
    final char ZWC_C = '\u200B'; // Zero Width Space

    public static void main(String[] args) {
        String encrypted = encrypt("Orginal Text", "Our Secret Message");
        System.out.println(encrypted);
        String decrypted = decrypt(encrypted);
        System.out.println(decrypted);
//        System.out.println("Hello"+ZWNJ_A+" world!"+ZWNJ_B+"Hello"+ZWC_C+"everyone!");
    }

    private static String encrypt(String text, String secret) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(text.charAt(0));

        String binarySize = String.format("%032d",Integer.valueOf(Integer.toBinaryString(secret.length())));
        binarySize = binarySize
                .replace('0',ZWNJ_A)
                .replace('1', ZWNJ_B);
        stringBuffer.append(binarySize);

        char[] secretChars = secret.toCharArray();
        for(char c : secretChars) {
            String bitChar = "0"+Integer.toBinaryString(c);
            bitChar = bitChar
                    .replace('0',ZWNJ_A)
                    .replace('1', ZWNJ_B);

            stringBuffer.append(bitChar);
        }

        stringBuffer.append(text.substring(1));
        return stringBuffer.toString();
    }

    private static String decrypt(String encrypted) {
        StringBuffer decrypted = new StringBuffer();

        encrypted = encrypted.substring(1);
        String size  = "";
        for(int i = 0; i < 32; i++) {
            size+= encrypted.charAt(i);
        }

        size.replace('0',ZWNJ_A).replace('1', ZWNJ_B);
        int secretSize = Integer.parseInt(size, 2);
        encrypted = encrypted.substring(32);
        for(int i = 0; i < secretSize; i++) {
            String bitChar = "";
            for(int j = 0; j < 8; j++) {
                bitChar+= encrypted.charAt(i*8+j);
            }
            bitChar
                    .replace(ZWNJ_A, '0')
                    .replace(ZWNJ_B, '1');
            decrypted.append((char) Integer.parseInt(bitChar, 2));
        }
        return decrypted.toString();
    }
}