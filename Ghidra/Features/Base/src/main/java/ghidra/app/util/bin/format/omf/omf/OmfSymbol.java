/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.app.util.bin.format.omf.omf;

import java.util.Set;

import ghidra.program.model.address.Address;

@SuppressWarnings("unused")
public class OmfSymbol {
	private String symbolName;
	private int typeIndex;
	private int dataType;		// 0=unused
	private int byteLength;		// 0=unused
	private int segmentRef = 0;	// Symbol is really reference to extra segment
	private long offset;
	private Address address;

	/**
	 * These names were taken from the OpenWatcom source code
	 * https://github.com/open-watcom/open-watcom-v2/blob/master/bld/watcom/h/fppatche.h
	 */
	private static final Set<String> FLOATINGPOINT_SPECIALNAMES = Set.of(
		"FIWRQQ", "FIDRQQ", "FIERQQ", "FICRQQ", "FJCRQQ", "FISRQQ",
		"FJSRQQ", "FIARQQ", "FJARQQ", "FIFRQQ", "FJFRQQ", "FIGRQQ",
		"FJGRQQ");

	public OmfSymbol(String name, int type, long off, int dT, int bL) {
		symbolName = name;
		typeIndex = type;
		offset = off;
		dataType = dT;
		byteLength = bL;
	}

	public String getName() {
		return symbolName;
	}

	public int getDataType() {
		return dataType;
	}

	public long getOffset() {
		return offset;
	}

	public int getSegmentRef() {
		return segmentRef;
	}

	public void setSegmentRef(int val) {
		segmentRef = val;
	}

	public void setAddress(Address addr) {
		address = addr;
	}

	public Address getAddress() {
		return address;
	}

	public int getFrameDatum() {
		return 0;					// This is currently unused
	}
	
	public boolean isFloatingPointSpecial() {
		return FLOATINGPOINT_SPECIALNAMES.contains(symbolName);
	}
}
