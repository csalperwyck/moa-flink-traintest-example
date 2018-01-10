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

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import com.yahoo.labs.samoa.instances.Instance;

import moa.core.Example;
import moa.streams.generators.RandomRBFGenerator;

public class RRBFSource extends RichParallelSourceFunction<Example<Instance>> {
	
		private static final long serialVersionUID = 1L;
		private boolean isRunning = false;
		private RandomRBFGenerator rrbf = new RandomRBFGenerator();

		@Override
		public void open(Configuration parameters) throws Exception {
			rrbf.prepareForUse();
		}
		
		@Override
		public void cancel() {
			isRunning = false;
		}

		@Override
		public void run(SourceContext<Example<Instance>> sc) throws Exception {
			isRunning = true;
			while (isRunning) {
				sc.collect(rrbf.nextInstance());
			}
		}
}
