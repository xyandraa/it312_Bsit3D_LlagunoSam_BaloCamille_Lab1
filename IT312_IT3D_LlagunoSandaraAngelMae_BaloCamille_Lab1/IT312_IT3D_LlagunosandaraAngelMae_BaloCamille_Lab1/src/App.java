import java.util.Arrays;
import java.util.Scanner;

public class App {
   //  HANDLE PLAINTEXT INPUT 
private static String promptPlaintext(Scanner sc) {
    while (true) { // loop until valid plaintext is given
        System.out.print("Enter plaintext (letters only): ");
        String raw = sc.nextLine(); // read user input

        // Trap 1: check if input is null or empty after trimming spaces
        if (raw == null || raw.trim().isEmpty()) {
            System.out.println("Error: Plaintext cannot be empty.");
            continue; // repeat loop until valid input
        }

        // Trap 2: check if input contains only alphabets
        // raw.matches("[a-zA-Z]+") → returns true if raw contains only letters
        if (!raw.matches("[a-zA-Z]+")) {
            System.out.println("Error: Plaintext must contain letters only (A-Z, a-z).");
            continue; // reject and loop again
        }

        // If valid, convert to uppercase before returning
        return raw.toUpperCase(); // HELLO, world → HELLO, WORLD
    }
}
//ISTOP

    // HANDLE KEY INPUT 
    private static int[] promptKey(Scanner sc) {
        while (true) { // loop until valid key is given
            System.out.print("Enter numeric key (digits only, e.g. 31425): ");
            String keyStr = sc.nextLine().trim();

            // Trap 1: ensure only digits are used
            // keyStr.matches("\\d+") → regex check for digits only
            if (!keyStr.matches("\\d+")) {
                System.out.println("Error: Key must contain digits only (no letters or symbols).");
                continue; 
            }

            int n = keyStr.length();
            int[] key = new int[n];
            boolean[] seen = new boolean[n + 1];
            boolean ok = true;

            // Loop through digits of the key
            for (int i = 0; i < n; i++) {
                int d = Character.getNumericValue(keyStr.charAt(i)); // convert char → int
                key[i] = d;

                // Trap 2: must be within range 1..n
                if (d < 1 || d > n) {
                    System.out.println("Error: Each key digit must be in the range 1.." + n + ".");
                    ok = false;
                    break;
                }

                // Trap 3: must not be repeated
                if (seen[d]) {
                    System.out.println("Error: Duplicate digit '" + d + "' found in key. Key digits must be unique.");
                    ok = false;
                    break;
                }
                seen[d] = true; // mark digit as used
            }
            if (!ok) continue;

            // Trap 4: check if it forms a complete permutation
            for (int i = 1; i <= n; i++) {
                if (!seen[i]) {
                    System.out.println("Error: Key must be a permutation of 1.." + n + ". Missing digit: " + i);
                    ok = false;
                    break;
                }
            }
            if (!ok) continue;

            return key; // return valid key
        }
    }
    //ISTOPPPP

    // HELPER METHOD: FIND COLUMN POSITION 
    private static int getKeyIndex(int[] key, int colNumber) {
        // Loops through key to find index of given column number
        for (int i = 0; i < key.length; i++)
            if (key[i] == colNumber) return i;
        return -1; // not found
    }

    // PRINT ENCRYPTION/DECRYPTION TABLE
    private static void printTable(int[] key, char[][] table) {
        int rows = table.length;
        int cols = key.length;
        int rowNumWidth = Integer.toString(rows).length();

        StringBuilder border = new StringBuilder("+");
        for (int j = 0; j < cols; j++) border.append("---+");
        String pad = String.format("%" + (rowNumWidth + 1) + "s", "");

        // Print top border
        System.out.println(pad + border.toString());

        // Print column key values
        System.out.print(pad + "|");
        for (int j = 0; j < cols; j++) {
            System.out.printf(" %d |", key[j]);
        }
        System.out.println();

        System.out.println(pad + border.toString());

        // Print table rows
        for (int r = 0; r < rows; r++) {
            System.out.printf("%" + rowNumWidth + "d ", r + 1); // row number
            System.out.print("|");
            for (int c = 0; c < cols; c++) {
                char ch = table[r][c];
                if (ch == '\0') ch = ' '; // replace empty char with space
                System.out.printf(" %c |", ch);
            }
            System.out.println();
            System.out.println(pad + border.toString());
        }
    }

