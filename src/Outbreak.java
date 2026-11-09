import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below him): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     *
     * The two states of a person v, seen together with everything below him:
     *   dpTested[v] = the cheapest cost inside the subtree of v when v IS tested;
     *   dpNotTested[v] = the cheapest cost inside the subtree of v when v is NOT tested.
     * If v is tested, every link inside the subtree is already covered by v himself, so every child may
     * take the cheaper of its two states: dpTested[v] = cost[v] + sum over children u of min(dpTested[u], dpNotTested[u]).
     * If v is not tested, then the link between v and a child u is covered only by u, so every child must be
     * tested: dpNotTested[v] = sum over children u of dpTested[u].
     * The answer is min(dpTested[0], dpNotTested[0]) at the root, and the tested people are rebuilt by
     * walking down from the root with the same rule.
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        if (n <= 0) return new boolean[0];
        long[] dpTested = new long[n];
        long[] dpNotTested = new long[n];
        costFromLeaves(0, -1, nb, cost, dpTested, dpNotTested);

        boolean[] tested = new boolean[n];
        choosePeople(0, -1, nb, dpTested, dpNotTested, tested);
        return tested;
    }

    /** The DP itself: one recursive pass from the root, the values come back from the leaves. */
    private static void costFromLeaves(int v, int parent, int[][] nb, int[] cost,
                                        long[] dpTested, long[] dpNotTested) {
        long tested = cost[v];
        long notTested = 0;
        for (int u : nb[v]) {
            if (u == parent) continue;          // the tree has no cycles, so this is the way back
            costFromLeaves(u, v, nb, cost, dpTested, dpNotTested);
            tested += Math.min(dpTested[u], dpNotTested[u]);
            notTested += dpTested[u];
        }
        dpTested[v] = tested;
        dpNotTested[v] = notTested;
    }

    /** The way back from the cost to the people: from the root down, using the same rule. */
    private static void choosePeople(int v, int parent, int[][] nb,
                                     long[] dpTested, long[] dpNotTested, boolean[] tested) {
        if (parent >= 0 && !tested[parent]) {
            tested[v] = true;                   // the parent is not tested, so the link needs v
        } else {
            tested[v] = dpTested[v] <= dpNotTested[v];
        }
        for (int u : nb[v]) {
            if (u == parent) continue;
            choosePeople(u, v, nb, dpTested, dpNotTested, tested);
        }
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     *
     * down[v] = the number of links on the longest way down from v to a leaf of his subtree.
     * For every person we keep the two longest of those ways: the longest chain that goes through v
     * is the sum of the two of them (one way up to v, one way down from v), and the longest chain of the
     * whole tree is the biggest such sum over all people.
     */
    public static int longestChain(int n, int[][] nb) {
        if (n <= 0) return 0;
        int[] chainThrough = new int[n];   // the longest chain that goes through v
        longestDown(0, -1, nb, chainThrough);
        int answer = 0;
        for (int v = 0; v < n; v++) answer = Math.max(answer, chainThrough[v]);
        return answer;
    }

    /**
     * Fills chainThrough[v] with the length of the longest chain that goes through v and
     * returns the number of links on the longest way down from v (0 for a leaf).
     */
    private static int longestDown(int v, int parent, int[][] nb, int[] chainThrough) {
        int first = 0, second = 0;   // the two longest ways down from v
        for (int u : nb[v]) {
            if (u == parent) continue;
            int down = longestDown(u, v, nb, chainThrough);   // links from u down to a leaf
            int length = down + 1;                            // plus the link v - u
            if (length > first) {
                second = first;
                first = length;
            } else if (length > second) {
                second = length;
            }
        }
        chainThrough[v] = first + second;   // one way up to v and one way down from v
        return first;
    }
}
