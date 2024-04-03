package game;

import java.awt.Color;

public class PerimeterGoal extends Goal {

	public PerimeterGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		int count = 0;
		Color[][] colorBoard = board.flatten();

		for (int i = 0; i < colorBoard.length; i++) {
			for (int j = 0; j < colorBoard[i].length; j++) {
				if (i == 0 || i == colorBoard.length - 1) {
					if (colorBoard[i][j] != null && colorBoard[i][j].equals(targetGoal))
						count++;
				}
				if (j == 0 || j == colorBoard.length - 1) {
					if (colorBoard[i][j] != null && colorBoard[i][j].equals(targetGoal))
						count++;
				}
			}
		}
		return count;
	}

	@Override
	public String description() {
		return "Place the highest number of " + GameColors.colorToString(targetGoal)
				+ " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
	}

}
