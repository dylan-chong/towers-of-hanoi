package main.gui.menu;

import main.gui.GUICardManager;
import main.gui.GUICardName;

import java.awt.event.ActionEvent;

public class MenuGUIController {

	private final MenuModel menuModel;
	private final GUICardManager guiCardManager;

	public MenuGUIController(MenuModel menuModel, GUICardManager guiCardManager) {
		this.menuModel = menuModel;
		this.guiCardManager = guiCardManager;
	}

	public void beginGameButtonClicked(ActionEvent actionEvent) {
		guiCardManager.setCurrentView(GUICardName.GAME);
	}

	public void infoButtonClicked(ActionEvent actionEvent) {
		// TODO
	}

	public void quitButtonClicked(ActionEvent actionEvent) {
		guiCardManager.quit();
	}
}
