/*
 * Copyright Addicticks 2015.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.addicticks.maven.httpsupload.mojo;

import com.addicticks.net.httpsupload.UploadProgress;
import com.addicticks.net.httpsupload.HttpsFileUploader;
import com.addicticks.net.httpsupload.HttpsFileUploaderConfig;
import com.addicticks.net.httpsupload.HttpsFileUploaderResult;
import com.addicticks.net.httpsupload.UploadItem;
import com.addicticks.net.httpsupload.Utils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

/**
 *
 * @author Addicticks
 */
public abstract class UploadAbstractMojo extends AbstractMojo implements UploadProgress {
    
    private static final long SIZE_1MB = (1024*1024);

    /**
     * The URL to upload the file(s) to. Must be either HTTP or HTTPS protocol.
     *
     * <p>
     * The plugin derives which protocol to use from this value.
      * 
     * <p>
     * Examples: 
     * <pre>
     *   https://solar.mycompany.com/upload
     *   http://pluto.vanilla.org/acceptfiles
     * </pre>
     *
     */
    @Parameter(required = true)
    protected URL targetURL;


    /**
     * Extra fields to send along with the file upload. The keys of
     * the map are the field names and the value their (String) content.
     *
     * <p>
     * Example
     * <pre>
     *    &lt;extraFields&gt;
     *        &lt;field1&gt;value1&lt;/field1&gt;
     *        &lt;field2&gt;value2&lt;/field2&gt;
     *        ...
     *    &lt;/extraFields&gt;
     * </pre>
     *
     * <p>
     * Typical use cases: Extra fields are often used to provide information
     * about the uploader such as a name or an e-mail address. Or they can
     * convey additional information about where the server should put the
     * files, such as a destination folder or similar.
     *
     * <p>
     * NB: Don't set the {@code filename} field here. Posting content for
     * {@code filename} is handled automatically.
     */
    @Parameter(required = false)
    protected Map<String, String> extraFields;

    /**
     * Specifies if the certificate at the endpoint should be validated or not.
     * Only relevant for HTTPS protocol, otherwise ignored.
     *
     * <p>
     * If the value is {@code false} then the server's certificate is not
     * validated in any way. This may be useful when working with endpoints that
     * use a self-signed certificate.
     */
    @Parameter(required = false, defaultValue = "true")
    protected boolean validateCertificate;

    
    /**
     * Connect timeout (in milliseconds) to use when connecting to the endpoint.
     * 
     * <p>If the URL is specified with a hostname as opposed to an IP address then 
     * a name lookup needs to be performed. This is <i>not</i> included in the 
     * connect timeout. The connect timeout is the time it takes to establish
     * the tcp connection, nothing else. The timeout for name resolution (DNS lookup) is
     * platform dependent.
     */
    @Parameter(required = false, defaultValue="10000")
    protected int connectTimeoutMs;
    
    /**
     * Read timeout (in milliseconds) to use when reading from the endpoint. This
     * is the time from the upload has completed until the server responds.
     * 
     * <p>You may have to increase this parameter if the endpoint takes a long
     * time to process the uploaded data.
     */
    @Parameter(required = false, defaultValue="10000")
    protected int readTimeoutMs;
    
    /**
     * Sets additional overall HTTP headers to add to the upload POST request.
     * There's rarely a need to use this parameter.
     *
     * <p>
     * The following header fields are automatically set:
     * <pre>
     *    "Connection"
     *    "Cache-Control"
     *    "Content-Type"
     *    "Content-Length"
     *    "Authorization"
     * </pre> and you must <i>never</i> set these here. (if you do they will be
     * ignored).
     *
     * <p>
     * However you might want to use this parameter to explicitly set e.g.
     * {@code User-Agent} or non-standard header fields that are required for your
     * particular endpoint. For example by overriding {@code User-Agent} you can
     * make the upload operation look to the endpoint as if it comes from a
     * browser.
     * 
     * <p>Example:
     * <pre>
     *     &lt;additionalHeaders&gt;
     *         &lt;!-- impersonate Safari browser on iPad  --&gt;
     *         &lt;User-Agent&gt;Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405&lt;/User-Agent&gt;
     *         &lt;!--  just an example of a really weird header field  --&gt;
     *         &lt;From&gt;joe@mycorp.com&lt;/From&gt;
     *     &lt;/additionalHeaders&gt;
     * </pre>
     * 
     *
     */
    @Parameter(required = false)
    protected Map<String,String> additionalHeaders;

