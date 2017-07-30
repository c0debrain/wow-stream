package controllers;

import boundary.MediaFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import control.StoreService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.MediaFile;
import ninja.Context;
import ninja.FilterWith;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.appengine.AppEngineFilter;
import ninja.params.Param;
import ninja.servlet.util.Request;
import ninja.servlet.util.Response;
import ninja.utils.ResponseStreams;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.slf4j.Logger;

@Singleton
@FilterWith(AppEngineFilter.class)
public class MediaController {

    @Inject
    MediaFacade mfacade;

    @Inject
    Logger logger;
    
    @Inject
    StoreService ss;

    public Result upload() {
        logger.info("this is info");
        MediaFile mf = new MediaFile();
        logger.debug("\n ### access upload section ####\n");
        return Results.html().render("mediaFile",mf);
    }

    public Result getFile(Context context) throws Exception {
        
        logger.debug("\n ############## get file ################ \n");
        String rpath = context.getRequestPath();
        rpath = rpath.replaceAll("\\/file\\/","");
        
        long fileLength = ss.getMetaData(rpath).getLength();
        long start=0;
        long contentLength = fileLength-1;
        long end = contentLength;
        String headerRange = context.getHeader("Range");
        
        logger.info("\n ### header range: {} ###\n",headerRange);
        
        if(headerRange!=null){
            
            String rangeValue = headerRange.trim().substring("bytes=".length());
            logger.debug("\n### header range {} #### \n",rangeValue);
            
            if (rangeValue.startsWith("-")) {
                end = fileLength - 1;
                start = fileLength - 1
                        - Long.parseLong(rangeValue.substring("-".length()));
            } else {
                String[] range = rangeValue.split("-");
                start = Long.parseLong(range[0]);
                end = range.length > 1 ? Long.parseLong(range[1])
                        : fileLength - 1;
                
                logger.debug("\n### Start {}, End {}",start,end);
                
                //end = end == 1 ? fileLength - 1 : end;
                
                
            }
            if (end > fileLength - 1) {
                end = fileLength - 1;
            }
            contentLength = end - start + 1;
        }else{
            logger.info("\n ### HEADER RANGE NULL #### \n");
        }
        
        logger.debug("\n### processing Start {}, End {}, Content-length {}",start,end,contentLength);
        
        context.setAttribute("offset",start);
        context.setAttribute("total",contentLength);
        
        Renderable renderable = new Renderable() { 
            @Override 
            public void render(Context context, Result result){ 
                String rpath = context.getRequestPath();
                Long offset = (Long)context.getAttribute("offset");
                Long total = (Long)context.getAttribute("total");
                
                logger.debug("\n ### offset {} #### \n",offset);
                rpath = rpath.replaceAll("\\/file\\/","");
                try {
                    ResponseStreams responseStreams = context
                            .finalizeHeaders(result);
                    
                    ss.getFile(rpath, responseStreams.getOutputStream(),offset,total.intValue());
                } catch (IOException ex) {
                    logger.debug("\n #### Exception sending file ##### ",ex);
                }
            }            
        };
        
        String filename = ss.getMetaData(rpath).getFilename().getObjectName().replaceFirst("videos///","");
        String etag = ss.getMetaData(rpath).getEtag();
        
        logger.debug("\n### meta data filename : {} , Etag: {}, LastMod: {}, "
                + "Length: {}, #### \n ",
                filename,
                etag,
                ss.getMetaData(rpath).getLastModified(),
                ss.getMetaData(rpath).getLength());
        
        //MessageDigest md = MessageDigest.getInstance("MD5");
        
        Result r = new Result(200);
        
        r = r.
                addHeader("Content-Type",ss.getMetaData(rpath).getOptions().getMimeType()).
                addHeader("cache-control","public, max-age=31536000").
                addHeader("Date",new Date().toGMTString()).
                addHeader("ETag",etag).
                addHeader("Last-Modified",ss.getMetaData(rpath).getLastModified().toGMTString());
        
        if(headerRange==null){
            r.status(200);
            logger.debug("\n ### return for null range ###\n");
            return r.render(renderable);
            
        }else{
            r.status(206);
        }
        
        logger.debug("\n ### return for rage ### \n");
        
        return r.
            addHeader("Accept-Ranges","bytes").
            addHeader("Content-Length", contentLength + "").
            addHeader("Content-Range", "bytes " + start + "-"+ end + "/" + fileLength).
            render(renderable);
    } 
    
    
    public Result processUpload(@Param("name") String name, 
            @Request HttpServletRequest httpServletRequest,
            @Response HttpServletResponse httpServletResponse,
            Context context) throws Exception {
        
        logger.info("### processing fileupload ####\n");
        logger.info("### movie name : {} ### ",name);
        logger.info("### request data : {}",httpServletRequest.getServletPath());
        logger.info("### headers {} ",context.getHeaders());
        logger.info("### parameters {} ",context.getParameters());
        
        // Make sure the context really is a multipart context...
        if (context.isMultipart()) {
            // This is the iterator we can use to iterate over the
            // contents of the request.
            FileItemIterator fileItemIterator = context
                    .getFileItemIterator();
           
            MediaFile mf = new MediaFile();
            while (fileItemIterator.hasNext()) {
                FileItemStream item = fileItemIterator.next();
                //String name = item.getFieldName();
                //InputStream stream = item.openStream();
                String contentType = item.getContentType();
                
                BufferedReader br = null;
                
                if (item.isFormField()) {
                    mf.setName(getStringFromInputStream(item.openStream()));
                    
                    logger.info("\n ### detect form field : {} #### \n",name);
                    logger.info("\n ### detect form field : {} #### \n", context.getParameter("name"));
                    //mf.setName(item.getFieldName());
                } else {
                    String fileName = item.getName().trim().replaceAll(" ","-");
                    logger.info("\n ### processing file name: {} ###\n", item.getName());
                    logger.info("\n ### processing file name: {} ###\n", fileName);
                    logger.info("\n ### content type {} ### \n ",contentType);
                    
                    if(contentType.contains("image")){
                        mf.setThumbPath("thumbs/"+fileName);
                        ss.saveFile(mf.getThumbPath(), contentType,item.openStream());
                    }else{
                        if(mf.getName()==null || mf.getName().isEmpty()){
                            mf.setName(fileName.replaceAll("\\.mp4","").
                                    replaceAll("[-_.]"," ").replaceAll("  "," ").trim());
                        }
                        mf.setFileName(fileName);
                        mf.setFilePath("videos/"+fileName);
                        ss.saveFile(mf.getFilePath(), contentType,item.openStream());
                    }
                    //MediaFile mf = new MediaFile(item.getName(), 
                      //      "videos/"+item.getName(),item.getContentType());
                    //mfacade.create(mf, item.openStream());
                }
            }
            mfacade.create(mf);
        }
        // We always return ok. You don't want to do that in production ;)
        //return Results.ok();
        return Results.redirect("/");
    }
    
    
    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
