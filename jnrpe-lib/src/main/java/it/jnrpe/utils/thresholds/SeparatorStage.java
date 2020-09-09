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

/**
 * This class is involved when we reached the separator between the left and the
 * right boundary ('..').
 *
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
class SeparatorStage extends Stage {

    /**
     * 
     */
    private static final long serialVersionUID = -7798534695986312023L;

    /**
     *
     */
    protected SeparatorStage() {
        super("separator");
    }

    /**
     * Method parse.
     * @param threshold String
     * @param tc RangeConfig
     * @return String
     */
    @Override
    public String parse(final String threshold, final RangeConfig tc) {
        return threshold.substring(2);
    }

    /**
     * Method canParse.
     * @param threshold String
     * @return boolean
     */
    @Override
    public boolean canParse(final String threshold) {
        return threshold.startsWith("..");
    }

    /**
     * Method expects.
     * @return String
     */
    @Override
    public String expects() {
        return "..";
    }
}