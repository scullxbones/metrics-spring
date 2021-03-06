/*
 * Copyright 2012 Ryan W Tenney (http://ryan.10e.us)
 *            and Martello Technologies (http://martellotech.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ryantenney.metrics.spring.reporter;

import static com.ryantenney.metrics.spring.reporter.JmxReporterFactoryBean.*;

import java.util.concurrent.TimeUnit;

public class JmxReporterElementParser extends AbstractReporterElementParser {

	@Override
	public String getType() {
		return "jmx";
	}

	@Override
	protected Class<?> getBeanClass() {
		return JmxReporterFactoryBean.class;
	}

	@Override
	protected void validate(ValidationContext c) {
		if (c.has(RATE_UNIT)) {
			TimeUnit.valueOf(c.get(RATE_UNIT));
		}
		if (c.has(DURATION_UNIT)) {
			TimeUnit.valueOf(c.get(DURATION_UNIT));
		}
	}

}
