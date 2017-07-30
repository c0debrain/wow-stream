/**
 * Copyright (C) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import boundary.MediaFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import models.MediaFile;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
//import ninja.appengine.AppEngineEnvironment;
import ninja.appengine.AppEngineFilter;
import ninja.params.PathParam;
import org.slf4j.Logger;
//import org.slf4j.Logger;

@Singleton
// Just a test to make sure @AppEngineEnvironment works.
// Usually @FilterWith(AppEngineFilter.class is much better.
//@AppEngineEnvironment 
@FilterWith(AppEngineFilter.class)
public class ApplicationController {

    @Inject
    MediaFacade mf;
    
    @Inject
    Logger logger;
    
    public ApplicationController() {

    }
    
    /**
     * Method to put initial data in the db...
     * @return
     */
    public Result setup() {
        //ObjectifyProvider.setup();
        return Results.ok();
        
    }

    public Result index() {
        List<MediaFile> files = mf.findRecent();
        return Results.html().render("files",files);
    }
    
    public Result videos() {
        List<MediaFile> files = mf.findAll();
        return Results.html().render("files",files);
    }
    
    public Result showVideo(@PathParam("id") Long id) {  
        MediaFile file = mf.find(id);
        return Results.html().render("file",file);
    }
    
    public Result music() {
        return Results.ok();
    }
}
