package conf;

import com.google.inject.Provider;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import models.MediaFile;
import models.User;

public class ObjectifyProvider implements Provider<Objectify> {
    
    @Override
    public Objectify get() {
        return ObjectifyService.ofy();
    }

    static {

        ObjectifyService.register(User.class);
        ObjectifyService.register(MediaFile.class);

        //setup();
    }


    public static void setup() {

        Objectify ofy = ObjectifyService.ofy();
        User user = ofy.load().type(User.class).first().now();

        if (user == null) {

            // Create a new user and save it
            User bob = new User("bob@gmail.com", "secret", "Bob");
            ofy.save().entity(bob).now();

        }

    }

}
