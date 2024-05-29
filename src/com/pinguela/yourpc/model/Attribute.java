package com.pinguela.yourpc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract factory class for product attributes.
 * <p><b>The name of each concrete subclass of this abstract factory must be comprised
 * of the subclass' type parameter class name followed by this class' name.</b></p>
 * @param <E> The attribute's data type
 */
public abstract class Attribute<E> 
extends AbstractValueObject 
implements Cloneable, AttributeDataTypes, AttributeValueHandlingModes {
	
	/**
	 * <p>Maps the SQL data type primary key (returned by {@link #getDataTypeIdentifier()})
	 * to the name of the parameterised class used by the attribute instance.</p>
	 * <p><b>This map must contain an entry for every parameterised type used in
	 * a subclass of this abstract factory.</b></p>
	 */
	public static final Map<String, Class<?>> PARAMETERIZED_TYPE_CLASS_NAMES;
	
	static {
		Map<String, Class<?>> classNameMap = new HashMap<String, Class<?>>();
		classNameMap.put(BIGINT, java.lang.Long.class);
		classNameMap.put(VARCHAR, java.lang.String.class);
		classNameMap.put(DECIMAL, java.lang.Double.class);
		classNameMap.put(BOOLEAN, java.lang.Boolean.class);
		PARAMETERIZED_TYPE_CLASS_NAMES = Collections.unmodifiableMap(classNameMap);
	}
	
	private String name;
	private List<AttributeValue<E>> values;

	protected Attribute() {
		values = new ArrayList<AttributeValue<E>>();
	}

	@SuppressWarnings("unchecked")
	public static <T> Attribute<T> getInstance(Class<T> target) {
		
		String packageName = Attribute.class.getPackage().getName();
		String parameterClassName = target.getSimpleName();
		
		String fullyQualifiedSubclassName =
				String.format("%s.%s%s", packageName, parameterClassName, Attribute.class.getSimpleName());
		
		Attribute<T> attribute = null;

		try { 
			attribute = (Attribute<T>) 
					Class.forName(fullyQualifiedSubclassName).getDeclaredConstructor().newInstance(); 
		} catch (Exception e) {

		}
		return attribute;
	}
	
	public static Attribute<?> getInstance(String dataType) {
		
		Attribute<?> attribute = null;

		Class<?> parameterClass = PARAMETERIZED_TYPE_CLASS_NAMES.get(dataType);
		attribute = getInstance(parameterClass);

		return attribute;
	}
	
	@SuppressWarnings("unchecked")
	public final Class<E> getParameterizedTypeClass() {
		return (Class<E>) PARAMETERIZED_TYPE_CLASS_NAMES.get(getDataTypeIdentifier());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AttributeValue<E>> getValues() {
		return values;
	}
	
	public E getValueAt(int index) {
		return values.get(index).getValue();
	}

	@SuppressWarnings("unchecked")
	public boolean addValue(Long id, Object value) {
		
		if (value != null && !getParameterizedTypeClass().isInstance(value)) {
			throw new IllegalArgumentException("Object type does not match type parameter.");
		}
		
		if (id == null && value == null) {
			return false;
		}
		
		AttributeValue<E> attributeValue = new AttributeValue<E>();
		values.add(attributeValue);
		attributeValue.setId(id);
		attributeValue.setValue((E) value);
		return true;
	}
	
	public void addAll(Collection<AttributeValue<E>> newValues) {
		values.addAll(newValues);
	}
	
	public void removeValue(int index) {
		values.remove(index);
	}
	
	public void removeValue(AttributeValue<?> value) {
		values.remove(value);
	}
	
	public void removeAllValues() {
		values.removeAll(values);
	}
	
	/**
	 * @return The data type constant from {@link AttributeDataTypes} corresponding
	 * to the parameterised type set for this attribute instance.
	 */
	public abstract String getDataTypeIdentifier();
	
	/**
	 * @return The constant that determines whether this attribute's values should be
	 * treated as a {@link #RANGE} (with minimum and maximum values), or a {@link #SET}.
	 */
	public abstract int getValueHandlingMode();
	
	@Override
	@SuppressWarnings("unchecked")
	public Attribute<E> clone() {
		try {
			Attribute<E> clone = (Attribute<E>) super.clone();
			clone.removeAllValues();
			
			for (AttributeValue<E> value : this.values) {
				clone.getValues().add(value.clone());
			}
			
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	};
	
	public Attribute<E> trim() {
		if (values.size() > 2 && getValueHandlingMode() == RANGE) {
			AttributeValue<E> min = values.get(0);
			AttributeValue<E> max = values.get(values.size()-1);
			
			values.clear();
			
			values.add(min);
			values.add(max);
		}
		
		return this;
	}
	
}
