# Assignment 4. Patient Zero

Student ID: 254072
Name: Turganbay Islambek
Group: SE-2540

This file is the report. Fill it in as you go. How to start: `START_HERE.md`. The task: `docs/Assignment4.pdf`.

## Program output

The 9 lines your program prints on your data (`java -cp out Main`):

    Mutations: 105
    Shared core: 1027
    Marker hits: 7
    First hit: 1941
    Border length: 3
    Shared words: 349
    Tests needed: 43
    Cheapest testing: 186
    Longest chain: 28

My outbreak report (a screenshot of `output/outbreak.html`):

![outbreak](output/outbreak.png)

The lab console says "Outbreak contained. All 5 missions agree with the lab", so every line of the
scoreboard has a green tick.

## Questions

Answer in your own words, two or three sentences each.

Mission 1. What does the cell d[i][j] of your table mean, and how is it computed from other cells?

The cell d[i][j] is the smallest number of single-letter changes (replace, insert, delete) that turn
the first i letters of strain A into the first j letters of strain B. The last step of any such
transformation is one of three things: keep or replace the last letter (d[i-1][j-1], plus 1 if the
letters differ), delete a[i-1] (d[i-1][j] + 1) or insert b[j-1] (d[i][j-1] + 1), so
d[i][j] = min of these three. The first row and the first column are filled before the loops
(d[i][0] = i deletions, d[0][j] = j insertions), and every cell of the table is then computed from the
three cells above it, to its left and diagonally above-left, so one pass over the table is enough.

Mission 2. How is the longest common subsequence different from the longest common substring? My Shared core is bigger than the number of letters that are the same at the same place: why?

A common substring must be a piece of one continuous piece of both strains, while a common subsequence
only has to keep the same letters in the same order; the gaps between them may be of any size in both
strains. That is why the shared core is bigger than the number of equal letters at the same index: the
virus deleted and inserted letters, so after the first change the two strains are shifted relative to
each other, and letters that match are then found at different positions (for example the letters of A
before the first deletion and the letters of B after it still belong to the same subsequence). The
shared core of my strains is 1027 letters, while only part of the strains stands at the same place.

Mission 3. What does the prefix function tell you, and why does the pointer in the text never goes back in KMP? What is the Border length of my marker and what does it mean?

The prefix function pi[i] gives the length of the longest border of the marker prefix p[0..i], that is
the longest piece that is both a beginning and an ending of it (a "proper" one, shorter than the
prefix itself). In KMP I keep k, the number of marker letters that are matched at the moment; when a
letter does not fit I do not move the text pointer back, I only shorten the part of the marker I still
believe in, and the only length worth jumping to is pi[k-1]: every other possible continuation would be
a border of the matched part as well, and pi points to the longest one, so nothing is lost and the text
is read exactly once. The Border length of my marker is 3 (my marker is AAGAGAAG: it starts and ends with the same three
letters "AAG"), so after a mismatch I can fall back three letters instead of starting from zero,
and the marker can overlap with itself.

Mission 4. Why must you compare the letters after the hashes are equal? What happens to the cost of Rabin-Karp if many windows have the same hash?

Two different words can have the same hash, because the hash is only the remainder of the number of the
word modulo MOD: a collision is possible, and then the hash table gives a false alarm. So after a hit I
compare the letters of the two windows one by one and I count the position only when they are really
equal. If many windows share the same hash (for example with a very small MOD, as in the lab console),
then every one of them needs a comparison of up to k letters, and the time grows from O(n + m) to
something like O(number of windows with the same hash * k); in the worst case (all hashes equal) it
becomes as slow as the naive search, O(n * m * k).

Mission 5. What are the two states of a person in your DP, and how do you get the answer for a person from its children? Why does a greedy "test the person with most links first" not always give the cheapest answer?

