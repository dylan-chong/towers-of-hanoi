package main.gui.menu;

import main.gui.GUICardView;
import main.gui.GUICardName;

import java.awt.event.ActionEvent;

public class MenuGUIController {

	private final MenuModel menuModel;
	private final GUICardView guiCardView;

	public MenuGUIController(MenuModel menuModel, GUICardView guiCardView) {
		this.menuModel = menuModel;
		this.guiCardView = guiCardView;
	}

	public void beginGameButtonClicked(ActionEvent actionEvent) {
		guiCardView.setCurrentView(GUICardName.GAME);
	}

	public void infoButtonClicked(ActionEvent actionEvent) {
		// TODO
	}

	public void quitButtonClicked(ActionEvent actionEvent) {
		guiCardView.quit();
	}
}
