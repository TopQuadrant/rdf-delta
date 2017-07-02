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

import java.io.IOException ;
import java.io.InputStream ;

import org.apache.jena.atlas.web.AcceptList ;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.fuseki.servlets.ActionREST ;
import org.apache.jena.fuseki.servlets.ActionSPARQL ;
import org.apache.jena.fuseki.servlets.HttpAction ;
import org.apache.jena.fuseki.servlets.ServletOps ;
import org.apache.jena.riot.web.HttpNames ;
import org.apache.jena.sparql.core.DatasetGraph ;
import org.seaborne.patch.RDFPatch ;
import org.seaborne.patch.RDFPatchOps ;

public class FusekiPatch extends ActionSPARQL {
    
    // ActionREST
//    @Override
//    protected void doOptions(HttpAction action) {
//        setCommonHeadersForOptions(action.response) ;
//        action.response.setHeader(HttpNames.hAllow, "OPTIONS,POST,PATCH");
//        action.response.setHeader(HttpNames.hContentLengh, "0") ;
//        ServletOps.success(action) ;
//    }
//
//    @Override
//    protected void doHead(HttpAction action) {
//        ServletOps.errorMethodNotAllowed("HEAD") ;
//    }
//
//    @Override
//    protected void doPost(HttpAction action) {
//        perform(action);
//    }
//
//    @Override
//    protected void doPut(HttpAction action) {
//        ServletOps.errorMethodNotAllowed("PUT") ;
//    }
//
//    @Override
//    protected void doDelete(HttpAction action) {
//        ServletOps.errorMethodNotAllowed("DELETE") ;
//    }
//
//    @Override
//    protected void doPatch(HttpAction action) {
//        perform(action);
//    }
//
//    @Override
//    protected void doGet(HttpAction action) {
//        ServletOps.errorMethodNotAllowed("GET") ;
//    }

    @Override
    protected void validate(HttpAction action) {
        String method = action.getRequest().getMethod() ;
        switch(method) {
            case HttpNames.METHOD_POST:
            case HttpNames.METHOD_PATCH:
                break ;
            default:
                ServletOps.errorMethodNotAllowed(method+" : Patch must use POST or PATCH");
        }
    }
    
    public static final String contentTypePatchText1    =  "application/rdf-patch";
    public static final String contentTypePatchText2    =  "text/rdf-patch";
    public static final String contentTypePatchBinary   =  "application/rdf-patch+thrift";

    // Preferred form.
    public static final ContentType ctPatchText         =  ContentType.create(contentTypePatchText1);
    public static final ContentType ctPatchBinary       =  ContentType.create(contentTypePatchBinary);

    
    public static final AcceptList rsOfferPatch        = AcceptList.create(contentTypePatchText1,
                                                                           contentTypePatchText2,
                                                                           contentTypePatchBinary);
    
    @Override
    protected void perform(HttpAction action) {
        try {

            String ctStr = action.request.getContentType() ;
            // Must be UTF-8 or unset. But this is wrong so often,
            // it is less trouble to just force UTF-8.
            String charset = action.request.getCharacterEncoding() ;

            ContentType mt = ( ctStr != null ) 
                // Parse it.
                ? ContentType.create(ctStr, charset)
                // No header Content-type - assume patch-text.
                : ctPatchText;

            DatasetGraph dsg = action.getActiveDSG(); 
            action.beginWrite();
            try {
                InputStream input = action.request.getInputStream();
                RDFPatchOps.applyChange(dsg, input);
                action.commit();
            //} catch (Throwable th) {}
            } finally { action.endWrite(); }
        } catch (IOException ex) {
            ServletOps.errorOccurred("IOException: "+ex.getMessage());
        }
    }

}