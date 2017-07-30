/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package boundary;

import com.google.inject.Inject;
import control.GenericCrudService;
import control.StoreService;
import java.io.IOException;
import java.util.List;
import models.MediaFile;

/**
 *
 * @author shahin
 */
public class MediaFacade {
    
    @Inject
    GenericCrudService gcs;
    
    @Inject
    StoreService ss;
    
    public MediaFile create(MediaFile m) throws IOException{
        return gcs.create(m);
    }
    
    public MediaFile find(Long id){
        return gcs.find(MediaFile.class, id);
    }
    
    public List<MediaFile> findAll(){
        return gcs.findAll(MediaFile.class);
    }
    
    public List<MediaFile> findRecent(){
        return gcs.findAll(MediaFile.class,0,8,"-createDate");
    }
}
