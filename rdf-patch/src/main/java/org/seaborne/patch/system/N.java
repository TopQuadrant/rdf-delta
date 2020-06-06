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

package org.seaborne.patch.system;

import java.util.UUID;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

/** Some functions to do with {@link Node Nodes}. */
public class N {
    public static final String SchemeUuid = "uuid:";
    public static final String SchemeUrnUuid = "urn:uuid:";

    private static final String SCHEME = SchemeUuid;
    // Version 1 are guessable.
    // Version 4 are not.
    //private static UUIDFactory uuidFactory = new UUID_V4_Gen();

    /** Generate a UUID */
    public static UUID genUUID() { return UUID.randomUUID() ; }

    /**
     * This is <i>not</i> a function!
     * It returns a fresh UUID URI on every call.
     */
    public static Node unique() {
        return unique(SCHEME);
    }

    /**
     * This is <i>not</i> a function!
     * It returns a fresh UUID URI with the given scheme name on every call.
     */
    public static Node unique(String schemeName) {
        return NodeFactory.createURI(schemeName+genUUID().toString());
    }
}
