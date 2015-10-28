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

import com.addicticks.net.httpsupload.HttpsFileUploaderConfig;
import com.addicticks.net.httpsupload.UploadItem;
import com.addicticks.net.httpsupload.UploadItemFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Uploads multiple file to a remote server using HTTP or HTTPS. This
 * goal should only be used with servers that accepts <i>multiple</i> file
 * to be uploaded in a single operation.
 *
 * <p>Files are sent using POST method and using
 * {@code multipart/form-data} encoding (RFC2388). 
 * 
 * @author Addicticks
 */
@Mojo(  name = "multiple",
        requiresProject = true,
        defaultPhase = LifecyclePhase.DEPLOY)
public class UploadMultipleMojo extends UploadAbstractMojo   {

    
    
    
    /**
     * Files to upload.
     * 
     * <p>
     * Example:
     * <pre>
     *     &lt;uploadFiles&gt;
     *         &lt;uploadFile&gt;
     *             &lt;formFieldName&gt;file1&lt;/formFieldName&gt;
     *             &lt;file&gt;${project.build.directory}/myfile.jar&lt;/file&gt;
     *         &lt;/uploadFile&gt;
     *         &lt;uploadFile&gt;
     *             &lt;formFieldName&gt;file2&lt;/formFieldName&gt;
     *             &lt;file&gt;c://mydir/whatsup.dat&lt;/file&gt;
     *             &lt;hintFilename&gt;whatsup.xls&lt;/hintFilename&gt;
     *         &lt;/uploadFile&gt;
     *     &lt;/uploadFiles&gt;
     * </pre>
     * 
     * <p>
     * There must be at least one <code>uploadFile</code> element.
     * 
     * <p>
     * The elements of an <code>uploadFile</code> are as follows:
     * <ul>
     *    <li><code>formFieldName</code>. The form field name to POST the file to. This value depends entirely on
     *          the server you're uploading to. Typically such a field is named "file" 
     *          or if multiple files are allowed: "file1", "file2", etc.
     *          The value must be unique among the files to be uploaded.
     *          <br>Type: <code>String</code>, Required: <code>Yes</code>
     *    </li><br>
     *    <li><code>file</code>. File to upload. 
     *          <br>Type: <code>java.io.File</code>, Required: <code>Yes</code>
     *    </li><br>
     *    <li><code>mimeType</code>. This is the http <code>Content-Type</code> 
     *          used when the file is uploaded. If not given explicitly here it 
     *          will be derived from the file name extension using Java's 
     *          {@link java.net.URLConnection#guessContentTypeFromName(java.lang.String)}
     *          method.
     *          <br>Type: <code>String</code>, Required: <code>No</code>
     *    </li><br>
     *    <li><code>hintFilename</code>. Hint given to the server as to what the 
     *          server should name the file. If this value is not set explicitly 
     *          it will be derived from the filename of the {@code file} element.
     *          <br>Type: <code>String</code>, Required: <code>No</code>
     *    </li>
     * </ul>
     * 
     * 
     * <br>
     * <br>
     **/ 
    @Parameter(required=true)
    private List<UploadFile> uploadFiles;
    
    

    
    
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        validateUploadFiles();
        
        HttpsFileUploaderConfig config = getConfig();

      
        Map<String, UploadItem> newMap = new HashMap<>();
        if (uploadFiles != null) {
            for (UploadFile e : uploadFiles) {
                
                UploadItem f;
                boolean hintFileNameSpecified = true;
                boolean mimeTypeSpecified = true;
                if (e.getHintFilename() == null) {
                   hintFileNameSpecified = false;
                }
                if (e.getMimeType() == null) {
                    mimeTypeSpecified = false;
                }
                
                if (mimeTypeSpecified && hintFileNameSpecified) {
                    f = new UploadItemFile(
                            e.getFile(),
                            e.getHintFilename(),
                            e.getMimeType());
                } else if (!mimeTypeSpecified && hintFileNameSpecified) {
                    f = new UploadItemFile(
                            e.getFile(), 
                            e.getHintFilename());
                } else {
                    f = new UploadItemFile(
                            e.getFile());
                }
                if (newMap.put(e.getFormFieldName(), f) != null) {
                    throw new MojoExecutionException("The value for formFieldName must be unique among uploadFiles. (value = \"" + e.getFormFieldName() + "\")");
                }
            }
        }
        
        upload(config, newMap, extraFields);
    }

    
    private void validateUploadFiles() throws MojoExecutionException {
        for (UploadFile e : uploadFiles) {
            if (e.getFormFieldName() == null || e.getFormFieldName().isEmpty()) {
                throw new MojoExecutionException("'formFieldName' is required on 'uploadFile'");
            }
            if (e.getFile() == null) {
                throw new MojoExecutionException("'file' is required on 'uploadFile'");
            }
        }
        
    }
    
    
}

