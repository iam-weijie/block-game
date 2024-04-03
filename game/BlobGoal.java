package game;

import java.awt.Color;

public class BlobGoal extends Goal {

	public BlobGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		Color[][] unitCells = board.flatten();
		boolean[][] visited = new boolean[unitCells.length][unitCells[0].length];
		int maxBlobSize = 0;

		for (int i = 0; i < unitCells.length; i++) {
			for (int j = 0; j < unitCells[i].length; j++) {
				if (!visited[i][j] && unitCells[i][j].equals(targetGoal)) {
					int blobSize = undiscoveredBlobSize(i, j, unitCells, visited);

					if (blobSize > maxBlobSize)
						maxBlobSize = blobSize;
				}
			}
		}
		return maxBlobSize;
	}

	@Override
	public String description() {
		return "Create the largest connected blob of " + GameColors.colorToString(targetGoal)
				+ " blocks, anywhere within the block";
	}

	public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
		if (i < 0 || i >= unitCells.length || j < 0 || j >= unitCells[i].length || visited[i][j])
			return 0;

		visited[i][j] = true;

		if (!unitCells[i][j].equals(targetGoal))
			return 0;

		int size = 1;
		size += undiscoveredBlobSize(i - 1, j, unitCells, visited);
		size += undiscoveredBlobSize(i + 1, j, unitCells, visited);
		size += undiscoveredBlobSize(i, j - 1, unitCells, visited);
		size += undiscoveredBlobSize(i, j + 1, unitCells, visited);
		return size;
	}
}
