package it.unimore.dipi.iot.wldt.exp.utils;

import java.util.Random;

/**
 *
 * Utility class to generate random strings for internal use and demo
 * applications.
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project edt-sdn-experiments
 * @created 21/10/2020 - 09:46
 */
public class StringGenerator {

    public static String generateRandomAlphanumericString(int targetStringLength) {

        try{

            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'

            Random random = new Random();

            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            return generatedString;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
