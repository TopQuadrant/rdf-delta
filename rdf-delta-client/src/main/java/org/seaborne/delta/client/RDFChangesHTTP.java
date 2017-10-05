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

package org.seaborne.delta.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse ;
import org.apache.http.StatusLine ;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.atlas.io.IO;
import org.apache.jena.atlas.io.IndentedWriter ;
import org.apache.jena.atlas.logging.FmtLog;
import org.apache.jena.graph.Node;
import org.seaborne.delta.*;
import org.seaborne.delta.lib.IOX ;
import org.seaborne.patch.RDFPatchConst;
import org.seaborne.patch.changes.RDFChangesCancelOnNoChange;
import org.seaborne.patch.changes.RDFChangesWriter;
import org.slf4j.Logger;

/** Collect the bytes of a change stream, then write to HTTP */ 
public class RDFChangesHTTP extends RDFChangesWriter {
    
    // This should be tied to the DeltaLink and have that control text/binary.
    
    private static final Logger LOG = Delta.DELTA_HTTP_LOG;
    // XXX Caching? Auth?
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final ByteArrayOutputStream bytes ;
    private final Runnable resetAction;
    private final Supplier<String> urlSupplier;
    private final String label ;
    // Used to coordinate with reading patches in.
    private final Object syncObject;
    private StatusLine statusLine       = null;
    private String response             = null;
    private Node patchId                = null;
    private boolean changeOccurred      = false;
    
    /**
     * An empty commit is not a no-op. It moves the head of the log to a new point,
     * stopping changes whose parent is the previous state.
     * <p>
     * As a practical point, in some usages, it can be onerous to track whether a change
     * really has been made, or whether a write transaction was started because a change
     * might occur but nothing did.
     * <p>
     * This code is ideally placed can easily track that information.
     * <p>
     * @see RDFChangesCancelOnNoChange RDFChangesCancelOnNoChange -- An alternative approach.
     */
    public static void setSuppressEmptyCommits(boolean b) {
        SuppressEmptyCommits = b;
    }
    private static boolean SuppressEmptyCommits = false ;
    
    
    public RDFChangesHTTP(String label, Supplier<String> urlSupplier, Runnable resetAction) {
        this(label, null, urlSupplier, resetAction);
    }
    
    public RDFChangesHTTP(String label, Object syncObject, Supplier<String> urlSupplier, Runnable resetAction) {
        this(label, syncObject, urlSupplier, resetAction, new ByteArrayOutputStream(100*1024));
    }

    private RDFChangesHTTP(String label, Object syncObject, Supplier<String> urlSupplier, Runnable resetAction, ByteArrayOutputStream out) {
        super(DeltaOps.tokenWriter(out));
        this.syncObject = (syncObject!=null) ? syncObject : new Object();
        this.resetAction = resetAction;
        this.urlSupplier = urlSupplier;
        this.label = label;
        this.bytes = out;
        reset();
    }
    
    @Override
    public void header(String field, Node value) {
        super.header(field, value);
        if ( field.equals(RDFPatchConst.ID) )
            patchId = value;
    }
    
    @Override
    public void add(Node g, Node s, Node p, Node o) {
        markChanged();
        super.add(g, s, p, o);
    }

    @Override
    public void delete(Node g, Node s, Node p, Node o) {
        markChanged();
        super.delete(g, s, p, o);
    }

    @Override
    public void addPrefix(Node gn, String prefix, String uriStr) {
        markChanged();
        super.addPrefix(gn, prefix, uriStr);
    }

    @Override
    public void deletePrefix(Node gn, String prefix) {
        markChanged();
        super.deletePrefix(gn, prefix);
    }

    @Override
    public void txnBegin() {
//        if ( currentTransactionId == null ) {
//            currentTransactionId = Id.create().asNode();
//            super.header(RDFPatch.ID, currentTransactionId);
//        }
        changeOccurred = false ;
        super.txnBegin();
    }

    @Override
    public void txnCommit() {
        super.txnCommit();
        send();
    }

