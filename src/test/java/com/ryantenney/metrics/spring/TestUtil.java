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
package com.ryantenney.metrics.spring;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.ryantenney.metrics.annotation.InjectMetric;

class TestUtil {

	private static final Logger log = LoggerFactory.getLogger(TestUtil.class);

	static String forTimedMethod(Class<?> klass, Member member, Timed annotation) {
		return Util.forTimedMethod(klass, member, annotation);
	}

	static String forMeteredMethod(Class<?> klass, Member member, Metered annotation) {
		return Util.forMeteredMethod(klass, member, annotation);
	}

	static String forGauge(Class<?> klass, Member member, com.codahale.metrics.annotation.Gauge annotation) {
		return Util.forGauge(klass, member, annotation);
	}

	static String forExceptionMeteredMethod(Class<?> klass, Member member, ExceptionMetered annotation) {
		return Util.forExceptionMeteredMethod(klass, member, annotation);
	}

	static String forInjectMetricField(Class<?> klass, Member member, InjectMetric annotation) {
		return Util.forInjectMetricField(klass, member, annotation);
	}

	static Gauge<?> forGaugeField(MetricRegistry metricsRegistry, Class<?> clazz, String fieldName) {
		Field field = findField(clazz, fieldName);
		String metricName = forGauge(clazz, field, field.getAnnotation(com.codahale.metrics.annotation.Gauge.class));
		log.info("Looking up gauge field named '{}'", metricName);
		return metricsRegistry.getGauges().get(metricName);
	}

	static Gauge<?> forGaugeMethod(MetricRegistry metricsRegistry, Class<?> clazz, String methodName) {
		Method method = findMethod(clazz, methodName);
		String metricName = forGauge(clazz, method, method.getAnnotation(com.codahale.metrics.annotation.Gauge.class));
		log.info("Looking up gauge method named '{}'", metricName);
		return metricsRegistry.getGauges().get(metricName);
	}

	static Timer forTimedMethod(MetricRegistry metricsRegistry, Class<?> clazz, String methodName) {
		Method method = findMethod(clazz, methodName);
		String metricName = forTimedMethod(clazz, method, method.getAnnotation(Timed.class));
		log.info("Looking up timed method named '{}'", metricName);
		return metricsRegistry.getTimers().get(metricName);
	}

	static Meter forMeteredMethod(MetricRegistry metricsRegistry, Class<?> clazz, String methodName) {
		Method method = findMethod(clazz, methodName);
		String metricName = forMeteredMethod(clazz, method, method.getAnnotation(Metered.class));
		log.info("Looking up metered method named '{}'", metricName);
		return metricsRegistry.getMeters().get(metricName);
	}

	static Meter forExceptionMeteredMethod(MetricRegistry metricsRegistry, Class<?> clazz, String methodName) {
		Method method = findMethod(clazz, methodName);
		String metricName = forExceptionMeteredMethod(clazz, method, method.getAnnotation(ExceptionMetered.class));
		log.info("Looking up exception metered method named '{}'", metricName);
		return metricsRegistry.getMeters().get(metricName);
	}

	static Metric forInjectMetricField(MetricRegistry metricsRegistry, Class<?> clazz, String fieldName) {
		Field field = findField(clazz, fieldName);
		String metricName = forInjectMetricField(clazz, field, field.getAnnotation(InjectMetric.class));
		log.info("Looking up injected metric field named '{}'", metricName);
		Class<?> type = field.getType();
		if (type.isAssignableFrom(Meter.class)) {
			return metricsRegistry.getMeters().get(metricName);
		}
		else if (type.isAssignableFrom(Timer.class)) {
			return metricsRegistry.getTimers().get(metricName);
		}
		else if (type.isAssignableFrom(Counter.class)) {
			return metricsRegistry.getCounters().get(metricName);
		}
		else if (type.isAssignableFrom(Histogram.class)) {
			return metricsRegistry.getHistograms().get(metricName);
		}
		return null;
	}

	private static Field findField(Class<?> clazz, String fieldName) {
		List<Field> fieldsFound = new ArrayList<Field>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getName().equals(fieldName)) {
				fieldsFound.add(field);
			}
		}
		if (fieldsFound.size() == 1) {
			return fieldsFound.get(0);
		}
		else {
			throw new RuntimeException("No unique field " + fieldName + " found on class " + clazz.getName());
		}
	}

	private static Method findMethod(Class<?> clazz, String methodName) {
		List<Method> methodsFound = new ArrayList<Method>();
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				methodsFound.add(method);
			}
		}
		if (methodsFound.size() == 1) {
			return methodsFound.get(0);
		}
		else {
			throw new RuntimeException("No unique method " + methodName + " found on class " + clazz.getName());
		}
	}

}
