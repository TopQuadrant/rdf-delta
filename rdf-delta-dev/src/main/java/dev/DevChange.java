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

package dev;


public class DevChange {
    // Next:
    //   Single call "my version is ... get patches I have missed. 

    // Fuseki./assembler ; need service modifier for catch start/finish?
    //   or poll for changes?
    // loop: DeltaClient.sync
    
    //   Client-side:
    //      DeltaClient.execWrite(...)
    //      DatasetRegistry (DeltaClient enough?)
    //      Receiver
    
    //   Server-side:
    //      Configuration
    //      Restart (but its passive so?) 
    
    // Transactional number.
    
    // Reader and Writer
    // Counters
    // Replace tio ... or tio without prefixes etc.
    
    // Server:
    //   receive, check checksum
    //   validate
    //   process
    //     - archive : formally happens
    //     - log
    //     - queue for collection
    //     - send/Patch
    //     - send/GSP
}
