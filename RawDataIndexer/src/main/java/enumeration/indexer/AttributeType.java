package enumeration.indexer;

/**
 *
 * @author vitor
 */
public enum AttributeType {
    FILE,
    NUMERIC,
    TEXT;

    public static int getPrecisionOfNumber(String rawColumnData[]) {
        if (rawColumnData.length > 2) {
            //there are extra information, like a Key or a Number of Precision. For example: {[A:NUMERIC:6],[A:NUMERIC:KEY:6],[A:NUMERIC:KEY]}
            int index = 2;//For this exemple: [A:NUMERIC:6]
            if (rawColumnData[2].toLowerCase().equals("key")) {
                if (rawColumnData.length == 4) {
                    index = 3;//For this example: [A:NUMERIC:KEY:6]
                }else{
                    return -1;//For this example: [A:NUMERIC:KEY]
                }
            }
            return Integer.parseInt(rawColumnData[index]);
        }
        return -1;//For this example: [A:NUMERIC]
    }
}
