package com.hjj.homieMatching.utils;

import java.util.List;
import java.util.Objects;

/**
 * 算法工具
 */
public class AlgorithmUtils {
        public static int minDistance(List<String> tagList1, List<String> tagList2) {
            int n = tagList1.size();
            int m = tagList2.size();

            if (n * m == 0) {
                return n + m;
            }

            int[][] d = new int[n + 1][m + 1];
            for (int i = 0; i < n + 1; i++) {
                d[i][0] = i;
            }

            for (int j = 0; j < m + 1; j++) {
                d[0][j] = j;
            }

            for (int i = 1; i < n + 1; i++) {
                for (int j = 1; j < m + 1; j++) {
                    int left = d[i - 1][j] + 1;
                    int down = d[i][j - 1] + 1;
                    int left_down = d[i - 1][j - 1];
                    if (!Objects.equals(tagList1.get(i - 1), tagList2.get(j - 1))) {
                        left_down += 1;
                    }
                    d[i][j] = Math.min(left, Math.min(down, left_down));
                }
            }
            return d[n][m];
        }


        public static int minDistance(String word1, String word2) {
            int n = word1.length();
            int m = word2.length();

            if (n * m == 0) {
                return n + m;
            }

            int[][] d = new int[n + 1][m + 1];
            for (int i = 0; i < n + 1; i++) {
                d[i][0] = i;
            }

            for (int j = 0; j < m + 1; j++) {
                d[0][j] = j;
            }

            for (int i = 1; i < n + 1; i++) {
                for (int j = 1; j < m + 1; j++) {
                    int left = d[i - 1][j] + 1;
                    int down = d[i][j - 1] + 1;
                    int left_down = d[i - 1][j - 1];
                    if (word1.charAt(i - 1) != word2.charAt(j - 1)) {
                        left_down += 1;
                    }
                    d[i][j] = Math.min(left, Math.min(down, left_down));
                }
            }
            return d[n][m];
        }

}
