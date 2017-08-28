package main.gui.game;

import aurelienribon.slidinglayout.*;
import main.GameUtils;
import main.gamemodel.InvalidMoveException;
import main.gamemodel.Player;
import main.gamemodel.cells.Cell;
import main.gamemodel.cells.PieceCell;
import main.gui.game.celldrawers.CellDrawer;
import main.gui.game.celldrawers.cellcanvas.GridCanvas;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * All the components for displaying the data for a given player
 */
public class PlayerComponents {

    public final SLPanel createPanel;
    public final GridCanvas createCreationCanvas;
    public final GridCanvas createRotationsCanvas;
    public final GridCanvas cemeteryCanvas;
    private final SLConfig createCreationConfig;
    private final SLConfig createRotationConfig;

	private final GameGUIModel gameGUIModel;
	private final CellDrawer cellDrawer;
	private final Player player;

	private SLConfig currentCreateConfig;

	public PlayerComponents(
			Player player,
			GameGUIController gameGUIController,
			GameGUIModel gameGUIModel,
			CellDrawer cellDrawer
	) {
		this.gameGUIModel = gameGUIModel;
		this.cellDrawer = cellDrawer;
		this.player = player;

		String name = player.getPlayerCell().getToken().name();

        createCreationCanvas = newGridCanvas(
                player.getUnusedPieces()::values,
                String.format("Creatable (%s)", name),
                gameGUIController::onCreationCellClick
        );
        createRotationsCanvas = newGridCanvas(
        		this::getRotatedCopiesOfSelectedPiece,
                String.format("Select Rotation (%s)", name),
                gameGUIController::onCreationRotationCellClick
        );
		cemeteryCanvas = newGridCanvas(
				player.getDeadPieces()::values,
				String.format("Dead (%s)", name),
				(cell, mouseEvent) -> {}
		);

		// Animation panel

        createPanel = new SLPanel();
        createPanel.setPreferredSize(createCreationCanvas.getPreferredSize());
        createPanel.setTweenManager(SLAnimator.createTweenManager());
        createCreationConfig = new SLConfig(createPanel)
                .row(1f)
                .col(1f)
                .place(0, 0, createCreationCanvas);
        createRotationConfig = new SLConfig(createPanel)
                .row(1f)
                .col(1f)
                .place(0, 0, createRotationsCanvas);
        currentCreateConfig = createCreationConfig;
        createPanel.initialize(createCreationConfig);
	}

	private Collection<? extends Cell> getRotatedCopiesOfSelectedPiece() {
		if (gameGUIModel.getGuiState() != GUIState.CREATE_PIECE_ROTATION
				 || gameGUIModel.getCurrentPlayer() != player) {
			return Collections.emptyList();
		}

		PieceCell baseCell = gameGUIModel.getCreationSelectedCell();
		if (baseCell == null) {
			return Collections.emptyList();
		}

		try {
			return gameGUIModel.calculateRotatedCopiesOfSelectedCell();
		} catch (InvalidMoveException e) {
			// Shouldn't happen
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	public List<? extends JComponent> getAllComponents() {
        return Arrays.asList(createPanel, cemeteryCanvas);
    }

    public void showCreateCreationCanvas() {
        if (currentCreateConfig == createCreationConfig) {
            return;
        }

        currentCreateConfig = createCreationConfig;

        SLKeyframe keyframe =
                new SLKeyframe(createCreationConfig, GameGUIView.TRANSITION_DURATION)
                        .setStartSide(SLSide.LEFT, createCreationCanvas)
                        .setEndSide(SLSide.RIGHT, createRotationsCanvas);
        createPanel.createTransition()
                .push(keyframe)
                .play();
    }

    public void showCreateRotationCanvas() {
        if (currentCreateConfig == createRotationConfig) {
            return;
        }

        currentCreateConfig = createRotationConfig;

        SLKeyframe keyframe =
                new SLKeyframe(createRotationConfig, GameGUIView.TRANSITION_DURATION)
                        .setStartSide(SLSide.RIGHT, createRotationsCanvas)
                        .setEndSide(SLSide.LEFT, createCreationCanvas);
        createPanel.createTransition()
                .push(keyframe)
                .play();
    }

    private GridCanvas newGridCanvas(
            Supplier<Collection<? extends Cell>> cellGetter,
            String title,
            BiConsumer<Cell, MouseEvent> onClickHandler
    ) {
        return new GridCanvas(
                (columns) -> GameUtils.packCells(
                        cellGetter.get()
                                .stream()
                                .sorted()
                                .collect(Collectors.toList()),
                        columns
                ),
                gameGUIModel,
                cellDrawer,
                title
        ) {
            @Override
            protected void onCellClick(Cell cell, MouseEvent e) {
                onClickHandler.accept(cell, e);
            }
        };
    }

}
