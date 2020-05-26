/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.support;

import org.springframework.aop.ClassFilter;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Simple ClassFilter implementation that passes classes (and optionally subclasses).
 *
 * @author Rod Johnson
 * @author Sam Brannen
 */
@SuppressWarnings("serial")
public class RootClassFilter implements ClassFilter, Serializable {

	private final Class<?> clazz;


	public RootClassFilter(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		this.clazz = clazz;
	}


	@Override
	public boolean matches(Class<?> candidate) {
		return this.clazz.isAssignableFrom(candidate);
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof RootClassFilter &&
				this.clazz.equals(((RootClassFilter) other).clazz)));
	}

	@Override
	public int hashCode() {
		return this.clazz.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + this.clazz.getName();
	}

}
