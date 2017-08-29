package main.gui.game;

public interface StateHooks {
    void onEnter();
    void onExit();

    class Adapter implements StateHooks {

		@Override
		public void onEnter() {
			// Do nothing
		}

		@Override
		public void onExit() {
			// Do nothing
		}
	}
}
