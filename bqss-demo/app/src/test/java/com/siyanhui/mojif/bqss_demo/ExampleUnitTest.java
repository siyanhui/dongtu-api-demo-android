package com.siyanhui.mojif.bqss_demo;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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
    public void outPut() {
        System.out.println("try块中语句没有被执行：nonexetry  " + nonexetry());

        System.out.println("finally块中有return语句：directReturn");
        System.out.println(directReturn());

        System.out.println("finall块中改变了return的值(基础变量)：changeReturnValue1");
        System.out.println(changeReturnValue1());

        System.out.println("finall块中改变了return的值(String变量)：changeReturnValue2");
        System.out.println(changeReturnValue2());

        System.out.println("finall块中改变了return的值(非String的引用变量)：changeReturnValue3");
        System.out.println(changeReturnValue3().get("name"));
    }

    public String exitIntry() {
        try {
            System.exit(0);
        } finally {
            return "cgz";
        }
    }


    public String nonexetry() {
        if (25 > 3)
            return "before try";
        try {
            return "in try";
        } finally {
            return "in finally";
        }
    }


    public Map changeReturnValue3() {
        Map<String, Object> map = new HashMap();
        map.put("name", "cgz");
        try {
            return map;
        } finally {
            map.put("name", "abc");
        }
    }


    public String changeReturnValue2() {
        String s = "cgz";
        try {
            return s;
        } finally {
            s = "abc";
        }
    }

    public boolean changeReturnValue1() {
        boolean b = true;
        try {
            return b;
        } finally {
            b = false;
        }
    }

    public boolean directReturn() {
        try {
            return true;
        } finally {
            return false;
        }
    }
}