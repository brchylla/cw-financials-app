package com.cw.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class XHTMLParser {

    public static boolean patternExistsInHTML(URL url, String[] patterns) {
        int count = 0;
        try ( InputStream in = url.openStream() ) {
            String content = new Scanner(in, "UTF-8").useDelimiter("\\A").next();
            for(String p : patterns) {
                if (content.contains(p)) {
                    count++;
                    System.out.println("Pattern \'" + p + "\' found in HTML at " + url.toString());
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return (count == patterns.length);
    }

    /**
     * Returns a string containing the full element content (e.g. <td> ... {pattern} </td> containing a pattern
     * @param content       Content string to search
     * @param elementTag    Name of element tag (e.g. "TD", "TR", etc.)
     * @param pattern       Unique pattern in content
     * @return              Substring of element text that contains the pattern
     */
    public static final String parseElementWithPattern(String content, String elementTag, String pattern) {
        int index = content.indexOf(pattern);
        if (index > 0) {
            index = content.lastIndexOf("<td", index);
            content = content.substring(index);
            index = content.indexOf("</td>") + 5;
            return content.substring(0, index);
        }
        return null;
    }

}
