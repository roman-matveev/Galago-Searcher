/*  TOKENIZER SUMMARY

    The new tokenizer now implements:
        - retention of periods within acronyms and decimals
        - retention of apostrophes within queries
        - converts uppercase to lowercase letters

    The benefits of this implementation are:
        - increased specificity when searching for acronym queries (ie. U.N.I.C.E.F or U.S.A)
        - decimals can now be represented fully using periods
        - names with apostrohpes are no longer split

    The drawbacks of this implementation are:
        - decreased effciency in searches, causing redundancy (ie. U.S.A is identical to USA)
        - very specific queries may return sparse results (ie. 192.168.255.255)

    The test cases I've used were:
        - A man with a Ph.D named O'Connor told me that pi was equal to 3.14159
        - Michael Phelps, an exceptional olympic athlete greets his friends saying G'day sir and has a private IP address of 192.186.255.255
*/

package org.galagosearch.exercises;
import org.galagosearch.core.parse.TagTokenizer;

/**
 * @author roman
 */

public class Tokenizer extends TagTokenizer {

    @Override
    protected void tokenAcronymProcessing(String token, int start, int end) {
        token = tokenComplexFix(token);

        // remove start and ending periods
        while (token.startsWith(".")) {

            token = token.substring(1);
            start = start + 1;
        }

        while (token.endsWith(".")) {

            token = token.substring(0, token.length() - 1);
            end -= 1;
        }

        addToken(token, start, end);
    }

    @Override
    protected String tokenSimpleFix(String token) {

        char[] chars = token.toCharArray();
        int j = 0;

        for (int i = 0; i < chars.length; i++) {

            char c = chars[i];

            boolean isAsciiUppercase = (c >= 'A' && c <= 'Z');
            boolean isApostrophe = (c == '\'');

            if (isAsciiUppercase) {

                chars[j] = (char) (chars[i] + 'a' - 'A');
            }

            else if (isApostrophe) {

                System.out.println("\'");
            }

            else {

                chars[j] = chars[i];
            }

            j++;
        }

        token = new String(chars, 0, j);
        return token;
    }
}
