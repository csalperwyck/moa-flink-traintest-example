/*
 * Copyright 2025 - Christophe Salperwyck
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.  
 */
package moa.flink.traintest;

import org.apache.flink.api.common.accumulators.AverageAccumulator;
import org.apache.flink.api.common.functions.AggregateFunction;

public class PerformanceFunction implements AggregateFunction<Boolean, AverageAccumulator, Double> {
	private static final long serialVersionUID = 1L;

	@Override
	public AverageAccumulator createAccumulator() {
		return new AverageAccumulator();
	}

	@Override
	public AverageAccumulator add(Boolean value, AverageAccumulator accumulator) {
		accumulator.add(value ? 1 : 0);
		return accumulator;
	}

	@Override
	public Double getResult(AverageAccumulator accumulator) {
		return accumulator.getLocalValue();
	}

	@Override
	public AverageAccumulator merge(AverageAccumulator a, AverageAccumulator b) {
		a.merge(b);
		return a;
	}
}