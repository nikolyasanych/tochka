
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.IntStream;


public class MainTwo {
    // Константы для символов ключей и дверей
    private static final char[] KEYS_CHAR = new char[26];
    private static final char[] DOORS_CHAR = new char[26];


    static {
        for (int i = 0; i < 26; i++) {
            KEYS_CHAR[i] = (char)('a' + i);
            DOORS_CHAR[i] = (char)('A' + i);
        }
    }


    // Чтение данных из стандартного ввода
    private static char[][] getInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = new ArrayList<>();
        String line;


        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }


        char[][] maze = new char[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            maze[i] = lines.get(i).toCharArray();
        }


        return maze;
    }


    private static int solve(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        List<int[]> starts = new ArrayList<>();
        int totalKeys = 0;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                if (grid[i][j] == '@') {
                    starts.add(new int[]{i, j});
                } else if (Character.isLowerCase(grid[i][j])) {
                    totalKeys |= 1 << (grid[i][j] - 'a');
                }
            }
        }
        class State {
            int[][] positions;
            int keys;
            int steps;
            State(int[][] pos, int keys, int steps) {
                this.positions = pos;
                this.keys = keys;
                this.steps = steps;
            }
            String getHash() {
                StringBuilder sb = new StringBuilder();
                for (int[] p : positions) {
                    sb.append(p[0]).append(',').append(p[1]).append(';');
                }
                sb.append('|').append(keys);
                return sb.toString();
            }
        }
        Queue<State> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        int[][] startPositions = starts.toArray(new int[4][2]);
        State startState = new State(startPositions, 0, 0);
        queue.offer(startState);
        visited.add(startState.getHash());
        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (current.keys == totalKeys) {
                return current.steps;
            }
            for (int r = 0; r < 4; ++r) {
                int[] pos = current.positions[r];
                for (int d = 0; d < 4; ++d) {
                    int nx = pos[0] + dx[d];
                    int ny = pos[1] + dy[d];
                    if (nx < 0 || ny < 0 || nx >= rows || ny >= cols) continue;
                    char cell = grid[nx][ny];
                    if (cell == '#') continue;
                    if (Character.isUpperCase(cell) && ((current.keys >> (cell - 'A')) & 1) == 0) {
                        continue;
                    }
                    int newKeys = current.keys;
                    if (Character.isLowerCase(cell)) {
                        newKeys |= 1 << (cell - 'a');
                    }
                    int[][] newPositions = new int[4][2];
                    for (int i = 0; i < 4; ++i) {
                        if (i == r) {
                            newPositions[i][0] = nx;
                            newPositions[i][1] = ny;
                        } else {
                            newPositions[i][0] = current.positions[i][0];
                            newPositions[i][1] = current.positions[i][1];
                        }
                    }
                    State newState = new State(newPositions, newKeys, current.steps + 1);
                    String hash = newState.getHash();
                    if (!visited.contains(hash)) {
                        visited.add(hash);
                        queue.offer(newState);
                    }
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    public static void main(String[] args) throws IOException {
        char[][] data = getInput();
        int result = solve(data);

        if (result == Integer.MAX_VALUE) {
            System.out.println("No solution found");
        } else {
            System.out.println(result);
        }
    }
}