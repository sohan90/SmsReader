package com.example.sohan.smsreader;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testPattern(){
        String text = "A sep Text sep With sep Many sep Separators";

        String patternString = "sep";
        Pattern pattern = Pattern.compile(patternString);

        String[] split = pattern.split(text);

        System.out.println("split.length = " + split.length);

        for(String element : split){
            System.out.println("element = " + element);
        }
    }

    @Test
    public void testPattern2(){
        String text = "A sep  sep With sep  sep Separators";

        String patternString = "(.*many.*|.*many.*)";
        //Pattern pattern = Pattern.compile(patternString);
       Pattern compiledPattern =  Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        boolean matches = Pattern.matches(patternString, text);

        System.out.println("matches = " + compiledPattern.matcher(text).matches());
    }
}