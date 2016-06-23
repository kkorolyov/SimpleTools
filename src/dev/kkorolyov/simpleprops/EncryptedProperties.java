// Copyright (c) 2016, Kirill Korolyov
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// * Redistributions of source code must retain the above copyright notice, this
//   list of conditions and the following disclaimer.
// 
// * Redistributions in binary form must reproduce the above copyright notice,
//   this list of conditions and the following disclaimer in the documentation
//   and/or other materials provided with the distribution.
// 
// * Neither the name of SimpleProps nor the names of its
//   contributors may be used to endorse or promote products derived from
//   this software without specific prior written permission.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package dev.kkorolyov.simpleprops;

import java.io.File;
import java.io.UncheckedIOException;
import java.util.Arrays;

/**
 * A {@code Properties} which encrypts properties stored in its backing file.
 * Uses a byte array as a symmetric encryption key to encrypt and decrypt each line of the backing properties file.
 */
public class EncryptedProperties extends Properties {
	private byte[] key;
	
	/**
	 * Constructs a new {@code EncryptedProperties} instance for a specified file and encryption key.
	 * @see #EncryptedProperties(File, Properties, boolean, byte[])
	 */
	public EncryptedProperties(File file, byte[] key) {
		this(file, null, key);
	}
	/**
	 * Constructs a new {@code EncryptedProperties} instance for a specified file, default values, and encryption key.
	 * @see #EncryptedProperties(File, Properties, boolean, byte[])
	 */
	public EncryptedProperties(File file, Properties defaults, byte[] key) {
		this(file, defaults, false, key);
	}
	/**
	 * Constructs a new {@code Properties} instance for a specified file, default values, and encryption key.
	 * This method may optionally create the path to the specified file.
	 * @param file backing filesystem file
	 * @param defaults default properties
	 * @param mkdirs if {@code true}, the path to the specified file is created if it does not exist
	 * @param key symmetric encryption key to use for decrypting read file contents and encrypting written file contents.
	 * @throws UncheckedIOException if an I/O error occurs
	 */
	public EncryptedProperties(File file, Properties defaults, boolean mkdirs, byte[] key) {
		super(file, defaults, mkdirs);
		setKey(key);
	}
	
	private void setKey(byte[] newKey) {
		key = Arrays.copyOf(newKey, newKey.length);
	}
	
	@Override
	String format(String line) {
		return applyKey(line);
	}
	
	private String applyKey(String line) {
		byte[] 	lineBytes = line.getBytes(),
						resultBytes = new byte[lineBytes.length];
		int keyCounter = 0;
		
		for (int i = 0; i < resultBytes.length; i++) {
			resultBytes[i] = (byte) (lineBytes[i] ^ key[keyCounter]);
			
			keyCounter = ((keyCounter + 1) < key.length) ? (keyCounter + 1) : 0;	// Increment or wrap
		}
		return new String(resultBytes);
	}
	
	/** @return encrypted {@code toString()} value */
	public String toStringEncrypted() {
		return format(toString());
	}
}
