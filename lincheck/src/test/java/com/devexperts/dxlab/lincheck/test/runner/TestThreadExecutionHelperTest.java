package com.devexperts.dxlab.lincheck.test.runner;

/*
 * #%L
 * Lincheck
 * %%
 * Copyright (C) 2015 - 2018 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.Result;
import com.devexperts.dxlab.lincheck.execution.ExecutionResult;
import com.devexperts.dxlab.lincheck.runner.Runner;
import com.devexperts.dxlab.lincheck.runner.TestThreadExecution;
import com.devexperts.dxlab.lincheck.runner.TestThreadExecutionGenerator;
import com.devexperts.dxlab.lincheck.strategy.Strategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Queue;

public class TestThreadExecutionHelperTest {
    private Runner runner;

    @Before
    public void setUp() {
        Strategy mockStrategy = new Strategy(null, null, null) {
            @Override
            public void run(){
                throw new UnsupportedOperationException();
            }
        };
        runner = new Runner(null, mockStrategy, ArrayDeque.class) {
            @Override
            public ExecutionResult run() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Test
    public void testBase() throws Exception {
        TestThreadExecution ex = TestThreadExecutionGenerator.create(runner, 0,
            Arrays.asList(
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(1), Collections.emptyList()),
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(2), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("element"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("peek"), Collections.emptyList(), Collections.emptyList())
            ), false);
        ex.testInstance = new ArrayDeque<>();
        Assert.assertArrayEquals(new Result[] {
            Result.createValueResult(true),
            Result.createValueResult(true),
            Result.createValueResult(1),
            Result.createValueResult(2),
            Result.createValueResult(2)
        }, ex.call());
    }

    @Test(expected = NoSuchElementException.class)
    public void testGlobalException() throws Exception {
        TestThreadExecution ex = TestThreadExecutionGenerator.create(runner, 0,
            Arrays.asList(
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(1), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(2), Collections.emptyList())
            ), false);
        ex.testInstance = new ArrayDeque<>();
        ex.call();
    }

    @Test
    public void testActorExceptionHandling() throws Exception {
        TestThreadExecution ex = TestThreadExecutionGenerator.create(runner, 0,
            Arrays.asList(
                new Actor(ArrayDeque.class.getMethod("addLast", Object.class), Arrays.asList(1), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Arrays.asList(NoSuchElementException.class)),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Arrays.asList(Exception.class, NoSuchElementException.class))
            ), false);
        ex.testInstance = new ArrayDeque<>();
        Assert.assertArrayEquals(new Result[] {
            Result.createVoidResult(),
            Result.createValueResult(1),
            Result.createExceptionResult(NoSuchElementException.class),
            Result.createExceptionResult(NoSuchElementException.class)
        }, ex.call());
    }

    @Test
    public void testWaits() throws Exception {
        TestThreadExecution ex = TestThreadExecutionGenerator.create(runner, 0,
            Arrays.asList(
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(1), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Arrays.asList(NoSuchElementException.class))
            ), true);
        ex.testInstance = new ArrayDeque<>();
        ex.waits = new int[] {15, 100};
        Assert.assertArrayEquals(new Result[] {
            Result.createValueResult(true),
            Result.createValueResult(1),
            Result.createExceptionResult(NoSuchElementException.class)
        }, ex.call());
    }
}