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

package conf;

import com.google.inject.Inject;
import controllers.ApplicationController;
import controllers.LoginLogoutController;
import controllers.MediaController;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;

public class Routes implements ApplicationRoutes {
    
    @Inject
    NinjaProperties ninjaProperties;

    /**
     * Using a (almost) nice DSL we can configure the router.
     * 
     * The second argument NinjaModuleDemoRouter contains all routes of a
     * submodule. By simply injecting it we activate the routes.
     * 
     * @param router
     *            The default router of this application
     */
    @Override
    public void init(Router router) {  
        
        // puts test data into db:
        if (!ninjaProperties.isProd()) {
            router.GET().route("/setup").with(ApplicationController.class, "setup");
        }
        
        ///////////////////////////////////////////////////////////////////////
        // Login / Logout
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/login").with(LoginLogoutController.class, "login");
        router.POST().route("/login").with(LoginLogoutController.class, "loginPost");
        router.GET().route("/logout").with(LoginLogoutController.class, "logout");
        

 
        ///////////////////////////////////////////////////////////////////////
        // Assets (pictures / javascript)
        ///////////////////////////////////////////////////////////////////////    
        router.GET().route("/assets/.*").with(AssetsController.class, "serve");
        //router.GET().route("/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");
        
        router.GET().route("/videos").with(ApplicationController.class, "videos");
        router.GET().route("/video/{id}").with(ApplicationController.class, "showVideo");
        
        router.GET().route("/music").with(ApplicationController.class, "music");
        router.GET().route("/upload").with(MediaController.class, "upload");
        router.POST().route("/processUpload").with(MediaController.class, "processUpload");
        router.GET().route("/file/.*").with(MediaController.class, "getFile");
        
        ///////////////////////////////////////////////////////////////////////
        // Index / Catchall shows index page
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/.*").with(ApplicationController.class, "index");
        
    }

}
