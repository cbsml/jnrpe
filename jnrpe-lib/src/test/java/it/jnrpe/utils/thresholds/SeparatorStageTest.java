/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
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
 *******************************************************************************/
package it.jnrpe.utils.thresholds;

import org.junit.Assert;
import org.junit.Test;

/**
 */
public class SeparatorStageTest {

    /**
     * Method testParseOk.
     */
    @Test
    public void testParseOk() {
        SeparatorStage stage = new SeparatorStage();

        Assert.assertEquals("10", stage.parse("..10", new RangeConfig()));
    }

    /**
     * Method testParseKo.
     */
    @Test
    public void testParseKo() {
        SeparatorStage stage = new SeparatorStage();
        Assert.assertFalse(stage.canParse("a"));
    }

}
