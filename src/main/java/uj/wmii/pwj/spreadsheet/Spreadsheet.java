package uj.wmii.pwj.spreadsheet;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spreadsheet {
    private static final int DEFAULT = Integer.MIN_VALUE;

    public String[][] calculate(String[][] source) {
        if (source == null) {
            return new String[0][0];
        }

        int rows = source.length, cols = source[0].length;

        int[][] val = new int[rows][cols];
        for (int[] row : val) {
            Arrays.fill(row, DEFAULT);
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                get(source, val, i, j);
            }
        }

        String[][] resultSpreadsheet = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultSpreadsheet[i][j] = String.valueOf(val[i][j]);
            }
        }

        return resultSpreadsheet;
    }

    private int get(String[][] source, int[][] calculated, int row, int col) {
        if (calculated[row][col] != DEFAULT) {
            return calculated[row][col];
        }

        String cellValue = source[row][col].trim();
        int value;

        if (cellValue.startsWith("=")) {
            value = calculateFormula(source, calculated, cellValue);
        } else if (cellValue.startsWith("$")) {
            value = resolveReference(source, calculated, cellValue);
        } else {
            try {
                value = Integer.parseInt(cellValue);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("blad");
            }
        }

        calculated[row][col] = value;
        return value;
    }

    private int calculateFormula(String[][] source, int[][] calculated, String formula) {
        Pattern pattern = Pattern.compile("=(\\w+)\\(([^,]+),([^)]+)\\)");
        Matcher matcher = pattern.matcher(formula);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("blad");
        }

        String operation = matcher.group(1);
        int p1 = resolveParameter(source, calculated, matcher.group(2).trim());
        int p2 = resolveParameter(source, calculated, matcher.group(3).trim());

        switch (operation) {
            case "ADD":
                return p1 + p2;
            case "SUB":
                return p1 - p2;
            case "MUL":
                return p1 * p2;
            case "DIV":
                return p1 / p2;
            case "MOD":
                return p1 % p2;
            default:
                return 0;
        }
    }

    private int resolveParameter(String[][] source, int[][] calculated, String paramStr) {
        if (paramStr.startsWith("$")) {
            return resolveReference(source, calculated, paramStr);
        } else {
            return Integer.parseInt(paramStr);

        }
    }

    private int resolveReference(String[][] source, int[][] calculated, String reference) {
        String ref = reference.substring(1);
        int firstDigitIndex = -1;
        for (int i = 0; i < ref.length(); i++) {
            if (Character.isDigit(ref.charAt(i))) {
                firstDigitIndex = i;
                break;
            }
        }

        int c = ref.substring(0, firstDigitIndex).toUpperCase().charAt(0) - 'A';
        int r = Integer.parseInt(ref.substring(firstDigitIndex)) - 1;

        return get(source, calculated, r, c);
    }
}