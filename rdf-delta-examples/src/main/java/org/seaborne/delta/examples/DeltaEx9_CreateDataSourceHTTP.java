/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.seaborne.delta.examples;

import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.sse.SSE;
import org.apache.jena.system.Txn;
import org.apache.jena.tdb.base.file.Location;
import org.seaborne.delta.Id;
import org.seaborne.delta.client.* ;
import org.seaborne.delta.link.DeltaLink;
import org.seaborne.delta.server.http.PatchLogServer;
import org.seaborne.delta.server.local.DeltaLinkLocal;
import org.seaborne.delta.server.local.LocalServer;

/** Connect to an HTTP server, create a new DataSource, remove it. */ 
public class DeltaEx9_CreateDataSourceHTTP {
    
    public static Quad quad = SSE.parseQuad("(:g :s :p :o)");
    
    public static void main(String... args) {
        try { 
            main2(args);
        } finally {
            // Explicitly exit - the server is still running.
            System.exit(0); }
    }
    
    public static void main2(String... args) {
        // The local state of the server.
        Location loc = Location.create("DeltaServer");
        LocalServer localServer = LocalServer.attach(loc);
        DeltaLink serverState = DeltaLinkLocal.connect(localServer);
        PatchLogServer server = PatchLogServer.create(1066, serverState);
        // --------
        // Connect to a server
        DeltaLink dLink = DeltaLinkHTTP.connect("http://localhost:1066/");
        // One one zone supported currently.
        Zone zone = Zone.connect("Zone");
        Id clientId = Id.create();
        
        // Create a new patch log.
        dLink.newDataSource("TEST", "http://example/test");
        
        // Put it under client management. 
        DeltaClient dClient = DeltaClient.create(zone, dLink);
        Id dsRef = dClient.register("TEST", LocalStorageType.MEM, SyncPolicy.TXN_RW);
        
        // and now connect to it
        try ( DeltaConnection dConn = dClient.get(dsRef) ) {
            long version1 = dConn.getRemoteVersionLatest();
            System.out.println("Version = "+version1);

            // Change the dataset
            DatasetGraph dsg = dConn.getDatasetGraph();
            Txn.executeWrite(dsg, ()->{
                dsg.add(quad);
            });
            
            long version2 = dConn.getRemoteVersionLatest();
            System.out.println("Version = "+version2);
        }

        System.out.println("DONE");
    }
    
}
