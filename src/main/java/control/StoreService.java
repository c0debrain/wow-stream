package control;

import com.google.appengine.tools.cloudstorage.GcsFileMetadata;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import java.io.BufferedInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

public class StoreService {

    public static final boolean SERVE_USING_BLOBSTORE_API = false;

    private static final int BUFFER_SIZE = 2 * 1024 * 1024;
    

    private static final String DEFAULT_BUCKET = "wow-stream.appspot.com";

    /**
     * This is where backoff parameters are configured. Here it is aggressively
     * retrying with backoff, up to 10 times but taking no more that 15 seconds
     * total to do so.
     */
    private static final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10)
            .retryMaxAttempts(50)
            .totalRetryPeriodMillis(15000)
            .build());

    public void saveFile(String path, String mimeType, InputStream input) throws IOException {
        
        GcsOutputChannel outputChannel
                = gcsService.createOrReplace(getFileName(path), 
                        new GcsFileOptions.Builder().mimeType(mimeType).build());

        BufferedInputStream bis = new BufferedInputStream(input);
        copy(bis, Channels.newOutputStream(outputChannel));
    }

    public InputStream getFile(String path) {
        GcsFilename fileName = getFileName(path);
        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
        return Channels.newInputStream(readChannel);
    }

    public GcsFileMetadata getMetaData(String path) throws IOException{
        GcsFilename fileName = getFileName(path);
        return gcsService.getMetadata(fileName);
    }
    
    public void getFile(String path, OutputStream out, long offset, int total) {
        GcsFilename fileName = getFileName(path);
        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, offset, BUFFER_SIZE);
        try {
            copy(Channels.newInputStream(readChannel),out,total);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * Use defulat baucket
     */
    private GcsFilename getFileName(String req) {
        return new GcsFilename(DEFAULT_BUCKET, req);
    }

    /**
     * Transfer the data from the inputStream to the outputStream. Then close
     * both streams.
     */
    private void copy(InputStream input, OutputStream output) throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = input.read(buffer);
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
        } finally {
            input.close();
            output.close();
        }
    }
    
    /**
     * Transfer the data from the inputStream to the outputStream. Then close
     * both streams.
     */
    private void copy(InputStream input, OutputStream output, int total) throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
                    
            int read;
            int toRead = total;
            
            while ((read = input.read(buffer)) > 0) {
                if ((toRead -= read) > 0) {
                    output.write(buffer, 0, read);
                } else {
                    output.write(buffer, 0, (int) toRead + read);
                    break;
                }
            }
        } finally {
            input.close();
            output.close();
        }
    }

}
