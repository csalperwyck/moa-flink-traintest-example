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

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import com.yahoo.labs.samoa.instances.Instance;

import moa.classifiers.Classifier;
import moa.core.Example;

public class LearningProcessFunction extends ProcessFunction<Example<Instance>, Classifier> {

	private static final long serialVersionUID = 1L;
	private final Class<? extends Classifier> clazz;
	private Classifier classifier;
	private final int updateSize;
	private long nbExampleSeen;

	public LearningProcessFunction(Class<? extends Classifier> clazz, int updateSize) {
		this.clazz = clazz;
		this.updateSize = updateSize;
	}
	
	@Override
	public void open(Configuration parameters) throws InstantiationException, IllegalAccessException {
		classifier = clazz.newInstance();
		classifier.prepareForUse();
	}

	@Override
	public void processElement(Example<Instance> record, ProcessFunction<Example<Instance>, Classifier>.Context arg1,
			Collector<Classifier> collector) {

		nbExampleSeen++;
		classifier.trainOnInstance(record);
		if (nbExampleSeen % updateSize == 0) {
			collector.collect(classifier);
		}
		if (nbExampleSeen % 500_000 == 0) {
			System.err.println("Already " + nbExampleSeen + " examples seen!");
		}

	}

}
