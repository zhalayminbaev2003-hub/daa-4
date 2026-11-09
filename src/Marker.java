import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     *
     * pi[i] is the length of the longest border of p[0..i]: a "border" is a word that is both a
     * beginning and an ending of it. We build the table from left to right. For position i we start
     * from the border of the previous position, pi[i-1], and try to make it one letter longer:
     * if p[i] == p[j] we can extend that border to j+1 letters. If not, the border of p[0..i] cannot be
     * longer than the border of p[0..j-1], so we fall back to pi[j-1] and try again. The fallback chain
     * is exactly the set of borders of p[0..j-1], so no candidate is lost.
     */
    public static int[] prefixFunction(String p) {
        int[] pi = new int[p.length()];
        for (int i = 1; i < p.length(); i++) {
            int j = pi[i - 1];
            while (j > 0 && p.charAt(i) != p.charAt(j)) {
                j = pi[j - 1];
            }
            if (p.charAt(i) == p.charAt(j)) {
                j++;
            }
            pi[i] = j;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     *
     * k is the number of letters of the pattern that are currently matched. We walk through the text once
     * and never move the text pointer back: on a mismatch we only shorten the already matched part of the
     * pattern, and the only length we can jump to is pi[k-1] (the border of the matched part). Every other
     * possible match would be a border of the matched part as well, so nothing is lost by that jump.
     * After a full match we do not stop and we do not continue from 0: we go back to pi[m-1], so that
     * overlapping occurrences are found too.
     */
    public static int[] findAll(String text, String p) {
        if (p.isEmpty() || text.isEmpty()) return new int[0];
        int m = p.length();
        int[] pi = prefixFunction(p);
        List<Integer> hits = new ArrayList<>();
        int k = 0;                       // how many letters of p are matched right now
        for (int i = 0; i < text.length(); i++) {
            while (k > 0 && text.charAt(i) != p.charAt(k)) {
                k = pi[k - 1];          // fall back to a shorter border, the text pointer stays
            }
            if (text.charAt(i) == p.charAt(k)) {
                k++;
            }
            if (k == m) {                // whole pattern found, ending at i
                hits.add(i - m + 1);
                k = pi[k - 1];          // look for an overlapping next match
            }
        }
        int[] out = new int[hits.size()];
        for (int i = 0; i < out.length; i++) out[i] = hits.get(i);
        return out;
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     *
     * The hash of a window is the number val(w[0])*BASE^(k-1) + ... + val(w[k-1]), all modulo MOD.
     * When the window moves one letter to the right, the old first letter leaves (its value times BASE^(k-1)
     * is taken away), every remaining letter shifts one position to the left (multiply the whole hash by BASE)
     * and the new letter is added. That is O(1) per window instead of O(k).
     * Equal hashes do not prove equal words (collisions), so after a hit in the table of b we compare
     * the letters one by one and only then count the position.
     */
    public static int sharedWords(String a, String b, int k) {
        if (k <= 0 || a.length() < k || b.length() < k) return 0;

        final long MOD = 1000000007L;
        final long BASE = 31L;

        long[] pow = new long[k + 1];
        pow[0] = 1;
        for (int i = 1; i <= k; i++) pow[i] = pow[i - 1] * BASE % MOD;

        // all windows of b: hash -> list of their start positions
        Map<Long, List<Integer>> windowsOfB = new HashMap<>();
        long hb = 0;
        for (int i = 0; i < b.length(); i++) {
            hb = (hb * BASE + letter(b.charAt(i))) % MOD;
            if (i >= k) {
                // the letter b[i-k] leaves the window: it was multiplied by BASE^k
                hb = (hb - letter(b.charAt(i - k)) * pow[k] % MOD + MOD) % MOD;
            }
            if (i >= k - 1) {
                windowsOfB.computeIfAbsent(hb, x -> new ArrayList<>()).add(i - k + 1);
            }
        }

        // the same rolling hash over a, and a comparison of the letters for every candidate
        int count = 0;
        long ha = 0;
        for (int i = 0; i < a.length(); i++) {
            ha = (ha * BASE + letter(a.charAt(i))) % MOD;
            if (i >= k) {
                ha = (ha - letter(a.charAt(i - k)) * pow[k] % MOD + MOD) % MOD;
            }
            if (i >= k - 1) {
                int start = i - k + 1;
                List<Integer> candidates = windowsOfB.get(ha);
                if (candidates != null) {
                    for (int j : candidates) {
                        if (sameLetters(a, start, b, j, k)) {
                            count++;      // one position of a, counted once
                            break;
                        }
                    }
                }
            }
        }
        return count;
    }

    /** The value of a letter for the hash: A, C, G, T become 1, 2, 3, 4. */
    private static long letter(char c) {
        switch (c) {
            case 'A': return 1;
            case 'C': return 2;
            case 'G': return 3;
            case 'T': return 4;
            default:  return 0;
        }
    }

    /** Do the k letters of a starting at ai equal the k letters of b starting at bi? */
    private static boolean sameLetters(String a, int ai, String b, int bi, int k) {
        for (int t = 0; t < k; t++) {
            if (a.charAt(ai + t) != b.charAt(bi + t)) return false;
        }
        return true;
    }
}
