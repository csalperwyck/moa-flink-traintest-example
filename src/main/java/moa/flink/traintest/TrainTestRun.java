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

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamContextEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.yahoo.labs.samoa.instances.Instance;

import moa.classifiers.Classifier;
import moa.classifiers.trees.HoeffdingTree;
import moa.core.Example;

public class TrainTestRun {
	
	public static void main(String[] args) throws Exception {
		
		// just one classifier here, so parallelism is 1
		StreamExecutionEnvironment env = StreamContextEnvironment.createLocalEnvironment(1);
		
		// create the generator
		DataStreamSource<Example<Instance>> rrbfSource = env.addSource(new RRBFSource());
		
		// split in train and test
		SplitStream<Example<Instance>> trainAndTestStream = rrbfSource.split(new RandomSamplingSelector(0.02));
		
		DataStream<Example<Instance>> testStream = trainAndTestStream.select(RandomSamplingSelector.TEST);
		DataStream<Example<Instance>> trainStream = trainAndTestStream.select(RandomSamplingSelector.TRAIN);
		
		// create one classifier
		SingleOutputStreamOperator<Classifier> classifier = trainStream.process(new LearningProcessFunction(HoeffdingTree.class, 1000));
		
		// predict on the test stream, update the classifier when there is a new version
		testStream.connect(classifier)
			.flatMap(new ClassifyAndUpdateClassifierFunction())
			.countWindowAll(1_000).aggregate(new PerformanceFunction()) // aggregate performance on a window
			.print(); // show results
		
		// fire execution
		env.execute("MOA Flink - MeetUp Krakow - January 16th 2018");
	}

}
