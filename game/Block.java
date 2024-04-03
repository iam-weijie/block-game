package game;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {
  private int xCoord;
  private int yCoord;
  private int size; // height/width of the square
  private int level; // the root (outer most block) is at level 0
  private int maxDepth;
  private Color color;

  private Block[] children; // {UR, UL, LL, LR}

  public static Random gen = new Random();

  /*
   * These two constructors are here for testing purposes.
   */
  public Block() {
  }

  public Block(int x, int y, int size, int lvl, int maxD, Color c, Block[] subBlocks) {
    this.xCoord = x;
    this.yCoord = y;
    this.size = size;
    this.level = lvl;
    this.maxDepth = maxD;
    this.color = c;
    this.children = subBlocks;
  }

  /*
   * Creates a random block given its level and a max depth.
   * 
   * xCoord, yCoord, size, and highlighted should not be initialized
   * (i.e. they will all be initialized by default)
   */
  public Block(int lvl, int maxDepth) {
    this.level = lvl;
    this.maxDepth = maxDepth;

    // generate random number between [0, 1)
    if (this.level < maxDepth) {
      double randomNumber = gen.nextDouble(1);
      if (randomNumber < Math.exp(-0.25 * this.level)) {
        this.color = null;

        // subdivide
        Block UR = new Block(this.level + 1, maxDepth);
        Block UL = new Block(this.level + 1, maxDepth);
        Block LR = new Block(this.level + 1, maxDepth);
        Block LL = new Block(this.level + 1, maxDepth);
        this.children = new Block[] { UR, UL, LR, LL };
        return;
      }
    }

    int randomInt = gen.nextInt(4);
    this.color = GameColors.BLOCK_COLORS[randomInt];
    this.children = new Block[0];
  }

  /*
   * Updates size and position for the block and all of its sub-blocks, while
   * ensuring consistency between the attributes and the relationship of the
   * blocks.
   * 
   * The size is the height and width of the block. (xCoord, yCoord) are the
   * coordinates of the top left corner of the block.
   */
  public void updateSizeAndPosition(int size, int xCoord, int yCoord) {
    if (this.level == 0 && size != 1) {
      if (size < 0 || size % Math.pow(2, maxDepth) != 0)
        throw new IllegalArgumentException("Size is invalid!");
    }

    this.size = size;
    this.xCoord = xCoord;
    this.yCoord = yCoord;

    if (this.children.length != 0) {
      this.children[0].updateSizeAndPosition(size / 2, xCoord + size / 2, yCoord);
      this.children[1].updateSizeAndPosition(size / 2, xCoord, yCoord);
      this.children[2].updateSizeAndPosition(size / 2, xCoord, yCoord + size / 2);
      this.children[3].updateSizeAndPosition(size / 2, xCoord + size / 2, yCoord + size / 2);
    }
  }

  /*
   * Returns a List of blocks to be drawn to get a graphical representation of
   * this block.
   * 
   * This includes, for each undivided Block:
   * - one BlockToDraw in the color of the block
   * - another one in the FRAME_COLOR and stroke thickness 3
   * 
   * Note that a stroke thickness equal to 0 indicates that the block should be
   * filled with its color.
   * 
   * The order in which the blocks to draw appear in the list does NOT matter.
   */
  public ArrayList<BlockToDraw> getBlocksToDraw() {
    ArrayList<BlockToDraw> list = new ArrayList<>();

    if (this.children.length == 0) {
      BlockToDraw block = new BlockToDraw(this.color, this.xCoord, this.yCoord, this.size, 0);
      BlockToDraw frame = new BlockToDraw(GameColors.FRAME_COLOR, xCoord, yCoord, size, 3);
      list.add(block);
      list.add(frame);
    } else {
      for (Block child : this.children) {
        ArrayList<BlockToDraw> sublist = new ArrayList<>(child.getBlocksToDraw());
        list.addAll(sublist);
      }
    }

    return list;
  }

  /*
   * This method is provided and you should NOT modify it.
   */
  public BlockToDraw getHighlightedFrame() {
    return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
  }

  /*
   * Return the Block within this Block that includes the given location
   * and is at the given level. If the level specified is lower than
   * the lowest block at the specified location, then return the block
   * at the location with the closest level value.
   * 
   * The location is specified by its (x, y) coordinates. The lvl indicates
   * the level of the desired Block. Note that if a Block includes the location
   * (x, y), and that Block is subdivided, then one of its sub-Blocks will
   * contain the location (x, y) too. This is why we need lvl to identify
   * which Block should be returned.
   * 
   * Input validation:
   * - this.level <= lvl <= maxDepth (if not throw exception)
   * - if (x,y) is not within this Block, return null.
   */
  public Block getSelectedBlock(int x, int y, int lvl) {
    if (lvl < this.level || lvl > this.maxDepth)
      throw new IllegalArgumentException("Invalid Level!");

    if (x >= this.xCoord && x < this.xCoord + this.size && y >= this.yCoord && y < this.yCoord + this.size) {
      if (lvl == this.level || this.children.length == 0)
        return this;
      else {
        for (Block child : this.children) {
          Block selectedBlock = child.getSelectedBlock(x, y, lvl);
          if (selectedBlock != null)
            return selectedBlock;
        }
      }
    }
    return null;
  }

  /*
   * Swaps the child Blocks of this Block.
   * If input is 1, swap vertically. If 0, swap horizontally.
   * If this Block has no children, do nothing. The swap
   * should be propagate, effectively implementing a reflection
   * over the x-axis or over the y-axis.
   * 
   */
  public void reflect(int direction) {
    if (direction != 0 && direction != 1) {
      throw new IllegalArgumentException("Input must be 0 or 1!");
    }

    if (this.children.length == 0) {
      return;
    }

    Block UL = this.children[1];
    Block UR = this.children[0];
    Block LR = this.children[3];
    Block LL = this.children[2];

    if (direction == 1) {
      this.children[0] = UL;
      this.children[1] = UR;
      this.children[2] = LR;
      this.children[3] = LL;
    } else {
      this.children[0] = LR;
      this.children[1] = LL;
      this.children[2] = UL;
      this.children[3] = UR;
    }

    this.updateSizeAndPosition(size, xCoord, yCoord);

    for (Block child : this.children) {
      child.reflect(direction);
    }
  }

  /*
   * Rotate this Block and all its descendants.
   * If the input is 1, rotate clockwise. If 0, rotate
   * counterclockwise. If this Block has no children, do nothing.
   */
  public void rotate(int direction) {
    if (direction != 0 && direction != 1) {
      throw new IllegalArgumentException("Input must be 0 or 1!");
    }

    if (this.children.length == 0) {
      return;
    }

    Block UL = this.children[1];
    Block UR = this.children[0];
    Block LL = this.children[2];
    Block LR = this.children[3];

    if (direction == 1) {
      this.children[0] = UL;
      this.children[1] = LL;
      this.children[2] = LR;
      this.children[3] = UR;
    } else {
      this.children[0] = LR;
      this.children[1] = UR;
      this.children[2] = UL;
      this.children[3] = LL;
    }

    this.updateSizeAndPosition(size, xCoord, yCoord);

    for (Block child : this.children) {
      child.rotate(direction);
    }
  }

  /*
   * Smash this Block.
   * 
   * If this Block can be smashed,
   * randomly generate four new children Blocks for it.
   * (If it already had children Blocks, discard them.)
   * Ensure that the invariants of the Blocks remain satisfied.
   * 
   * A Block can be smashed iff it is not the top-level Block
   * and it is not already at the level of the maximum depth.
   * 
   * Return True if this Block was smashed and False otherwise.
   * 
   */
  public boolean smash() {
    if (this.level > 0 && this.level < maxDepth) {
      Block UR = new Block(this.level + 1, maxDepth);
      Block UL = new Block(this.level + 1, maxDepth);
      Block LR = new Block(this.level + 1, maxDepth);
      Block LL = new Block(this.level + 1, maxDepth);
      this.children = new Block[] { UR, UL, LR, LL };
      this.color = null;
      this.updateSizeAndPosition(size, xCoord, yCoord);
      return true;
    } else
      return false;
  }

  /*
   * Return a two-dimensional array representing this Block as rows and columns of
   * unit cells.
   * 
   * Return and array arr where, arr[i] represents the unit cells in row i,
   * arr[i][j] is the color of unit cell in row i and column j.
   * 
   * arr[0][0] is the color of the unit cell in the upper left corner of this
   * Block.
   */
  public Color[][] flatten() {
    Color[][] arr = new Color[(int) Math.pow(2, maxDepth)][(int) Math.pow(2, maxDepth)];

    for (int i = 0; i < Math.pow(2, maxDepth); i++) {
      for (int j = 0; j < Math.pow(2, maxDepth); j++) {
        Block unitCell = getSelectedBlock(j * size / (int) Math.pow(2, maxDepth),
            i * size / (int) Math.pow(2, maxDepth), maxDepth);
        arr[i][j] = (unitCell != null) ? unitCell.color : null;
      }
    }
    return arr;
  }

  // These two get methods have been provided. Do NOT modify them.
  public int getMaxDepth() {
    return this.maxDepth;
  }

  public int getLevel() {
    return this.level;
  }

  /*
   * The next 5 methods are needed to get a text representation of a block.
   * You can use them for debugging. You can modify these methods if you wish.
   */
  public String toString() {
    return String.format("pos=(%d,%d), size=%d, level=%d", this.xCoord, this.yCoord, this.size, this.level);
  }

  public void printBlock() {
    this.printBlockIndented(0);
  }

  private void printBlockIndented(int indentation) {
    String indent = "";
    for (int i = 0; i < indentation; i++) {
      indent += "\t";
    }

    if (this.children.length == 0) {
      // it's a leaf. Print the color!
      String colorInfo = GameColors.colorToString(this.color) + ", ";
      System.out.println(indent + colorInfo + this);
    } else {
      System.out.println(indent + this);
      for (Block b : this.children)
        b.printBlockIndented(indentation + 1);
    }
  }

  private static void coloredPrint(String message, Color color) {
    System.out.print(GameColors.colorToANSIColor(color));
    System.out.print(message);
    System.out.print(GameColors.colorToANSIColor(Color.WHITE));
  }

  public void printColoredBlock() {
    Color[][] colorArray = this.flatten();
    for (Color[] colors : colorArray) {
      for (Color value : colors) {
        String colorName = GameColors.colorToString(value).toUpperCase();
        if (colorName.length() == 0) {
          colorName = "\u2588";
        } else {
          colorName = colorName.substring(0, 1);
        }
        coloredPrint(colorName, value);
      }
      System.out.println();
    }
  }

}
