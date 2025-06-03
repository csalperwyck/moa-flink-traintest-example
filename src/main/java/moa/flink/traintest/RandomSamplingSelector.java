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

import java.util.Collections;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;

import com.yahoo.labs.samoa.instances.Instance;

import moa.core.Example;

public class RandomSamplingSelector implements OutputSelector<Example<Instance>> {
	
	//names of the streams
	public final static String TEST = "test";
	public final static String TRAIN = "train";
	
	public final static Iterable<String> TEST_LIST = Collections.singletonList(TEST);
	public final static Iterable<String> TRAIN_LIST = Collections.singletonList(TRAIN);
	
	private static final long serialVersionUID = 1L;
	private final double split;
	private final MersenneTwister rand = new MersenneTwister(11);
	
	public RandomSamplingSelector(double split) {
		this.split = split;
	}

	@Override
	public Iterable<String> select(Example<Instance> value) {
		//random sampling
		if (rand.nextFloat() < split) {
			return TEST_LIST;
		}
		return TRAIN_LIST;
	}
}