    @Override
    public void txnAbort() {
        reset();
        // Forget.
    }
    
    private void markChanged() {
        changeOccurred = true;
    }

    private boolean changed() {
        return changeOccurred;
    }

    private void reset() {
        patchId = null ;
        changeOccurred = false ;
        bytes.reset();
    }

    private byte[] collected() {
        return bytes.toByteArray();
    }
    
    public void send() {
        if ( SuppressEmptyCommits && ! changed() )
            return ;
        synchronized(syncObject) {
            send$();
        }
    }
    
    /** Get the protocol response - may be null */
    public String getResponse() {
        return response;
    }
    
//    /** An {@link HttpEntity} that is "output only"; it writes a RDF Patch
//     * to an {@code OutputStream}.  
//     * It does not support {@link HttpEntity#getContent()}
//     */
//    static class HttpEntityRDFChanges extends AbstractHttpEntity {
//        // Open the connection on begin()
//        
//        // Really want to get to the base OutputStream.
//        // Output only?
//
//        RDFChangesWriter w;
//        
//        public HttpEntityRDFChanges(RDFChanges changes) {}
//        
//        @Override
//        public boolean isRepeatable() {
//            return false;
//        }
//
//        @Override
//        public long getContentLength() {
//            return -1;
//        }
//
//        @Override
//        public InputStream getContent() throws IOException, UnsupportedOperationException {
//            throw new UnsupportedOperationException("HttpEntityRDFChanges");
//        }
//
//        @Override
//        public void writeTo(OutputStream outstream) throws IOException {
//
//        }
//
//        @Override
//        public boolean isStreaming() {
//            return false;
//        }
//    }
    
    private void send$() {
        if ( patchId == null )
            throw new DeltaBadPatchException("Patch does not have an ID");
        String idStr = Id.str(patchId);
        byte[] bytes = collected();

        FmtLog.info(LOG, "Send patch %s (%d bytes) -> %s", idStr, bytes.length, label);
        
        if ( false ) {
            if ( LOG.isDebugEnabled() ) {
                // Ouch.
                String s = new String(bytes, StandardCharsets.UTF_8);
                LOG.debug("== Sending ...");
                // Do NOT close!
                IndentedWriter w = IndentedWriter.stdout;
                String x = w.getLinePrefix();
                w.setLinePrefix(">> ");
                w.print(s);
                w.setLinePrefix(x);
                if ( ! s.endsWith("\n") )
                    w.println();
                w.flush();
                LOG.debug("== ==");
            }
        }
        
        int attempts = 0 ;
        for(;;) {
            
            HttpPost postRequest = new HttpPost(urlSupplier.get());
            postRequest.setEntity(new ByteArrayEntity(bytes));

            try(CloseableHttpResponse r = httpClient.execute(postRequest) ) {
                attempts++;
                statusLine = r.getStatusLine();
                response = readResponse(r);
                int sc = r.getStatusLine().getStatusCode();
                if ( sc >= 200 && sc <= 299 )
                    return ;
                if ( sc >= 300 && sc <= 399 ) {
                    FmtLog.info(LOG, "Send patch %s HTTP %d", idStr, sc);
                    throw new DeltaHttpException(sc, "HTTP Redirect");
                }
                if ( sc == 401 && attempts == 1 && resetAction != null ) {
                    resetAction.run();
                    continue;
                }
                if ( sc >= 400 && sc <= 499 )
                    throw new DeltaHttpException(sc, r.getStatusLine().getReasonPhrase());
                if ( sc >= 500 )
                    throw new DeltaHttpException(sc, r.getStatusLine().getReasonPhrase());
                break;
            } catch (IOException e) { throw IOX.exception(e); }
        }
    }
        
    private static String readResponse(HttpResponse resp) {
        HttpEntity e = resp.getEntity();
        if ( e != null ) {
            try ( InputStream ins = e.getContent() ) {
                return IO.readWholeFileAsUTF8(ins);
            } catch (IOException ex) { ex.printStackTrace(); }
        } 
        return null;
    }
}
