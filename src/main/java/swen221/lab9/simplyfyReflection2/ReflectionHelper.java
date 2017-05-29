package swen221.lab9.simplyfyReflection2;

import java.lang.reflect.InvocationTargetException;

public class ReflectionHelper {
    public static interface SupplierWithException<T> {
        /*FIXME you may want to change code here*/
        T get() throws Throwable;
    }

    public static <T> T tryCatch(SupplierWithException<T> s) {
        /*FIXME add here the try-catching logic as from the text*/ return null;
    }
}
