package com.socgen;

import java.util.ArrayList;
import java.util.List;

public class StarTrack {
    private static char BOTH = 'B';
    private static char LEFT = 'L';
    private static char RIGHT = 'R';
    private static char EMPTY = '.';
    private static String TRACK = "X";

    private void animate(int speed, List<char[]> list) {
        char[] initChars = list.get(list.size() - 1);
        char[] movedChars = new char[initChars.length];
        boolean isEmpty = true;

        for (int i = 0; i < movedChars.length; i++) {
            if (initChars[i] != StarTrack.EMPTY) {
                isEmpty = false;
            }
            movedChars[i] = StarTrack.EMPTY;
        }
        if (isEmpty) {
            return;
        }

        // Move the stars
        for (int i = 0; i < initChars.length; i++) {
            char c = initChars[i];
            int index;
            
            if (StarTrack.LEFT == c || StarTrack.BOTH == c) {
                if ((index = i - speed) >= 0) {
                    if (movedChars[index] == StarTrack.EMPTY) {
                        movedChars[index] = StarTrack.LEFT;
                    } else {
                        movedChars[index] = StarTrack.BOTH;
                    }
                }
            }
            if (StarTrack.RIGHT == c || StarTrack.BOTH == c) {
                if ((index = i + speed) < initChars.length) {
                    if (movedChars[index] == StarTrack.EMPTY) {
                        movedChars[index] = StarTrack.RIGHT;
                    } else {
                        movedChars[index] = StarTrack.BOTH;
                    }
                }
            }
        }
        list.add(movedChars);
        // Recursive for new location
        animate(speed, list);
    }

    public List<String> animate(int speed, String init) {
        List<char[]> charList = new ArrayList<>();
        char[] initChars = init.toCharArray();
        charList.add(initChars);
        animate(speed, charList);

        // Change the stars to track
        List<String> list = new ArrayList<>();
        for (char[] chars : charList) {
            String s = String.valueOf(chars);
            s = s.replaceAll("[" + StarTrack.LEFT + StarTrack.RIGHT + StarTrack.BOTH + "]", StarTrack.TRACK);
            list.add(s);
        }
        return list;
    }

    public static void main(String[] args) {
        StarTrack st = new StarTrack();
        int speed;
        String init = null;
        List<String> list;

        speed = 2;
        init = "..R....";
        list = st.animate(speed, init);
        System.out.println("{");
        list.forEach(s -> System.out.println(s));
        System.out.println("}\n");

        speed = 3;
        init = "RR..LRL";
        list = st.animate(speed, init);
        System.out.println("{");
        list.forEach(s -> System.out.println(s));
        System.out.println("}\n");

        speed = 2;
        init = "LRLR.LRLR";
        list = st.animate(speed, init);
        System.out.println("{");
        list.forEach(s -> System.out.println(s));
        System.out.println("}\n");

        speed = 10;
        init = "RLRLRLRLRL";
        list = st.animate(speed, init);
        System.out.println("{");
        list.forEach(s -> System.out.println(s));
        System.out.println("}\n");

        speed = 1;
        init = "...";
        list = st.animate(speed, init);
        System.out.println("{");
        list.forEach(s -> System.out.println(s));
        System.out.println("}\n");

        speed = 1;
        init = "LRRL.LR.LRR.R.LRRL.";
        list = st.animate(speed, init);
        System.out.println("{");
        list.forEach(s -> System.out.println(s));
        System.out.println("}\n");
    }
}
