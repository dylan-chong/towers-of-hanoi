package swen221.lab9.simplyfyReflection2;

import java.lang.reflect.InvocationTargetException;

public class ReflectionHelper {
    public static interface SupplierWithException<T> {
        T get() throws IllegalAccessException,
				NoSuchMethodException,
				SecurityException,
				InvocationTargetException;
    }

    public static <T> T tryCatch(SupplierWithException<T> s) {
		try {
			return s.get();
		} catch (IllegalAccessException | NoSuchMethodException | SecurityException e) {
			throw new Error(e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;
			else if (cause instanceof Error)
				throw (Error) cause;
			throw new Error(cause);
		}
	}
}
