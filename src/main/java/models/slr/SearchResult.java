package models.slr;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Index
public class SearchResult {
    
    @Id
    public Long id;
    public String author;
    public String title;
    public String cited;
    
    public SearchResult() {}
    
    public SearchResult(String title, String author, String cited) {
        this.author=author;
        this.title=title;
        this.cited=cited;
    }
 
}