For every person v I keep two numbers for the subtree of v: dpTested[v], the cheapest cost when v is
tested, and dpNotTested[v], the cheapest cost when v is not tested. If v is tested, the links to his
children are already covered, so every child may take the cheaper of its two states
(dpTested[v] = cost[v] + sum of min(dpTested[u], dpNotTested[u])); if v is not tested, then the link
v-u can only be covered by the child, so all children have to be tested
(dpNotTested[v] = sum of dpTested[u]). The answer is the smaller of the two states of person 0, and the
tested people are rebuilt by walking down from the root with the same rule. A greedy rule that tests
the person with the most links first does not look at the subtree below that person and at the prices
there: one person with five links but price 50 is worse than five neighbours with price 1 each, while
the greedy rule always takes the big one, so it can pay much more than the optimal answer.

## AI use

The syllabus requires you to say which AI tools you used and for what. Write the tool (ChatGPT, Copilot, other) and what you asked it: explain an idea, find an error, review code. Write "none" if you did not use any. If you took code or text from a book or a website, give the source here.

I used MiniMax Code (MCode) as a helper for this assignment. I asked it to explain the ideas of the
five missions (the tables d[i][j], the prefix function and KMP, the rolling hash of Rabin-Karp and the
DP on the tree), to write the five methods in `src/Strains.java`, `src/Marker.java` and
`src/Outbreak.java`, to find and fix the bug in the reconstruction of the tested people (the rule "the
parent is not tested, so the child must be tested" was overwritten), and to fill in this README
(the output, the answers, the timings and the complexity table). The algorithms, the code and the
words are the standard ones of the lectures; no code was taken from a book or from a website.

## Timing (Mission 7)

Milliseconds, average of 3 runs, from `java -cp out Bench`.

| test | size | time |
|---|---|---|
| naive search | 20 000 / 40 000 / 80 000 | 59,1 / 97,8 / 202,0 |
| KMP | 20 000 / 40 000 / 80 000 | 2,9 / 0,8 / 0,4 |
| edit distance | 500 / 1000 / 2000 / 4000 | 6,3 / 7,8 / 20,9 / 84,0 |

What do the numbers say about how the time grows? When you double the size, by how much does each time grow? Two or three sentences.

The naive search doubles its time when the text doubles (59 -> 98 -> 202 ms), because it tries every
starting position and compares the pattern again and again, that is O(n * m) with a fixed pattern.
KMP stays at about one millisecond and does not grow with the text at all: the text is read once and
the pattern pointer only jumps back over the borders, that is O(n + m), and the numbers are so small
that they mostly measure the start-up of the JVM (the first run is even the slowest one, because the
JIT has not compiled the method yet). The edit distance grows about four times when the length is
doubled (6 -> 8 -> 21 -> 84 ms): the table has n * m cells, so the time is quadratic in the length.

## Complexity

n is the length of the text (or of a strain), m the length of the pattern, k the length of the window, v the number of people. Fill in every algorithm you wrote.

| algorithm | time | space | assumptions |
|---|---|---|---|
| Edit distance | O(n * m) | O(n * m) | the whole table d is kept, every cell needs three neighbours |
| LCS | O(n * m) | O(n * m) | the same table, the rule of the cell is different |
| Prefix function and KMP | O(m) + O(n + m) = O(n + m) | O(m) for the table, plus the list of hits | the text is read once, the pointer never goes back |
| Rabin-Karp (shared words) | O(n + m) normally, O(n * k) per false alarm | O(m) for the table of windows of B, plus O(k) for the powers | the hash of a window is updated in O(1); the letters are compared only when the hashes are equal |
| Tree DP | O(v) | O(v) (two arrays, plus the recursion stack) | the tree is rooted in person 0 and every person is visited once |

## Before you submit

- The four files in `data` and the screenshot `output/outbreak.png` are in the repository.
- `java tools/Check.java` shows all examples right.
- This README is filled in, the program output and the section AI use included.
- The work is pushed to main (at least one commit per mission) and the repository is public.
- The link is attached in Moodle.
