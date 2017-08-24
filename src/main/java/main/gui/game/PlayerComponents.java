package main.gui.game;

import aurelienribon.slidinglayout.*;
import main.GameUtils;
import main.gamemodel.GameModel;
import main.gamemodel.Player;
import main.gamemodel.cells.Cell;
import main.gui.game.drawersandviews.CellDrawer;
import main.gui.game.drawersandviews.cellcanvas.GridCanvas;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class PlayerComponents {

    public final SLPanel createPanel;
    public final GridCanvas createCreationCanvas;
    public final GridCanvas createRotationsCanvas;
    public final GridCanvas cemeteryCanvas;
    private final SLConfig createCreationConfig;
    private final SLConfig createRotationConfig;

	private final GameModel gameModel;
	private final CellDrawer cellDrawer;

    private SLConfig currentCreateConfig;

	public PlayerComponents(
			Player player,
			GameGUIController gameGUIController,
			GameModel gameModel,
			CellDrawer cellDrawer
	) {
		this.gameModel = gameModel;
		this.cellDrawer = cellDrawer;
		String name = player.getPlayerCell().getToken().name();

        createCreationCanvas = newGridCanvas(
                player.getUnusedPieces()::values,
                String.format("Creatable (%s)", name),
                gameGUIController::onCreationCellClick
        );
        createRotationsCanvas = newGridCanvas(
                player.getUnusedPieces()::values,// TODO
                String.format("Select Rotation (%s)", name),
                gameGUIController::onCreationRotationCellClick
        );
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

        cemeteryCanvas = newGridCanvas(
                player.getDeadPieces()::values,
                String.format("Dead (%s)", name),
                (cell, mouseEvent) -> {}
        );
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
                new SLKeyframe(createCreationConfig, GameGUI.TRANSITION_DURATION)
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
                new SLKeyframe(createRotationConfig, GameGUI.TRANSITION_DURATION)
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
                gameModel,
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
