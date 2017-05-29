package swen221.lab9.flatMapFilterClass1;

import java.util.function.Function;
import java.util.stream.Stream;

public class FilterClass {
    public static <T, R> Function<T, Stream<R>> isInstanceOf(Class<R> clazz) {
        /*
         * produce a Function so that if the parameter is not instance of clazz
         * "Stream.empty()" is returned, otherwise use "Stream.of(..)" to return
         * the appropriately casted parameter.
         */
        return (T item) -> {
        	if (clazz.isInstance(item)) {
				// noinspection unchecked
				return Stream.of((R) item);
			} else {
        		return Stream.empty();
			}
		};
    }
}
