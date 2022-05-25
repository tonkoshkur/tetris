package ua.tonkoshkur.tetris.factory;

import java.util.Random;

import ua.tonkoshkur.tetris.model.Block;
import ua.tonkoshkur.tetris.model.BlockBackZ;
import ua.tonkoshkur.tetris.model.BlockL;
import ua.tonkoshkur.tetris.model.BlockO;
import ua.tonkoshkur.tetris.model.BlockP;
import ua.tonkoshkur.tetris.model.BlockQ;
import ua.tonkoshkur.tetris.model.BlockT;
import ua.tonkoshkur.tetris.model.BlockZ;

public class BlockFactory {

    private static final Random RANDOM = new Random();

    public static Block getRandomBlock(int squareSize) {
        Block.BlockShape blockShape = getRandomBlockShape();
        return getBlock(blockShape, squareSize);
    }

    public static Block.BlockShape getRandomBlockShape() {
        Block.BlockShape[] blockShapes = Block.BlockShape.values();
        int blockShapeId = RANDOM.nextInt(blockShapes.length);
        return blockShapes[blockShapeId];
    }

    public static Block getBlock(Block.BlockShape blockShape, int squareSize) {
        switch (blockShape) {
            case BACK_Z:
                return new BlockBackZ(squareSize);
            case L:
                return new BlockL(squareSize);
            case O:
                return new BlockO(squareSize);
            case P:
                return new BlockP(squareSize);
            case Q:
                return new BlockQ(squareSize);
            case T:
                return new BlockT(squareSize);
            case Z:
            default:
                return new BlockZ(squareSize);
        }
    }
}
