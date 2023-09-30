/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
 */

package org.seaborne.delta.lib;

import org.apache.jena.atlas.lib.Version;

public class SystemInfo {
    /**
     * A relative resources path to the location of
     * <code>delta-properties.xml</code> file.
     */

    /** Property path name base */
    private static final String PATH = "org.seaborne.delta";

    public static String systemName()   { return "RDF Delta"; }
    public static String version()      { return Version.versionForClass(SystemInfo.class).orElse("development"); }
    @Deprecated
    public static String buildDate()    { return ""; }
}
