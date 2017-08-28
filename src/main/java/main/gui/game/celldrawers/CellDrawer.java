package main.gui.game.celldrawers;

import main.gamemodel.Direction;
import main.gamemodel.Player;
import main.gamemodel.cells.Cell;
import main.gamemodel.cells.PieceCell;
import main.gamemodel.cells.PlayerCell;

import java.awt.*;
import java.util.Arrays;

public class CellDrawer implements
		Cell.Mapper<CellDrawer.DrawFunction> {

	private static final float STROKE_WIDTH = 5;
	private static final Color SWORD_AND_SHIELD_COLOUR = new Color(252, 14, 27);
	private static final float SHIELD_DISTANCE_FROM_CENTRE = 0.40f;
	private static final float SWORD_DISTANCE_FROM_CENTRE = 0.40f;

	private static final float FACE_STROKE_WIDTH = 1.5f;

	private final CellColorProcessor colorProcessor;

	public CellDrawer(CellColorProcessor colorProcessor) {
		this.colorProcessor = colorProcessor;
	}

	@Override
	public DrawFunction valueOfPieceCell(PieceCell cell) {
		return (playerData, graphics2D, col, row, size) -> {
			float[] centre = {
					(row + 0.5f) * size,
					(col + 0.5f) * size,
			};

			// Fill circle in cell

			Color color = playerData.getPlayerCell().getToken().color;
			color = colorProcessor.process(color, cell);
			graphics2D.setPaint(new RadialGradientPaint(
					centre[1],
					centre[0],
					size / 2,
					new float[]{0, 1},
					new Color[]{color.brighter(), color}
			));
			graphics2D.fillOval(
					(int) (col * size),
					(int) (row * size),
					(int) (size),
					(int) (size)
			);

			// Swords and shields

			graphics2D.setPaint(colorProcessor.process(SWORD_AND_SHIELD_COLOUR, cell));
			graphics2D.setStroke(new BasicStroke(STROKE_WIDTH));
			for (Direction sideDir : Direction.values()) {
				PieceCell.SideType side = cell.getSide(sideDir);

				side.getFromMap(new PieceCell.SideType.Mapper<Void>() {
					@Override
					public Void getEmptyValue() {
						return null;
					}

					@Override
					public Void getSwordValue() {
						float[] middleOfSide = sideDir.shift(
								centre,
								SWORD_DISTANCE_FROM_CENTRE * size
						);
						graphics2D.drawLine(
								(int) centre[1],
								(int) centre[0],
								(int) middleOfSide[1],
								(int) middleOfSide[0]
						);
						return null;
					}

					@Override
					public Void getShieldValue() {
						float distance = SHIELD_DISTANCE_FROM_CENTRE;
						float[] middleOfSide = sideDir.shift(centre, distance * size);
						float[] cornerOne = Direction
								.values()[(sideDir.ordinal() + 3)
								% Direction.values().length]
								.shift(middleOfSide, distance * size);
						float[] cornerTwo = Direction
								.values()[(sideDir.ordinal() + 1)
								% Direction.values().length]
								.shift(middleOfSide, distance * size);

						graphics2D.drawLine(
								(int) cornerOne[1],
								(int) cornerOne[0],
								(int) cornerTwo[1],
								(int) cornerTwo[0]
						);
						return null;
					}
				});
			}
		};
	}

	@Override
	public DrawFunction valueOfPlayerCell(PlayerCell cell) {
		return (playerData, graphics2D, col, row, size) -> {
			Color faceColor = playerData.getPlayerCell().getToken().color;
			faceColor = colorProcessor.process(faceColor, cell);
			graphics2D.setColor(faceColor);
			graphics2D.fillOval(
					(int) (col * size),
					(int) (row * size),
					(int) (size),
					(int) (size)
			);

			float[] centre = {
					(col + 0.5f) * size,
					(row + 0.5f) * size,
			};

			graphics2D.setStroke(new BasicStroke(FACE_STROKE_WIDTH));
			graphics2D.setColor(colorProcessor.process(Color.BLACK, cell));
			cell.getToken().getFromMap(new PlayerCell.Token.Mapper<Void>() {
				@Override
				public Void getHappyValue() {
					drawEyes();
					drawMouth(-1);
					return null;
				}

				@Override
				public Void getAngry() {
					drawEyes();
					drawMouth(1);
					return null;
				}

				private void drawEyes() {
					drawEye(-1); // left
					drawEye(1); // right
				}

				private void drawEye(float distFromCenterCol) {
					float[] eye = Arrays.copyOf(centre, centre.length);
					eye[0] += distFromCenterCol * 0.17f * size;
					eye[1] -= 0.17f * size;
					float radius = 0.1f * size;
					graphics2D.drawOval(
							(int) (eye[0] - (radius / 2)),
							(int) (eye[1] - (radius / 2)),
							(int) (radius),
							(int) (radius)
					);
				}

				private void drawMouth(float curviness) {
					float width = 0.4f * size;
					float height = 0.2f * size;
					graphics2D.drawArc(
							(int) (centre[0] - (width / 2)),
							(int) (centre[1] + (0.1f * size)),
							(int) width,
							(int) height,
							0,
							(int) (180 * curviness)
					);
				}
			});
		};
	}

	public interface DrawFunction {
		void draw(
				Player player,
				Graphics2D graphics2D,
				int col,
				int row,
				float size
		);
	}
}
