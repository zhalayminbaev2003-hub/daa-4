/**
 * Missions 1 and 2. Compare two strains with dynamic programming.
 *
 * Both are the same kind of table: d[i][j] answers a question about the first i letters of a and the first j letters of b.
 * Think about three things before you type: what d[i][j] means, how it follows from smaller cells, and what the first row and column are.
 */
public class Strains {

    /**
     * Mission 1. The smallest number of single-letter changes that turn a into b. One change is: replace a letter,
     * insert a letter, or delete a letter. This is the "Mutations" line.
     * Example: "ACGT" -> "AGT" is 1 (delete C). "GATTACA" -> "GCATGCA" needs 3.
     *
     * The table d[i][j] answers: how many changes are needed to turn the first i letters of a
     * into the first j letters of b. The table has (n+1) rows and (m+1) columns, because the
     * empty prefix is a case too: d[i][0] = i (delete i letters) and d[0][j] = j (insert j letters).
     *
     * The last step of an optimal transformation is one of three operations, and every one of them
     * lands in a smaller cell:
     *   - the letters are equal: d[i-1][j-1]       (keep the letter, no change)
     *   - replace a[i-1] by b[j-1]: d[i-1][j-1] + 1
     *   - delete a[i-1]:              d[i-1][j]   + 1
     *   - insert b[j-1]:              d[i][j-1]   + 1
     * We take the smallest of those, so d[i][j] = min(d[i-1][j-1] + cost, d[i-1][j] + 1, d[i][j-1] + 1)
     * with cost = 0 when the letters are equal and 1 otherwise.
     * The answer is d[n][m]: the whole of a against the whole of b.
     */
    public static int editDistance(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] d = new int[n + 1][m + 1];
        // first row and first column: turning something into nothing costs one deletion per letter
        for (int i = 0; i <= n; i++) d[i][0] = i;
        for (int j = 0; j <= m; j++) d[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                int diagonal = d[i - 1][j - 1] + cost;  // keep or replace the last letter
                int delete = d[i - 1][j] + 1;          // delete a[i-1]
                int insert = d[i][j - 1] + 1;          // insert b[j-1]
                d[i][j] = Math.min(diagonal, Math.min(delete, insert));
            }
        }
        return d[n][m];
    }

    /**
     * Mission 2. The length of the longest common subsequence: letters that appear in both strains in the same order,
     * but not necessarily next to each other. This is the "Shared core" line.
     * Example: "ACGTT" and "AGTC": the longest is "AGT", length 3.
     */
    public static int lcsLength(String a, String b) {
        // TODO Mission 2
        return -1;
    }
}
