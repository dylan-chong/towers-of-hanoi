package main.gamemodel.cells;

/**
 * The action a cell should do when it touches another cell
 */
public enum Reaction {
	DO_NOTHING {
		@Override
		public <T> T getFromMap(Mapper<T> mapper) {
			return mapper.getDoNothingValue();
		}
	},
	GET_KILLED {
		@Override
		public <T> T getFromMap(Mapper<T> mapper) {
			return mapper.getDieValue();
		}
	},
	GET_BUMPED_BACK {
		@Override
		public <T> T getFromMap(Mapper<T> mapper) {
			return mapper.getGetBumpedBackValue();
		}
	},
	LOSE_THE_GAME {
		@Override
		public <T> T getFromMap(Mapper<T> mapper) {
			return mapper.getLoseTheGameValue();
		}
	},
	;

	public abstract <T> T getFromMap(Mapper<T> mapper);

	/**
	 * Visitor style pattern to make sure colleges implemented for every single
	 * enum value
	 * @param <ValueT>
	 */
	public interface Mapper<ValueT> {
		ValueT getDoNothingValue();
		ValueT getDieValue();
		ValueT getGetBumpedBackValue();
		ValueT getLoseTheGameValue();
	}
}
