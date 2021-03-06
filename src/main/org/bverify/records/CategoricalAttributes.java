package org.bverify.records;

import java.io.Serializable;
import java.util.BitSet;

import org.bverify.serialization.BverifySerialization;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Wrapper class for manipulating categorical attributes
 * For now the categorical attributes are indexed by  
 *  [0, .... , NUM_ATTRIBUTES - 1]
 * @author henryaspegren
 *
 */
public class CategoricalAttributes implements Serializable {

	private static final long serialVersionUID = 1L;

	// Minimum possible representation size!
	private final BitSet representation;
	
	// default number of categorical attributes
	public static final int DEFAULT_NUM_CATEGORICAL_ATTRIBUTES = 64;
	
	private final int size;
	
	/**
	 * Creates the default categorical attributes 
	 * which are all set to false
	 */
	public CategoricalAttributes() {
		this.representation = new BitSet(DEFAULT_NUM_CATEGORICAL_ATTRIBUTES);
		this.size = DEFAULT_NUM_CATEGORICAL_ATTRIBUTES;
	}
	
	public CategoricalAttributes(int numberAttributes) {
		this.representation = new BitSet(numberAttributes);
		this.size = numberAttributes;
	}
	
	public CategoricalAttributes(CategoricalAttributes copy) {
		this.representation = (BitSet) copy.representation.clone();
		this.size = copy.size;
	}
	
	public boolean getAttribute(int attributeIdx) {
		return this.representation.get(attributeIdx);
	}
	
	public void setAttribute(int attributeIdx, boolean value) {
		this.representation.set(attributeIdx, value);
	}
	
	
	public BverifySerialization.CategoricalAttributes serializeCategoricalAttributes(){
		BverifySerialization.CategoricalAttributes.Builder res = BverifySerialization.CategoricalAttributes.newBuilder();
		res.setAttributes(ByteString.copyFrom(this.representation.toByteArray()));
		res.setSize(this.size);
		return res.build();
	}
	
	public static CategoricalAttributes parseCategoricalAttributes(byte[] data) throws InvalidProtocolBufferException {
		BverifySerialization.CategoricalAttributes message = BverifySerialization.CategoricalAttributes.parseFrom(data);
		ByteString bitsetbytes = message.getAttributes();
		BitSet bitset = BitSet.valueOf(bitsetbytes.toByteArray());
		int size = message.getSize();
		CategoricalAttributes res = new CategoricalAttributes(size);
		for(int i = 0; i < res.numberOfAttributes(); i++) {
			res.setAttribute(i, bitset.get(i));
		}
		return res;
	}
	
	
	public byte[] toByteArray() {
		return this.representation.toByteArray();
	}
	
	/**
	 * Checks to see if this categorical attributes 
	 * has all of the attributes in the filter (satisfies 
	 * the filter)
	 * @param filter
	 * @return
	 */
	public boolean hasAttributes(CategoricalAttributes filter) {
		CategoricalAttributes andRes = this.and(filter);
		// if and is equal to filter than this categorical attributes
		// has 1 for all of the 1s in filters
		return andRes.equals(filter);
	}
	
	/**
	 * Creates a new categorical attributes by logically ORing this 
	 * categorical attribute with another set of categorical attributes.
	 * An attribute will be set to true if it is true in this or in the other. Must 
	 * have same number of attributes or throws a runtime error. Does 
	 * NOT mutate this
	 * @param other
	 */
	public CategoricalAttributes or(CategoricalAttributes other) {
		if(this.numberOfAttributes() != other.numberOfAttributes()) {
			throw new RuntimeException("Error - Trying to OR two Categorical"
					+ "Attributes with Different Numbers of Attributes!");
		}
		CategoricalAttributes newcatatt = new CategoricalAttributes(this);
		newcatatt.representation.or(other.representation);
		return newcatatt;
	}
	
	/**
	 * Creates a new categorical attributes by logically ANDing this 
	 * categorical attribute with another set of categorical attributes. Must 
	 * have the same number of attributes or will throw a runtime error. Does NOT 
	 * mutate this.
	 * @param other
	 * @return
	 */
	public CategoricalAttributes and(CategoricalAttributes other) {
		if(this.numberOfAttributes() != other.numberOfAttributes()) {
			System.out.println(this.size);
			System.out.println(other.size);
			throw new RuntimeException("Error - Trying to AND two Categorical"
					+ "Attributes with Different Numbers of Attributes!");	
		}
		CategoricalAttributes newcatatt = new CategoricalAttributes(this);
		newcatatt.representation.and(other.representation);
		return newcatatt;
	}
	
	/**
	 * Creates a new categorical attributes by logically XORing this 
	 * categorial attribute with another set of categorical attributes. Must
	 * have the same number of attributes or will throw a runtime error. Does 
	 * NOT mutate this!
	 * @param other
	 * @return
	 */
	public CategoricalAttributes xor(CategoricalAttributes other) {
		if(this.numberOfAttributes() != other.numberOfAttributes()) {
			throw new RuntimeException("Error - Trying to AND two Categorical"
					+ "Attributes with Different Numbers of Attributes!");	
		}
		CategoricalAttributes newcatatt = new CategoricalAttributes(this);
		newcatatt.representation.xor(other.representation);
		return newcatatt;
	}	
	public int numberOfAttributes() {
		return this.size;
	}
	
	@Override
	public int hashCode() {
		return this.representation.hashCode();
	}
	
	@Override
	public boolean equals(Object arg0) {
		if( arg0 instanceof CategoricalAttributes) {
			CategoricalAttributes arg0cast = (CategoricalAttributes) arg0;
			return this.representation.equals(arg0cast.representation) && 
					this.size == arg0cast.size;
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder message = new StringBuilder();
		message.append("<Categorical Attributes:");
		message.append(this.representation);
		message.append(">");
		return message.toString();
	}
	
}
