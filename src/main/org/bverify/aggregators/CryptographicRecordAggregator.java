package org.bverify.aggregators;

import org.bverify.records.Record;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import edu.rice.historytree.AggregationInterface;

/**
 * This class is used to cryptographically aggregate data stored in records.
 * It tracks two aggregates: netAmount - the net value of the records in the (sub)tree
 * and totalAmount - the total value of records in the (sub)tree. These values 
 * are calculated recursively and included as input to cryptographic hash function
 * (SHA-256) in the intermediate nodes.
 * 
 * 
 * See {@code RecordAggregation} for details
 * 
 * NOTE that because of the way the code is structured whenever we parse a tree
 * we will parse the raw bytes using this code. That means that if the records are 
 * manipulated we will compute the 'true' aggregation values which will be 
 * different than any previously stored value. This allows us to detect tampering
 *  
 * @author henryaspegren
 *
 */
public class CryptographicRecordAggregator implements AggregationInterface<RecordAggregation, Record> {

	public String getName() {
		return "CryptoRecordAgg";
	}

	public String getConfig() {
		return "";
	}
	
	public AggregationInterface<RecordAggregation, Record> setup(String config) {
		return this;
	}

	public RecordAggregation aggChildren(RecordAggregation leftAnn, RecordAggregation rightAnn) {
		return new RecordAggregation(leftAnn, rightAnn);
	}

	public RecordAggregation aggVal(Record event) {
		return new RecordAggregation(event);
	}

	public RecordAggregation emptyAgg() {
		return new RecordAggregation();
	}

	
	/** 
	 * Currently these are done using Java Serialization but 
	 * Google protobuf is much faster and smaller
	 * TODO: Port to google protobuf
	 */
	public ByteString serializeVal(Record val) {
		//System.out.println("SERIALIZING VAL :"+ val.toString());
		byte[] byteRep = val.serializeRecord();
		return ByteString.copyFrom(byteRep);
	}

	public ByteString serializeAgg(RecordAggregation agg) {
		//System.out.println("SERIALIZING AGG :"+ agg.toString());
		byte[] byteRep = agg.serializatRecordAggregation();
		return ByteString.copyFrom(byteRep);
	}


	public RecordAggregation parseAgg(ByteString b) {
		RecordAggregation recordAgg = new RecordAggregation();
		try {
			recordAgg.parseFrom(b.toByteArray());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return recordAgg;
		
	}

	public Record parseVal(ByteString b) {
		Record r;
		try {
			r = Record.parseRecord(b.toByteArray());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			throw new RuntimeException("Fatal serialization error");
		}
		return r;
	}

	public AggregationInterface<RecordAggregation, Record> clone() {
		// this aggregator is stateless
		return this;
	}


}
