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

import java.io.File;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Specification of a file to be uploaded.
 * @author Addicticks
 */
public class UploadFile {
    
    /**
     * File to upload.
     *
     */
    @Parameter(required=true)
    private File file;
    
    
    /**
     * The form field name to POST the file to. This value depends entirely on
     * the server. Typically such a field is named "file". 
     * 
     * <p>
     * There's no requirement that this value is unique among the files to be 
     * uploaded.
     * 
     * <p>
     * If a value is not supplied then the names {@code file}, {@code file2},
     * {@code file3},... and so on, will be used.
     */
    @Parameter(required=true)
    private String formFieldName;
    
    
    /**
     * Mime type.
     * 
     * <p>This is the http <code>Content-Type</code> used when the file
     * is uploaded. If not given explicitly here it will be derived
     * from the file name extension using Java's 
     * {@link java.net.URLConnection#guessContentTypeFromName(java.lang.String)}
     * method and if this fails then <code>application/octet-stream</code>
     * will be used.
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

    
    
    
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getHintFilename() {
        return hintFilename;
    }

    public void setHintFilename(String hintFilename) {
        this.hintFilename = hintFilename;
    }

    public String getFormFieldName() {
        return formFieldName;
    }

    public void setFormFieldName(String formFieldName) {
        this.formFieldName = formFieldName;
    }

    
}
