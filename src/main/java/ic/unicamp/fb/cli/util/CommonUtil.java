package ic.unicamp.fb.cli.util;

public class CommonUtil {

    public static String retrieveStringFromPieces(String[] labelInParts) {
        String featureLabel = "";
        if (labelInParts != null) {
            StringBuilder sb = new StringBuilder();
            for (String s : labelInParts) {
                sb.append(s);
            }
            featureLabel = sb.toString();
        }
        return featureLabel;
    }
}
