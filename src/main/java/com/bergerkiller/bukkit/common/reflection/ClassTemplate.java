package com.bergerkiller.bukkit.common.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Uses reflection to transfer/copy all the fields of a class
 */
public class ClassTemplate<T> {
	private final Class<T> type;
	private final List<Field> fields;

	private ClassTemplate(Class<T> type) {
		this.type = type;
		this.fields = new ArrayList<Field>();
		if (this.type != null) {
			try {
				this.fillFields(type);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private void fillFields(Class<?> clazz) {
		if (clazz == null) {
			return;
		}
		for (Field field : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			field.setAccessible(true);
			this.fields.add(field);
		}
		this.fillFields(clazz.getSuperclass());
	}

	/**
	 * Gets the Class type represented by this Template
	 * 
	 * @return Class type
	 */
	public Class<T> getType() {
		return this.type;
	}

	/**
	 * Checks whether a given object is an instance of the class represented by this Template
	 * 
	 * @param object to check
	 * @return True if the object is an instance, False if not
	 */
	public boolean isInstance(Object object) {
		return this.type.isInstance(object);
	}

	/**
	 * Checks whether the object class equals the class represented by this Template
	 * 
	 * @param object to check
	 * @return True if the object is a direct instance, False if not
	 */
	public boolean isType(Object object) {
		return object != null && isType(object.getClass());
	}

	/**
	 * Checks whether a given class instance equals the class represented by this Template
	 * 
	 * @param clazz to check
	 * @return True if the clazz is not null and equals the class of this template
	 */
	public boolean isType(Class<?> clazz) {
		return clazz != null && this.type.equals(clazz);
	}

	/**
	 * Transfers all the fields from one class instance to the other
	 * 
	 * @param from instance
	 * @param to instance
	 */
	public void transfer(T from, T to) {
		for (Field field : this.fields) {
			try {
				field.set(to, field.get(from));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Checks whether this Class Template is properly initialized and can be used
	 * 
	 * @return True if this template is valid for use, False if not
	 */
	public boolean isValid() {
		return this.type != null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(500);
		builder.append("Class path: ").append(this.getType().getName()).append('\n');
		builder.append("Fields (").append(this.fields.size()).append("):").append('\n');
		for (Field field : this.fields) {
			builder.append("    ");
			if (Modifier.isStatic(field.getModifiers())) {
				builder.append("static ");
			}
			builder.append(field.getType().getName()).append(" ").append(field.getName());
			builder.append('\n');
		}
		return builder.toString();
	}
	
	/**
	 * Attempts to find the field by name
	 * 
	 * @param name of the field
	 * @return field
	 */
	public <K> SafeField<K> getField(String name) {
		return new SafeField<K>(this.getType(), name);
	}

	/**
	 * Attempts to find the method by name
	 * 
	 * @param name of the method
	 * @param arguments of the method
	 * @return method
	 */
	public <K> SafeMethod<K> getMethod(String name, Class<?>... parameterTypes) {
		return new SafeMethod<K>(this.getType(), name, parameterTypes);
	}

	/**
	 * Attempts to create a new template for the class at the path specified
	 * 
	 * @param path to the class
	 * @return a new template, or null if the template could not be made
	 */
	public static ClassTemplate<?> create(String path) {
		return create(CommonUtil.getClass(path));
	}

	/**
	 * Attempts to create a new template for the class of the class instance specified<br>
	 * If something fails, an empty instance is returned
	 * 
	 * @param value of the class to create the template for
	 * @return a new template
	 */
	@SuppressWarnings("unchecked")
	public static <T> ClassTemplate<T> create(T value) {
		return create((Class<T>) value.getClass());
	}

	/**
	 * Attempts to create a new template for the class specified<br>
	 * If something fails, an empty instance is returned
	 * 
	 * @param clazz to create
	 * @return a new template
	 */
	public static <T> ClassTemplate<T> create(Class<T> clazz) {
		return new ClassTemplate<T>(clazz);
	}
}