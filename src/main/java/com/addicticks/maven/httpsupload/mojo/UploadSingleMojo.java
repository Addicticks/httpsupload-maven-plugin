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
import java.io.File;
import java.util.Collections;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Uploads a single file to a remote server using HTTP or HTTPS.
 * 
 * <p>File is sent using POST method and using
 * {@code multipart/form-data} encoding (RFC2388). 
 * 
 * @author Addicticks
 */
@Mojo(  name = "single", requiresProject = false)
public class UploadSingleMojo extends UploadAbstractMojo  {
    
    /**
     * File to upload.
     **/ 
    @Parameter(required=true)
    private File file;
    
    
    /**
     * The form field name to POST the file to. This value depends entirely on
     * the server. Typically such a field is named "file".
     */
    @Parameter(required = false, defaultValue = "file")
    protected String formFieldName;
    
    /**
     * Mime type.
     * 
     * <p>This is the http <code>Content-Type</code> used when the file
     * is uploaded. If not given explicitly here it will be derived
     * from the file name extension using Java's 
     * {@link java.net.URLConnection#guessContentTypeFromName(java.lang.String)}
     * method.
     * 
     */
    @Parameter(required=false)
    private String mimeType;

    /**
     * Hint given to the server as to what the server should name the file. If 
     * this value is not set explicitly it will be derived from the filename
     * of the {@code file} argument.
     */
    @Parameter(required=false)
    private String hintFilename;
    
    
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        HttpsFileUploaderConfig config = getConfig();
        
        UploadItem uploadItem;
        if (hintFilename != null) {
            uploadItem = new UploadItemFile(file, hintFilename, this.mimeType);
        } else {
            uploadItem = new UploadItemFile(file, file.getName(), this.mimeType);
        }
        uploadItem.setFormFieldName(formFieldName);
        
        upload(config, 
               Collections.singletonList(uploadItem),
               extraFields);
    }

    
    
    
}

