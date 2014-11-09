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
package it.jnrpe;

import java.nio.charset.Charset;

/**
 * The JNRPE execution context contains all the context information useful during 
 * a plugin execution.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public interface IJNRPEExecutionContext {

    /**
     * Returns all the listeners.
     * 
    
     * @return the event bus */
    IJNRPEEventBus getEventBus();

    /**
     * Returns the charset.
     * 
    
     * @return the configured charset */
    Charset getCharset();
}