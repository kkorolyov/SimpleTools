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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * {@link Properties} which are encrypted before being written to a backing file.
 */
public class EncryptedProperties extends Properties {
	private byte[] key;
	
	/**
	 * Constructs a new {@code EncryptedProperties} instance for a specified file and encryption key.
	 * @see #EncryptedProperties(Path, Properties, byte[])
	 */
	public EncryptedProperties(Path file, byte[] key) {
		this(file, null, key);
	}
	/**
	 * Constructs a new {@code Properties} instance for a specified file, default values, and encryption key.
	 * @param file backing filesystem file
	 * @param defaults default properties
	 * @param key symmetric encryption key to use for decrypting read file contents and encrypting written file contents.
	 * @throws UncheckedIOException if an I/O error occurs
	 */
	public EncryptedProperties(Path file, Properties defaults, byte[] key) {
		setFile(file);
		setDefaults(defaults);
		setKey(key);
		
		try {
			reload();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	private void setKey(byte[] newKey) {
		key = Arrays.copyOf(newKey, newKey.length);
	}
	
	@Override
	protected Properties buildFileProperties(Path file) {
		return new EncryptedProperties(file, key);
	}
	
	@Override
	protected String format(String string) {
		return applyKey(string);
	}
	
	private String applyKey(String line) {
		byte[] bytes = line.getBytes();
		
		for (int i = 0; i < bytes.length; i++)
			bytes[i] = (byte) (bytes[i] ^ key[i % key.length]);	// Wraps if not enough key
			
		return new String(bytes);
	}
	
	/** @return encrypted {@code toString()} value */
	public String toStringEncrypted() {
		return format(toString());
	}
}