    /**
     * Can be used to turn off upload progress logging (if parameter value is {@code false}).
     * By default upload progress logging is enabled. Progress logging can be
     * a bit 'chatty'.
     * 
     * <p>Files larger than 100 MBytes will see a log entry for every one percent 
     * uploaded. For files less this size there will be proportionally less log
     * entries.
     * 
     * <p>This parameter will only turn off the actual progress logging not the logging
     * at the beginning and end of the file upload operation.
     */
    @Parameter(required=false, defaultValue="true")
    protected boolean indicateProgress;
    

    /**
     * 
     */
    @Parameter(required=false, defaultValue="false")
    protected boolean useAuthentication;
    
    
    @Parameter(defaultValue = "${settings}", readonly = true)
    protected Settings settings;

    @Override
    public void uploadProgress(File file, long totalSize, int pct) {
        if (indicateProgress) {
            boolean doPrint = false;
            if (totalSize > (100 * SIZE_1MB)) {
                doPrint = true;
            } else if (totalSize > (50 * SIZE_1MB)) {
                if ( (totalSize % 10) == 0) {
                    doPrint = true;
                }
            } else if (totalSize > (10 * SIZE_1MB)) {
                if ( (totalSize % 25) == 0) {
                    doPrint = true;
                }
            } else {
                if ( pct == 50) {
                    doPrint = true;
                }
            }
            if (doPrint || pct == 0 || pct == 100) {
                getLog().info("Uploading " + file + ":  " + pct + " pct completed.");
            }
        }
    }

    @Override
    public void uploadStart(int noOfFiles, long totalSizeAll) {
        getLog().info("Uploading " + noOfFiles + " files. Total " + Utils.fileSizeAsStr(totalSizeAll));
    }

    @Override
    public void uploadEnd(long totalSizeAll, long msecondsUsed) {
        getLog().info("Upload completed. " + Utils.fileSizeAsStr(totalSizeAll) + " sent in " + ((double) (msecondsUsed / 1000)) + " seconds.");
    }

    protected void setEndpointCredentials(HttpsFileUploaderConfig config, URL targetURL) throws MojoExecutionException {
        String targetHost = targetURL.getHost().toLowerCase();
        Server serverSettings = settings.getServer(targetHost);
        if (serverSettings != null) {
            if (serverSettings.getUsername() != null && serverSettings.getPassword() != null) {
                getLog().debug("Found Server settings in settings.xml for " + targetHost + " :   username : " + serverSettings.getUsername() + ", password : *********");
                config.setEndpointUsername(serverSettings.getUsername());
                config.setEndpointPassword(serverSettings.getPassword());
            } else {
                throw new MojoExecutionException("Found Server settings in settings.xml for " + targetHost + " but no username and password information was found");
            }
        } else {
            throw new MojoExecutionException("No Server settings found in settings.xml for " + targetHost);
        }
    }
    
    protected HttpsFileUploaderConfig getConfig() throws MojoExecutionException  {
        HttpsFileUploaderConfig config = new HttpsFileUploaderConfig(targetURL);

        config.setValidateCertificates(validateCertificate);

        if (useAuthentication) {
            setEndpointCredentials(config, targetURL);
        }

        return config;
    }

    protected void upload(
            HttpsFileUploaderConfig config,
            List<UploadItem> filesToUpload,
            Map<String, String> otherFields) throws MojoExecutionException {

        getLog().info("Uploading to " + targetURL);
        try {
            HttpsFileUploaderResult result = HttpsFileUploader.upload(config, filesToUpload, extraFields, this);
            if (result.isError()) {
                getLog().error("Uploading was unsuccesful !");
                String responseText = result.getResponseTextNoHtml();
                throw new MojoExecutionException("Could not upload to " + targetURL + ". Error from server was : " 
                        + System.lineSeparator() + "Status code: " + Utils.getHttpStatusCodeText(result.getHttpStatusCode())
                        + System.lineSeparator() + ((responseText==null)? "<server did not return a text response>" : responseText));
            }
        } catch (IOException ex) {
            throw new MojoExecutionException("Error uploading file to " + targetURL.toString(), ex);
        }
    }

}
