package com.devexperts.dxlab.lincheck.strategy.randomswitch;

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

import com.devexperts.dxlab.lincheck.Options;

/**
 * Options for {@link RandomSwitchStrategy random-switch} strategy.
 */
public class RandomSwitchOptions extends Options<RandomSwitchOptions, RandomSwitchCTestConfiguration> {
    protected int invocationsPerIteration = RandomSwitchCTestConfiguration.DEFAULT_INVOCATIONS;

    /**
     * Run each test scenario {@code invocations} times.
     */
    public RandomSwitchOptions invocationsPerIteration(int invocations) {
        this.invocationsPerIteration = invocations;
        return this;
    }

    @Override
    public RandomSwitchCTestConfiguration createTestConfigurations() {
        return new RandomSwitchCTestConfiguration(iterations, threads, actorsPerThread, actorsBefore, actorsAfter,
            executionGenerator, verifier, invocationsPerIteration);
    }
}