    //  ENCRYPTION 
    private static String encryptAndPrint(String plaintext, int[] key) {
        // Remove non-uppercase letters (safety)
        String pt = plaintext.replaceAll("[^A-Z]", "");
        int cols = key.length;
        int rows = (int) Math.ceil((double) pt.length() / cols);

        // Fill table row by row
        char[][] table = new char[rows][cols];
        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (idx < pt.length()) table[r][c] = pt.charAt(idx++);
                else table[r][c] = 'X'; // padding
            }
        }

        // Show encryption process
        System.out.println("\n=== ENCRYPTION ===");
        System.out.println("PT = " + pt);
        System.out.println("K  = " + Arrays.toString(key) + "  -> " + cols + " columns");
        System.out.println("Rows = " + rows);
        printTable(key, table);

        // Build ciphertext column by column (based on key order)
        StringBuilder ct = new StringBuilder();
        for (int order = 1; order <= cols; order++) {
            int colIdx = getKeyIndex(key, order); // find actual column index
            for (int r = 0; r < rows; r++) {
                ct.append(table[r][colIdx]); // append() builds ciphertext
            }
        }
        System.out.println("CT = " + ct.toString());
        return ct.toString();
    }

    //  DECRYPTION 
    private static String decryptAndPrint(String ciphertext, int[] key) {
        String ct = ciphertext.replaceAll("[^A-Z]", ""); // clean ciphertext
        int cols = key.length;
        int rows = (int) Math.ceil((double) ct.length() / cols);

        System.out.println("\n=== DECRYPTION ===");
        System.out.println("CT = " + ct);
        System.out.println("K  = " + Arrays.toString(key) + "  -> " + cols + " columns");
        System.out.println("Rows = " + rows);

        // Divide ciphertext into groups according to rows
        String[] groupsInKeyOrder = new String[cols];
        int idx = 0;
        for (int order = 1; order <= cols; order++) {
            int take = Math.min(rows, ct.length() - idx);
            if (take < 0) take = 0;
            String g = ct.substring(idx, idx + take); // substring → cut portion of string
            groupsInKeyOrder[order - 1] = g;
            idx += take;
        }

        // Show how CT is divided per column
        System.out.print("CT Division: ");
        for (int i = 0; i < cols; i++) {
            System.out.print(groupsInKeyOrder[i]);
            if (i < cols - 1) System.out.print(" | ");
        }
        System.out.println();

        // Fill table column by column
        char[][] table = new char[rows][cols];
        for (int order = 1; order <= cols; order++) {
            int colIdx = getKeyIndex(key, order);
            String group = groupsInKeyOrder[order - 1];
            for (int r = 0; r < rows; r++) {
                if (r < group.length()) table[r][colIdx] = group.charAt(r);
                else table[r][colIdx] = 'X'; // padding
            }
        }

        // Print reconstructed table
        printTable(key, table);

        // Recover plaintext row by row
        StringBuilder pt = new StringBuilder();
        for (int r = 0; r < rows; r++) {        // outer loop rows
            for (int c = 0; c < cols; c++) {    // inner loop columns
                pt.append(table[r][c]);
            }
        }

        // Remove padding X at the end
        String recovered = pt.toString().replaceAll("X+$", "");
        System.out.println("PT = " + recovered);
        return recovered;
    }

    // YES/NO INPUT(EXPLAIN)
    private static boolean askYesNo(Scanner sc, String prompt) {
        while (true) { // loop until yes/no is answered
            System.out.print(prompt + " (yes/no): ");
            String input = sc.nextLine().trim().toLowerCase(); // normalize input

            // equals() → exact match check
            if (input.equals("yes") || input.equals("y")) return true;
            if (input.equals("no") || input.equals("n")) return false;

            // Trap: if invalid answer
            System.out.println("Invalid input. Please type yes or no.");
        }
    }


      // MAIN PROGRAM 
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        while (true) { // loop until user exits
            String plaintext = promptPlaintext(sc);   // handle plaintext
            int[] key = promptKey(sc);                // handle key

            String ciphertext = encryptAndPrint(plaintext, key); // encryption

            // Optional decryption
            if (askYesNo(sc, "\nDo you want to decrypt the message?")) {
                decryptAndPrint(ciphertext, key);
            }

            // Ask if another message should be processed
            if (!askYesNo(sc, "\nDo you want to process another message?")) {
                System.out.println("Exiting program...");
                break;
            }
        }

        sc.close(); // close scanner
    }
}
