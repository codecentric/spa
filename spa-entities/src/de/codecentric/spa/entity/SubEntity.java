package de.codecentric.spa.entity;

import de.codecentric.spa.annotations.Id;
import de.codecentric.spa.annotations.Transient;

@de.codecentric.spa.annotations.Entity
public class SubEntity {

	@Id
	public long id;

	public String name;
	public boolean isOrIsNot;
	public int someInt;

	@Transient
	public String notPersisted;

	public double subFldDbl;

	public byte b;
	public Byte b1;
	public byte[] bArr;
	public short sh;
	public Short sh1;
	public int i;
	public Integer i1;
	public long l;
	public Long l1;
	public float f;
	public Float f1;
	public double d;
	public Double d1;
	public char c;
	public Character c1;

}
