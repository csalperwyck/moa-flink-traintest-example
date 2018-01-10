/*
 * Copyright 2018 - Christophe Salperwyck
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

import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.util.Collector;

import com.yahoo.labs.samoa.instances.Instance;

import moa.classifiers.Classifier;
import moa.classifiers.functions.NoChange;
import moa.core.Example;

public class ClassifyAndUpdateClassifierFunction implements CoFlatMapFunction<Example<Instance>, Classifier, Boolean> {
	
	private static final long serialVersionUID = 1L;
	private Classifier classifier = new NoChange(); //default classifier - return 0 if didn't learn

	@Override
	public void flatMap1(Example<Instance> value, Collector<Boolean> out) throws Exception {
		out.collect(classifier.correctlyClassifies(value.getData()));
	}

	@Override
	public void flatMap2(Classifier classifier, Collector<Boolean> out) throws Exception {
		//update the classifier when a new version is sent
		this.classifier = classifier;
		
	}
}