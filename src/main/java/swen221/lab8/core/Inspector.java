package swen221.lab8.core;

import swen221.lab8.util.Rectangle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsible for interacting with widgets via reflection. This includes
 * reading and writing attributes on the widgets, and creating instances of
 * them.
 * 
 * @author David J. Pearce
 *
 */
public class Inspector {
	/**
	 * Provides the master list of widgets from which we can create new widgets
	 */
	private Class<? extends Widget>[] widgets;

	public Inspector(Class<? extends Widget>... widgets) {
		this.widgets = widgets;
	}

	/**
	 * Return the array of all valid widgets being considered by this inspector.
	 * 
	 * @return
	 */
	public Class<? extends Widget>[] getWidgets() {
		return widgets;
	}

	/**
	 * Construct a new widget of a particular kind with given dimensions.
	 * 
	 * @param name
	 *            The kind of widget to create
	 * @param dimensions
	 *            The dimensions of the widget
	 * @return
	 */
	public Widget newWidget(String name, Rectangle dimensions) {
		Class<?> widgetClass = findWidget(name);
		Constructor<?>[] constructors = widgetClass.getConstructors();

		List<Constructor<?>> validConstructors = Arrays.stream(constructors)
				.filter(constructor ->
						constructor.getParameterTypes().length == 1)
				.filter(constructor ->
						constructor.getParameterTypes()[0].equals(Rectangle.class))
				.collect(Collectors.toList());
		if (validConstructors.size() != 1)
			System.out.println("Size != 1 " + validConstructors);

		// HINT: You should look in the widget class (using reflection) for a
		// constructor which accepts a Rectangle object as a parameter. Then you
		// can just invoke this method passing in the dimensions parameter.
		try {
			return (Widget) validConstructors
					.get(0)
					.newInstance(dimensions);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the value of a given attribute for this widget. This is done my
	 * calling the corresponding (public) getter method.
	 * 
	 * @param widget
	 *            The widget whose attribute we wish to read
	 * @param name
	 *            The name of the attribute to be read
	 * 
	 * @param name
	 * @return
	 */
	public Object getAttributeValue(Widget widget, String name) {
		// To get the value of a given attribute in the widget, you need to use
		// its corresponding "getter" method. For example, for an attribute
		// "color" the getter would be "getColor".
		String getterName = "get" + capitalise(name);
		// What you need to do is find the appropriate getter method and invoke
		// it. This doesn't accept any parameters. Furthermore, you should just
		// through an IllegalArgumentException for all the various error cases
		// which arise when doing this (SecurityException,
		// NoSuchMethodException, etc).
		try {
			return widget.getClass()
                    .getMethod(getterName)
					.invoke(widget);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the value of a given attribute for this widget
	 *
	 * @param widget
	 *            The widget whose attribute we wish to update
	 * @param name
	 *            The name of the attribute to be updated
	 * @param value
	 *            The value of the attribute to be updated
	 */
	public void setAttribute(Widget widget, String name, Object value) {
		// To set the value of a given attribute in the widget, you need to use
		// its corresponding "setter" method. For example, for an attribute
		// "color" the getter would be "SetColor".
		String setterName = "set" + capitalise(name);
		Method[] methods = widget.getClass().getMethods();
		// As before, you need to first find the setter method. This is harder
		// than before because you need to know the type of the attribute being
		// set. To work this out, you need to get the "type" of the corresponding
		// field using Class.getDeclaredField().
		try {
			Class<?> fieldType = widget.getClass()
					.getDeclaredField(name)
					.getType();

			List<Method> validMethods = Arrays.stream(methods)
					.filter(meth -> meth.getName().equals(setterName))
					.filter(meth -> meth.getParameterTypes().length == 1)
					.filter(meth -> meth.getParameterTypes()[0].equals(fieldType))
					.collect(Collectors.toList());
			if (validMethods.size() != 1)
				System.out.println("Size != 1 " + validMethods);

			validMethods.get(0).invoke(widget, value);
		} catch (IllegalAccessException |
				InvocationTargetException |
				NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Make the first letter of the string a captial.
	 * 
	 * @param str
	 * @return
	 */
	private static String capitalise(String str) {
		String rest = str.substring(1);
		char c = Character.toUpperCase(str.charAt(0));
		return c + rest;
	}

	private Class<? extends Widget> findWidget(String widgetName) {
		for (Class<? extends Widget> w : widgets) {
			if (w.getSimpleName().equals(widgetName)) {
				return w;
			}
		}
		throw new IllegalArgumentException("Widget " + widgetName + " does not exist");
	}
}
